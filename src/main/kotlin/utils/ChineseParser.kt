import com.hankcs.hanlp.HanLP
import com.hankcs.hanlp.tokenizer.SpeedTokenizer
import utils.Dictionary
import utils.HskDictionary

private const val TAG = "ChineseParser"

object ChineseParser {
    fun calculateHskCoverage(words: Collection<String>): Map<String, Int> {
        val hskCounts = mutableMapOf(
            "hsk1" to 0,
            "hsk2" to 0,
            "hsk3" to 0,
            "hsk4" to 0,
            "hsk5" to 0,
            "hsk6" to 0,
        )
        for (word in words) {
            val hsk = HskDictionary.search(word)
            hsk?.hsk?.also {
                if (hskCounts.keys.contains(it)) {
                    hskCounts[hsk.hsk] = (hskCounts[hsk.hsk] ?: 0) + 1
                }
            }
        }
        return hskCounts
    }

    fun convertSentencesToWordCounts(lines: List<String>): Map<String, Int> {
        val wordCounts: MutableMap<String, Int> = mutableMapOf()
        for (line in lines) {
            val words = SpeedTokenizer.segment(line).map { it.word }
            for (word in words) {
                if (containsHanzi(word)) {
                    wordCounts[word] = (wordCounts[word] ?: 0) + 1
                }
            }
        }
        return wordCounts.toMap()
    }

    /**
     * Segment a Chinese sentence into words. Characters not found in HSK words are split using
     * an algorithm that checks if our dictionary contains the split word.
     * split into individual characters. If the sentence has traditional chars, it will be
     * converted to simplified, since this is what our Dictionary, CWS-algorithm, HSK tagging,
     * and Comprehension calculation is based on.
     */

    fun segmentLines(lines: List<String>): List<String> {
        val words: MutableList<String> = ArrayList()
        for (line in lines) {
            val terms = SpeedTokenizer.segment(line)
            for (term in terms) {
                var currentWord: String = term.word
                if (!HskDictionary.contains(currentWord)) {
                    while (currentWord.isNotEmpty() &&
                        !Dictionary.contains(currentWord)
                    ) {
                        val splitChar = currentWord.substring(0, 1)
                        words.add(splitChar)
                        currentWord = currentWord.substring(1)
                    }
                }
                words.add(currentWord)
            }
        }
        return words
    }

    fun segmentSentence(line: String): List<String> {
        val words: MutableList<String> = ArrayList()
        val terms = SpeedTokenizer.segment(line)

        for (term in terms) {
            var currentWord: String = term.word
            if (!HskDictionary.contains(currentWord)) {
                while (currentWord.isNotEmpty() &&
                    !Dictionary.contains(currentWord)
                ) {
                    val splitChar = currentWord.substring(0, 1)
                    words.add(splitChar)
                    currentWord = currentWord.substring(1)
                }
            }
            words.add(currentWord)
        }
        return words
    }

    suspend fun getHskCoverageFromText(lines: List<String>): Map<String, Int> {
        val hskCounts = mutableMapOf(
            "hsk1" to 0,
            "hsk2" to 0,
            "hsk3" to 0,
            "hsk4" to 0,
            "hsk5" to 0,
            "hsk6" to 0,
        )
        val words: MutableSet<String> = mutableSetOf()
        var count = 0
        val terms = HanLP.segment(lines.joinToString("\n"))
        for (term in terms) {
            val currentWord: String = term.word
            val hsk = HskDictionary.search(currentWord)
            hsk?.hsk?.also {
                if (hskCounts.keys.contains(it) && !words.contains(currentWord)) {
                    words.add(currentWord)
                    hskCounts[it] = (hskCounts[it] ?: 0) + 1
                }
            }
            count++
        }
        return hskCounts
    }

    /**
     * Checks if the string contains Chinese characters
     */
    fun containsHanzi(word: String): Boolean {
        val chineseCharacterPattern = Regex("\\p{Script=Han}")
        return chineseCharacterPattern.containsMatchIn(word)
    }

}

