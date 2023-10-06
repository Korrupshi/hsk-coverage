import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.model.HskWord
import domain.contracts.HskDictionary
import java.io.*

object HskDictionaryNew : HskDictionary {
    private lateinit var hskDict: Map<String, HskWord>
    private const val HSK_WORD_COUNT: Int = 10943
    private val hskLevels: List<String> =
        listOf("hsk1", "hsk2", "hsk3", "hsk4", "hsk5", "hsk6", "hsk7-9")
    override val size: Int
        get() = TODO("Not yet implemented")


    override fun init() {
        TODO("Not yet implemented")
    }

    override fun filterHsk(data: Collection<String>): Set<String> {
        TODO("Not yet implemented")
    }

    override fun filterUnknownHsk(knownWords: Collection<String>): Set<String> {
        TODO("Not yet implemented")
    }

    override fun search(word: String): HskWord? {
        TODO("Not yet implemented")
    }

    override fun contains(word: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getHskLevel(word: String): String? {
        TODO("Not yet implemented")
    }

    override fun loadHskDictionary(): Map<String, HskWord> {
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
