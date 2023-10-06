package repository

import java.io.File
import java.io.InputStream

object KnownRepository {
    private lateinit var knownWords: Set<String>
    fun init() {
        knownWords = readKnownTxtFile()
    }

    fun getKnownWords(): Set<String> {
        return knownWords
    }

    fun contains(word: String): Boolean {
        return knownWords.contains(word)
    }

    private fun readKnownTxtFile(): Set<String> {
        val fileName =
            "C:\\Users\\Stefa\\Desktop\\Main\\Coding\\02_Projects\\hsk-coverage\\src\\main\\resources\\assets\\known_words.txt"

//        val bufferedReader: BufferedReader = File(fileName).bufferedReader()
//        val inputString = bufferedReader.use { it.readText() }

        val inputStream: InputStream = File(fileName).inputStream()
//        val inputString = inputStream.bufferedReader().use { it.readText() }

        val lineList = mutableListOf<String>()

        inputStream.bufferedReader().forEachLine { lineList.add(it) }

        return lineList.toSet()
//        return inputString.split("\n").toSet()

    }
}