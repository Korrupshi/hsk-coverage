package utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.model.DictEntry
import java.io.*

object Dictionary {
    private lateinit var mainDict: Map<String, DictEntry>
    fun init() {
        mainDict = loadJsonDictionary()
    }

    fun search(word: String): DictEntry? {
        return mainDict[word]
    }

    fun contains(word: String): Boolean {
        return mainDict.contains(word)
    }

    private fun loadJsonDictionary(): Map<String, DictEntry> {
        var dictionary: List<DictEntry> = emptyList()
        val fileName = "src\\main\\resources\\assets\\dictionary.json"
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
            val dictItemType = object : TypeToken<List<DictEntry>>() {}.type

            dictionary = gson.fromJson(jsonContent, dictItemType)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return dictionary.associateBy { it.simplified }
    }

}