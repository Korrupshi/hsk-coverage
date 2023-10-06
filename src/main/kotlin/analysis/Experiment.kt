import analysis.Analysis
import dictionary.HskDictionaryOld
import domain.contracts.HskDictionary
import remote.Shu69Category
import utils.FileUtils

data class CategoryBookCoverageResults(
    val category: String,
    val bookId: String,
    val coverage: Int,
    val additionalCoverage: Int,
)

class HskExperiment private constructor(
    private val wordLimit: Int,
    private val chapterLimit: Int,
    private val bookLimit: Int,
//    private val categoryBookLimit: Int,
    private val export: Boolean,
//    private val useNewHsk: Boolean,
    private val analysis: Analysis
) {
    // Builder class for analysis.Analysis
    class Builder {
        private var wordLimit = 150_000
        private var chapterLimit = 100
        private var bookLimit = 100
//        private var categoryBookLimit = 10
        private var export = false
        private var useNewHsk = false
        private var hskDictionary : HskDictionary = HskDictionaryOld

        fun setWordLimit(limit: Int): Builder {
            wordLimit = limit
            return this
        }
        fun useNewHsk(value: Boolean): Builder {
            useNewHsk = value
            if(useNewHsk) hskDictionary = HskDictionaryNew
            return this
        }


        fun setChapterLimit(limit: Int): Builder {
            chapterLimit = limit
            return this
        }

        fun setExport(shouldExport: Boolean): Builder {
            export = shouldExport
            return this
        }

        fun setBookLimit(limit: Int): Builder {
            bookLimit = limit
            return this
        }

//        fun setCategoryBookLimit(limit: Int): Builder {
//            categoryBookLimit = limit
//            return this
//        }

        // Build the analysis.Analysis object with the configured values
        fun build(): HskExperiment {
            return HskExperiment(
                wordLimit,
                chapterLimit,
                bookLimit,
//                categoryBookLimit,
                export,
//                useNewHsk,
                Analysis(hskDictionary)
            )
        }
    }

    /**
     * Per category give me x bookIds, get the total coverage for these
     * books. Total coverage meaning my already known Hsk words, including
     * any new hsk words the book gives me.
     */
    suspend fun analyseTotalHskCoverageForCategoryBooks(categories : Collection<Shu69Category>): List<CategoryBookCoverageResults> {
        var result = "category\tbookId\tcoverage\tadditional_coverage\tword_limit\n"
        val coverages: MutableList<CategoryBookCoverageResults> =
            mutableListOf()
        for (category in categories) {
//        for (category in Shu69Category.entries) {
            val bookIds = analysis.fetchBookIdsPerCategory(
                category,
                limit = bookLimit
            )
            for ((index, bookId) in bookIds.withIndex()) {
                println("${category.name}: ${index + 1}/${bookIds.size}")
                val textHskWords = analysis.fetchFullNovelHskWords(
                    bookId = bookId,
                    chapterLimit = chapterLimit,
                    wordLimit = wordLimit
                )
                val (coverage, addedCoverage) = analysis.getUnknownHskCoverage(
                    textHskWords
                )
                coverages.add(
                    CategoryBookCoverageResults(
                        category.name,
                        bookId,
                        coverage,
                        addedCoverage
                    )
                )
            }
        }
        val sortedResults = coverages.sortedByDescending { it.coverage }
        for ((category, bookId, coverage, addedCoverage) in sortedResults) {
            result += "$category\t$bookId\t$coverage\t$addedCoverage\t$wordLimit\n"
        }
        if (export) {
            for ((cat, bookId, coverage, addedCoverage) in sortedResults) {
                result += "$cat\t$bookId\t$coverage\t$addedCoverage\n"
            }
            FileUtils.writeTxtFile("results/bookTotalCoverageOldHsk.txt", result)
        }
        return sortedResults
    }


    /**
     * For given category give me x bookIds, get the total coverage for these
     * books. Total coverage meaning my already known Hsk words, including
     * any new hsk words the book gives me.
     */
    suspend fun analyseTotalHskCoverageForSingleCategory(category: Shu69Category): List<CategoryBookCoverageResults> {
        var result = "category\tbookId\tcoverage\tadditional_coverage\tword_limit\n"
        val coverages: MutableList<CategoryBookCoverageResults> =
            mutableListOf()
        val bookIds = analysis.fetchBookIdsPerCategory(
            category,
            limit = bookLimit
        )
        for ((i, bookId) in bookIds.withIndex()) {
            val textHskWords = analysis.fetchFullNovelHskWords(
                bookId = bookId,
                chapterLimit = chapterLimit,
                wordLimit = wordLimit
            )
            val (coverage, addedCoverage) = analysis.getUnknownHskCoverage(
                textHskWords
            )
            println("${i + 1}/${bookIds.size}")
            coverages.add(
                CategoryBookCoverageResults(
                    category.name,
                    bookId,
                    coverage,
                    addedCoverage
                )
            )
        }
        val sortedResults = coverages.sortedByDescending { it.coverage }

        if (export) {
            for ((cat, bookId, coverage, addedCoverage) in sortedResults) {
                result += "$cat\t$bookId\t$coverage\t$addedCoverage\t$wordLimit\n"
            }
            FileUtils.writeTxtFile(
                "results/${category.name.lowercase()}TotalCoverage.txt",
                result
            )
        }
        return sortedResults
    }
}
//object HskExperiment {
//
//    private const val CATEGORY_BOOK_LIMIT = 10
//    private const val BOOK_LIMIT = 100
//
//    data class categoryBookCoverageResults(
//        val category: String,
//        val bookId: String,
//        val coverage: Int,
//        val additionalCoverage: Int,
//    )
//
//    suspend fun analyseTotalHskCoverageForCategoryBooks() {
//        /**
//         * Per category give me 5 bookIds, get the total coverage for these
//         * books. Total coverage meaning my already known Hsk words, including
//         * any new hsk words the book gives me.
//         */
//        var result = "category\tbookId\tcoverage\tadditional_coverage\n"
//        val coverages: MutableList<categoryBookCoverageResults> =
//            mutableListOf()
//        for (category in Shu69Category.entries) {
//            val bookIds = analysis.analysis.fetchBookIdsPerCategory(
//                category,
//                limit = CATEGORY_BOOK_LIMIT
//            )
//            for (bookId in bookIds) {
//                val textHskWords = analysis.analysis.fetchFullNovelHskWords(bookId)
//                val (coverage, addedCoverage) = analysis.analysis.getUnknownHskCoverage(
//                    textHskWords
//                )
//                coverages.add(
//                    categoryBookCoverageResults(
//                        category.name,
//                        bookId,
//                        coverage,
//                        addedCoverage
//                    )
//                )
//            }
//        }
//        val sortedResults = coverages.sortedByDescending { it.coverage }
//        for ((category, bookId, coverage, addedCoverage) in sortedResults) {
//            result += "$category\t$bookId\t$coverage\t$addedCoverage\n"
//        }
////        FileUtils.writeTxtFile("results/bookTotalCoverage.txt", result)
//    }
//
//
//    suspend fun analyseTotalHskCoverageForCategory(category: Shu69Category) {
//        /**
//         * Per category give me 5 bookIds, get the total coverage for these
//         * books. Total coverage meaning my already known Hsk words, including
//         * any new hsk words the book gives me.
//         */
//        var result = "category\tbookId\tcoverage\tadditional_coverage\n"
//        val coverages: MutableList<categoryBookCoverageResults> =
//            mutableListOf()
//        val bookIds = analysis.analysis.fetchBookIdsPerCategory(
//            category,
//            limit = BOOK_LIMIT
//        )
//        for (bookId in bookIds) {
//            val textHskWords = analysis.analysis.fetchFullNovelHskWords(bookId)
//            val (coverage, addedCoverage) = analysis.analysis.getUnknownHskCoverage(
//                textHskWords
//            )
//            coverages.add(
//                categoryBookCoverageResults(
//                    category.name,
//                    bookId,
//                    coverage,
//                    addedCoverage
//                )
//            )
//        }
//        val sortedResults = coverages.sortedByDescending { it.coverage }
//        for ((cat, bookId, coverage, addedCoverage) in sortedResults) {
//            result += "$category\t$bookId\t$coverage\t$addedCoverage\n"
//        }
//        FileUtils.writeTxtFile("results/${category.name.lowercase()}TotalCoverage2.txt", result)
//    }
//}
//
//
