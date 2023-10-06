import examples.Example
import kotlinx.coroutines.runBlocking
import remote.Shu69Category
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

fun main() {

    runBlocking {
        val milli = measureTimeMillis {
            val experiment: HskExperiment = HskExperiment.Builder()
                .setWordLimit(100_000)
                .setBookLimit(100)
                .setChapterLimit(50)
                .setExport(true)
                .build()

            experiment.analyseTotalHskCoverageForSingleCategory(Shu69Category.WUXIA)
        }
        println("Completed in ${milli / 1000} sec")
    }
    exitProcess(64)

}



