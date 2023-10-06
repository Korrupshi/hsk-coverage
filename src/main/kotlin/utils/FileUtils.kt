package utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import data.model.HskWord
import data.model.TextHskWords
import java.io.*
import java.util.zip.GZIPOutputStream

object FileUtils {

    fun loadFileAsText(fileName: String): String {
        val stringBuilder = StringBuilder()
        val filePath =
            "C:\\Users\\Stefa\\Desktop\\Main\\Coding\\02_Projects\\hsk-coverage\\src\\main\\resources\\assets\\$fileName"

        try {
            val inputStream: InputStream = File(filePath).inputStream()
            val bufferedReader =
                BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append("$line\n")
            }
            bufferedReader.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

    fun exportHskWordJson(fileName: String, data: List<HskWord>) {
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()

        try {
            // Convert data to JSON and write it to a JSON file
            val json = gson.toJson(data)
            val filePath =
                "C:\\Users\\Stefa\\Desktop\\Main\\Coding\\02_Projects\\hsk-coverage\\src\\main\\resources\\assets\\$fileName.json.bin"

            val outputStream = FileOutputStream(filePath)
            val bufferedOutputStream = BufferedOutputStream(outputStream)
            val gzipOutputStream = GZIPOutputStream(bufferedOutputStream)

            gzipOutputStream.write(json.toByteArray(Charsets.UTF_8))
            gzipOutputStream.close()
//            File(filePath).writeText(json)
            println("Data has been written.")
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }
    }

    fun exportTextHskWords(fileName: String, data: List<TextHskWords>) {
//        val gson = Gson()
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()

        try {
            // Convert data to JSON and write it to a JSON file
            val json = gson.toJson(data)
            val filePath = "results/$fileName.json"

            File(filePath).writeText(json)
            println("Data has been written.")
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }
    }

    fun writeTxtFile(filePath: String, content: String) {
        try {
            // Create a File object representing the file
            val outputFile = File(filePath)

            // Initialize a FileWriter and BufferedWriter to write to the file
            val fileWriter = FileWriter(outputFile)
            val bufferedWriter = BufferedWriter(fileWriter)

            // Write data to the file
            bufferedWriter.write(content)

            // Close the BufferedWriter to flush and close the file
            bufferedWriter.close()

            println("Data written to $filePath successfully.")
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }
    }
}