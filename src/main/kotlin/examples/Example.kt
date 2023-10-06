package examples

import HskExperiment
import data.model.HskWord
import remote.HskService
import remote.Shu69Category
import utils.FileUtils

object Example {
    /**
     * Analyse the total hsk coverage for books in a specific category
     */
    suspend fun analyseTotalHskCoverageForCategory() {
//        Dictionary.init()
//        repository.KnownRepository.init()
        val experiment: HskExperiment = HskExperiment.Builder()
            .setWordLimit(100_000)
            .setBookLimit(1)
            .setExport(true)
            .build()

        experiment.analyseTotalHskCoverageForCategoryBooks(Shu69Category.entries)
    }

    /**
     * Fetch old hsk vocabulary from a website and export as a json file
     * [List<HskWord>]
     */
    suspend fun fetchAndExportHskWordsOld() {
        val allHsk = mutableSetOf<String>()
        val output = mutableListOf<HskWord>()
        for (i in 1..6) {
            val words = HskService.fetchHskWordsOld(i)
            for (word in words) {
                val level = "hsk$i"
                if (!allHsk.contains(word)) {
                    allHsk.add(word)
                    val hskWord = HskWord(word = word, hsk = level)
                    output.add(hskWord)
                }
            }
            println("HSK$i: ${output.size}")
        }
        //        FileUtils.exportHskWordJson("hskOld2.json", output)

    }

    /**
     * Load hsk csv files from assets and convert to a json file for old Hsk
     * words
     */

    fun readAndExportHskWordsOld() {
        val allHsk = mutableSetOf<String>()
        val output = mutableListOf<HskWord>()
        for (i in 1..6) {
            val data = FileUtils.loadFileAsText("hsk_$i.csv")
            val lines = data.split("\n")
            lines.subList(1, lines.size).forEach { line ->
                line.split(",").getOrNull(1)?.also { word ->
                    val hsk = word.split("（").first()
                    val level = "hsk$i"
                    if (!allHsk.contains(hsk)) {
                        allHsk.add(hsk)
                        val hskWord = HskWord(word = hsk, hsk = level)
                        output.add(hskWord)
                    }
                }
            }
            println("HSK$i: ${output.size}")
        }
        FileUtils.exportHskWordJson("hskOld", output)
    }
}