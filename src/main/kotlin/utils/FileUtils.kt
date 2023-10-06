package utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import data.model.TextHskWords
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

object FileUtils {

    fun exportTextHskWords(fileName: String, data : List<TextHskWords>){
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
    fun writeTxtFile(filePath : String, content : String) {

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