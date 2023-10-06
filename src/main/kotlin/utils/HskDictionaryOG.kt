package utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.model.HskWord
import java.io.*

object HskDictionaryOG {
    private const val HSK6_WORD_COUNT = 5324
    private const val HSK7_WORD_COUNT = 10943
    private var maxHsk6: Boolean = true
    var hskCount: Int = HSK6_WORD_COUNT
        private set

    private val hskWordCount = mapOf(
        "hsk1" to 462,
        "hsk2" to 734,
        "hsk3" to 952, // 2148 + 854 + 787 + 745 = 4534
        "hsk4" to 982,
        "hsk5" to 1064,
        "hsk6" to 1130, // 5324
        "hsk7-9" to 5619, // 10943
    )

    private val allHskLevels: List<String> =
        listOf("hsk1", "hsk2", "hsk3", "hsk4", "hsk5", "hsk6", "hsk7-9")

    private val hskLevels: List<String> =
        listOf("hsk1", "hsk2", "hsk3", "hsk4", "hsk5", "hsk6")

    private lateinit var hskDict: Map<String, HskWord>

    fun init(maxHsk6: Boolean) {
        hskDict = loadHskDictionary()
        this.maxHsk6 = maxHsk6
        if(!maxHsk6) hskCount = HSK7_WORD_COUNT
    }

    fun updateMaxHsk(maxHsk6: Boolean) {
        this.maxHsk6 = maxHsk6
        if(maxHsk6) hskCount = HSK6_WORD_COUNT else HSK7_WORD_COUNT
    }

    fun filterHsk(data: Collection<String>): Set<String> {
        val levels = if (maxHsk6) hskLevels else allHskLevels
        val known = mutableSetOf<String>()
        for (word in data) {
            val entry = hskDict[word]
            if (levels.contains(entry?.hsk)) {
                known.add(word)
            }
        }
        return known
    }

    fun filterUnknownHsk(knownWords: Collection<String>): Set<String> {
        val levels = if (maxHsk6) hskLevels else allHskLevels
        val hskWords =
            hskDict.filter { levels.contains(it.value.hsk) }.values.map { it.word }
        val dataHsk6 = filterHsk(knownWords)
        return hskWords.subtract(dataHsk6)
    }

    fun getTotalHskCount(): Int {
        return if (maxHsk6) HSK6_WORD_COUNT else HSK7_WORD_COUNT
    }

    fun search(word: String): HskWord? {
        return hskDict[word]
    }

    fun contains(word: String): Boolean {
        val levels = if (maxHsk6) hskLevels else allHskLevels
        val entry = hskDict[word]
        return levels.contains(entry?.hsk)
    }

    fun getHskLevel(word: String): String? {
        return hskDict[word]?.hsk
    }

    private fun loadHskDictionary(): Map<String, HskWord> {
        var dictionary: List<HskWord> = emptyList()
        val fileName =
            "C:\\Users\\Stefa\\Desktop\\Main\\Coding\\02_Projects\\hsk-coverage\\src\\main\\resources\\assets\\hskCache.json"

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
            val dictItemType = object : TypeToken<List<HskWord>>() {}.type

            dictionary = gson.fromJson(jsonContent, dictItemType)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return dictionary.associateBy { it.word }
    }
}