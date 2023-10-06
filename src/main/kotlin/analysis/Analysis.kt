import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.model.TextHskWords
import data.model.TextSource
import kotlinx.coroutines.*
import remote.Shu69Category
import remote.Shu69Service
import utils.FileUtils
import utils.HskDictionary
import java.io.*


object Analysis {
//    private var WORD_LIMIT = 150_000
//    private var CHAPTER_LIMIT = 100
//    private var BOOK_LIMIT = 100
//    private var CATEGORY_BOOK_LIMIT = 10

//    fun setWordLimit(limit : Int){
//        this.WORD_LIMIT = limit
//    }

    // Builder class for Analysis
    fun getUnknownHskCoverage(data: TextHskWords): Pair<Int, Int> {
        val knownWords: Set<String> = KnownRepository.getKnownWords()
        val knownHsk = HskDictionary.filterHsk(knownWords)
        val totalHsk = knownHsk.plus(data.hskWords)
        val currentCoverage = calculateHskCoverage(knownHsk.size)
        val totalCoverage = calculateHskCoverage(totalHsk.size)
//        println("${data.id}: ${totalCoverage}% (+${totalCoverage - currentCoverage}%)")
        return totalCoverage to (totalCoverage - currentCoverage)
    }

    fun filterUnknownHsk(data: List<TextHskWords>) {
        val knownWords: Set<String> = KnownRepository.getKnownWords()
        val unknownHsk = HskDictionary.filterUnknownHsk(knownWords)
        val knownHsk = HskDictionary.filterHsk(knownWords)
        val current = (knownHsk.size * 100) / HskDictionary.hskCount

        println("Current coverage: $current% ${knownHsk.size} words")
        for (item in data) {
            val words: Set<String> = item.hskWords.subtract(knownWords)
            val coverage = (words.size * 100) / HskDictionary.hskCount
            println("${item.id}: $coverage% can be added")
        }
    }
//    fun genreUnknownHsk(data: List<TextHskWords>) {
//        val knownWords: Set<String> = KnownRepository.getKnownWords()
//        val unknownHsk = HskDictionary.filterUnknownHsk(knownWords)
//        println(unknownHsk.size)
//        val knownHsk = HskDictionary.filterHsk(knownWords)
//        val current = (knownHsk.size * 100) / HskDictionary.hskCount
//
//        println("Current coverage: $current% ${knownHsk.size} words")
//        for (item in data) {
//            val words: Set<String> = item.hskWords.subtract(knownWords)
//            val coverage = (words.size * 100) / HskDictionary.hskCount
//            println("${item.id}: $coverage% can be added")
//        }
//    }

    fun maxCategoryCoverage(data: List<TextHskWords>) {
        val hskWords = mutableSetOf<String>()
        for (item in data) {
            hskWords.addAll(item.hskWords)
        }

        val coverage = (hskWords.size * 100) / HskDictionary.hskCount
        println("Full genre: $coverage%")
    }

    fun calculateHskCoverage(hskWordCount: Int): Int {
        return (hskWordCount * 100) / HskDictionary.hskCount
    }

    fun categoryPairwiseCoverage(data: List<TextHskWords>): Pair<TextHskWords, TextHskWords>? {
        var maxUniqueWords = 0
        var maxCombination: Pair<TextHskWords, TextHskWords>? = null

        for (i in data.indices) {
            for (j in i + 1 until data.size) {
                val combination = data[i] to data[j]
                val uniqueWords = (data[i].hskWords + data[j].hskWords).size
                val coverage =
                    (uniqueWords * 100) / HskDictionary.hskCount

                if (uniqueWords > maxUniqueWords) {
//                    println("${data[i].id} + ${data[j].id}: ${coverage}% ")
                    maxUniqueWords = uniqueWords
                    maxCombination = combination
                }
            }
        }

        return maxCombination
    }

    suspend fun generateCategoryWords(): List<TextHskWords> {
        val allCategoryWords = mutableListOf<TextHskWords>()
        for (category in Shu69Category.entries) {
            val categoryWords = fetchCategoryHskWords(category)
            allCategoryWords.add(categoryWords)
        }
        FileUtils.exportTextHskWords("categoryWords", allCategoryWords)
        return allCategoryWords
    }

    fun importCategoryWords(): List<TextHskWords> {
        var data: List<TextHskWords> = emptyList()
        val fileName =
            "C:\\Users\\Stefa\\Desktop\\Main\\Coding\\02_Projects\\hsk-coverage\\results\\categoryWords.json"
        try {
            val inputStream: InputStream = File(fileName).inputStream()
            val bufferedReader =
                BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            bufferedReader.close()
            inputStream.close()
            val jsonContent = stringBuilder.toString()
            val gson = Gson()
            val dictItemType = object : TypeToken<List<TextHskWords>>() {}.type

            data = gson.fromJson(jsonContent, dictItemType)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return data
    }

    fun calculateCategoryHskCoverage(allWords: List<TextHskWords>): String {
        val coverages: MutableList<Pair<String, Int>> = mutableListOf()
        for (textHskWords in allWords) {
            // Get HSKCoverage%
            val hskCounts = textHskWords.hskWords.size
            val coverage = (hskCounts * 100) / HskDictionary.hskCount
            println("${textHskWords.id}: $coverage%")
            coverages.add(textHskWords.id to coverage)
        }

        var result = "category\thsk_percent\n"
        val sortedPairs = coverages.sortedByDescending { it.second }
        for ((category, value) in sortedPairs) {
            result += "$category\t$value\n"
        }
        return result
//        val filePath = "results/categoryCoverage.txt"
//        FileUtils.writeTxtFile(filePath, result)
    }

    fun exportTextHskWords(filePath: String, data: String) {
        FileUtils.writeTxtFile(filePath, data)
    }

    suspend fun fetchFullNovelHskWords(
        bookId: String,
        chapterLimit: Int,
        wordLimit: Int
    ): TextHskWords =
        coroutineScope {
            val allIds = Shu69Service.fetchChapterTable(bookId)
            val chapterIds =
                if (allIds.size < chapterLimit) allIds
                else allIds.subList(0, chapterLimit)
            var wordCount = 0
            val allWordsDeferredList = mutableListOf<Deferred<List<String>>>()
            for (chapterId in chapterIds) {
                val deferred = async(Dispatchers.IO) {
                    val result =
                        Shu69Service.fetchChapterTextRaw(bookId, chapterId)
                    if (result is MyResult.Failure) {
                        return@async emptyList()
                    }
                    result as MyResult.Success
                    val chapterLines: List<String> = result.data
                    val words = ChineseParser.segmentLines(chapterLines)
                    wordCount += words.size
                    return@async words
                }
                allWordsDeferredList.add(deferred)
            }

            val allWordsList: List<List<String>> =
                awaitAll(*allWordsDeferredList.toTypedArray())

            val allWords = allWordsList.flatten()
            val maxWords =
                if (allWords.size < wordLimit) allWords
                else allWords.subList(0, wordLimit)
            val hskWords = mutableSetOf<String>()
            for (word in maxWords.toSet()) {
                if (HskDictionary.contains(word)) {
                    hskWords.add(word)
                }
            }
            return@coroutineScope TextHskWords(
                source = TextSource.WEB_NOVEL,
                id = bookId,
                hskWords = hskWords
            )
        }

    suspend fun fetchBookIdsPerCategory(
        category: Shu69Category,
        limit: Int
    ): List<String> {
        return Shu69Service.fetchCategoryBookIds(category)
            .subList(0, limit)
    }

    private suspend fun fetchCategoryHskWords(category: Shu69Category): TextHskWords =
        coroutineScope {
            val discoverLimit = 100
            val bookIds = Shu69Service.fetchCategoryBookIds(category)
                .subList(0, discoverLimit)

            val allWordsDeferredList: List<Deferred<List<String>>> =
                bookIds.map { bookId ->
                    async(Dispatchers.IO) {
                        val chapterLines =
                            Shu69Service.fetchChapterLinesFromBookId(bookId)
                        ChineseParser.segmentLines(chapterLines)
                    }
                }

            val allWordsList: List<List<String>> =
                awaitAll(*allWordsDeferredList.toTypedArray())

            val allWordsSet = allWordsList.flatten<String>().toSet<String>()
            val hskWords = mutableSetOf<String>()
            for (word in allWordsSet) {
                if (HskDictionary.contains(word)) {
//                if (HskDictionary.containsHsk6(word)) {
                    hskWords.add(word)
                }
            }

            return@coroutineScope TextHskWords(
                source = TextSource.CATEGORY,
                id = category.name,
                hskWords = hskWords
            )
        }
}