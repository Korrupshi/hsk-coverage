package dictionary

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.model.HskWord
import domain.contracts.HskDictionary
import java.io.*

object HskDictionaryOld : HskDictionary {
    private lateinit var hskDict: Map<String, HskWord>
    private val hskLevels: List<String>
        get() = listOf("hsk1", "hsk2", "hsk3", "hsk4", "hsk5", "hsk6")
    override var size: Int = 0
        private set

    override fun init() {
        this.hskDict = loadHskDictionary()
        this.size = hskDict.size
    }

    override fun filterHsk(data: Collection<String>): Set<String> {
        val known = mutableSetOf<String>()
        for (word in data) {
            val entry = hskDict[word]
            if (hskLevels.contains(entry?.hsk)) {
                known.add(word)
            }
        }
        return known
    }

    override fun filterUnknownHsk(knownWords: Collection<String>): Set<String> {
        val hskWords =
            hskDict.filter { hskLevels.contains(it.value.hsk) }.values.map { it.word }
        val dataHsk6 = filterHsk(knownWords)
        return hskWords.subtract(dataHsk6)
    }


    override fun search(word: String): HskWord? {
        return hskDict[word]
    }

    override fun contains(word: String): Boolean {
        return hskDict.contains(word)
    }

    override fun getHskLevel(word: String): String? {
        return hskDict[word]?.hsk
    }

    override fun loadHskDictionary(): Map<String, HskWord> {
        var dictionary: List<HskWord> = emptyList()
        val fileName =
            "C:\\Users\\Stefa\\Desktop\\Main\\Coding\\02_Projects\\hsk-coverage\\src\\main\\resources\\assets\\hskOld.json"

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