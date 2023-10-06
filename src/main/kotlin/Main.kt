import kotlinx.coroutines.*
import remote.Shu69Category
import utils.Dictionary
import utils.HskDictionary
import kotlin.system.exitProcess

fun main() {
    Dictionary.init()
    HskDictionary.init(true)
    KnownRepository.init()
    runBlocking {
        val experiment: HskExperiment = HskExperiment.Builder()
            .setWordLimit(100_000)
            .setCategoryBookLimit(10)
            .setExport(true)
            .build()

        experiment.analyseTotalHskCoverageForCategory(Shu69Category.SCI_FI)
//        experiment.analyseTotalHskCoverageForCategoryBooks()


            /**
             * Get coverage for webnovel.
             * Per category give me 10 bookIds
             */
//        Analysis.fetchFullNovelHskWords("28826")
//        HskAnalysis.analyseTotalHskCoverageForCategoryBooks()
//        HskExperiment.analyseTotalHskCoverageForCategory(Shu69Category.GAMING)
//        val textHskWords = Analysis.fetchFullNovelHskWords("49055")
//        val (coverage, addedCoverage) = Analysis.getUnknownHskCoverage(
//            textHskWords
//        )
//        println("$coverage% (+$addedCoverage%)")
//        var result = "category\tbookId\tcoverage\n"
//        val coverages: MutableList<Triple<String, String, Int>> =
//            mutableListOf()
//        for (category in Shu69Category.entries) {
//            val bookIds = Analysis.fetchBookIdsPerCategory(category, limit = 5)
//            for (bookId in bookIds) {
//                val textHskWords = Analysis.fetchFullNovelHskWords(bookId)
//                val coverage = Analysis.getUnknownHskCoverage(textHskWords)
//                coverages.add(Triple(category.name, bookId, coverage))
//            }
//        }
//        val sortedTriple = coverages.sortedByDescending { it.third }
//        for ((category, bookId, coverage) in sortedTriple) {
//            result += "$category\t$bookId\t$coverage\n"
//        }
//        FileUtils.writeTxtFile("results/bookTotalCoverage.txt", result)


            // 1. Generate or import CategoryWords
//        val bookIds = listOf(
//            "50913",
//            "46957", // wuxia
//            "30435",
//            "42525", // 6% history
//            "32907", // 6% history
//        )
//        val bookId = "28826"
//        val bookId = "46957"
//        var result = "bookId\tcoverage\n"
//        val coverages: MutableList<Pair<String, Int>> = mutableListOf()
//
//        for (bookId in bookIds) {
//            val textHskWords = Analysis.fetchFullNovelHskWords(bookId)
//            val coverage = Analysis.getUnknownHskCoverage(textHskWords)
//            coverages.add(bookId to coverage)
//        }
//        val sortedPairs = coverages.sortedByDescending { it.second }
//        for ((bookId, coverage) in sortedPairs) {
//            result += "$bookId\t$coverage\n"
//        }

//        FileUtils.writeTxtFile("results/bookTotalCoverage.txt",result)

            /**
             * Get coverage for each category
             */
            // 1. Generate or import CategoryWords
//        val allCategoryWords = Analysis.generateCategoryWords()
//        val allCategoryWords = Analysis.importCategoryWords()

            // 2. Analyse Category words
//        Analysis.categoryPairwiseCoverage(allCategoryWords)
//        Analysis.genreUnknownHsk(allCategoryWords)
//        Analysis.maxCategoryCoverage(allCategoryWords)
//        Analysis.calculateCategoryCoverage(allCategoryWords)

        println("End..")
        exitProcess(64)
    }
}



