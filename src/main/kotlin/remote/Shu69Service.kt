package remote

import MyResult
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Response
import java.io.IOException
import java.nio.charset.Charset

object Shu69Service {
    private fun extractBookId(url: String?): String? {
        if (url == null) return null
        val pattern = Regex("/(\\d+)/(\\d+)$")
        val matchMyResult = pattern.find(url)
        return matchMyResult?.groupValues?.get(1)
    }

    suspend fun fetchCategoryBookIds(category: Shu69Category): List<String> {
        val result: MyResult<String> = handleApiCall {
            Shu69NetworkAPI.retrofit.fetchCategoryHtml(category.id)
        }
        return when (result) {
            is MyResult.Failure -> emptyList()
            is MyResult.Success -> extractCategoryBookIdList(result.data)
            else -> emptyList()
        }
    }

    private fun extractCategoryBookIdList(data: String): List<String> {
        val document: Document = Jsoup.parse(data)
        val bookElements = document
            .getElementById("article_list_content")
            ?.getElementsByTag("li")

        val novels = mutableListOf<String>()

        if (bookElements != null) {
            for (el in bookElements) {
                val href = el.select("a").attr("href")
                val temp = href.split("/").last()
                val bookId = temp.split(".").first()
                novels.add(bookId)
            }
        }
        return novels
    }

    suspend fun fetchFirstChapterId(bookId: String): String? {
        val result: MyResult<String> = handleApiCall {
            Shu69NetworkAPI.retrofit.fetchChapterTableHtml(bookId)
        }

        return when (result) {
            is MyResult.Failure -> null
            is MyResult.Success -> {
                val data = result.data
                val document = Jsoup.parse(data)
                val catalog = document.getElementById("catalog")
                val item = catalog?.getElementsByTag("li")?.get(0)
                val chapterUrl = item?.select("a")?.attr("href")
                extractChapterId(chapterUrl)
            }

            else -> null
        }
    }

    private fun extractChapterId(url: String?): String? {
        if (url == null) return null
        // Define a regular expression pattern to match the second set of numbers
        val pattern = Regex("/(\\d+)/(\\d+)$")
//        val pattern = Regex("/(\\d{5})/\\d+$")

        // Find the match in the URL
        val matchResult = pattern.find(url)

        // Extract and return the second set of numbers if found
        return matchResult?.groupValues?.get(2)
    }

    suspend fun fetchChapterLinesFromBookId(bookId: String): List<String> {
        val result = handleApiCall {
            Shu69NetworkAPI.retrofit.fetchChapterTableHtml(bookId)
        }
        when (result) {
            is MyResult.Failure -> return emptyList()
            is MyResult.Success -> {
                try {
                    val data = result.data
                    val document = Jsoup.parse(data)
                    val catalog = document.getElementById("catalog")
                    val item = catalog?.getElementsByTag("li")?.firstOrNull()
                    //                val item = catalog?.getElementsByTag("li")?.get(1)
                    val chapterUrl = item?.select("a")?.attr("href")
                    val chapterId = extractChapterId(chapterUrl)
                    if (chapterId != null) {
                        val res = fetchChapterTextRaw(bookId, chapterId)
                        return when (res) {
                            is MyResult.Failure -> emptyList()
                            is MyResult.Success -> res.data
                            else -> emptyList()
                        }
                    } else {
                        return emptyList()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    return emptyList()
                }
            }

            else -> return emptyList()
        }
    }
//
//    suspend fun fetchCategoryBookIds(category: Shu69Category): MyResult<List<String>> {
//        val MyResult = handleApiCall {
//            Shu69NetworkAPI.retrofit.fetchCategoryHtml(category.id)
//        }
//        if (MyResult is MyResult.Success) {
//            val novels = extractNovelsFromCategoryHtml(MyResult.data)
//
//            val tableRes = handleApiCall {
//                Shu69NetworkAPI.retrofit.fetchChapterTableHtml(novels[50].bookId!!)
//            }
//            if (tableRes is MyResult.Success) {
//                val data = tableRes.data
//                val document = Jsoup.parse(data)
//                val catalog = document.getElementById("catalog")
//                val item = catalog?.getElementsByTag("li")?.get(1)
//                val chapterUrl = item?.select("a")?.attr("href")
//                val bookId = extractBookId(chapterUrl)
//                val chapterId = extractChapterId(chapterUrl)
//                if (bookId != null && chapterId != null) {
//                    val res = fetchChapterTextRaw(bookId, chapterId)
//                    return when (res) {
//                        is MyResult.Error -> MyResult.Error(Exception("failed to get chapter text"))
//                        is MyResult.Success -> MyResult.Success(res.data)
//                    }
//                }
//            }
//        }
//        return MyResult.Error(Exception("failed to get chapter text"))
//    }

    suspend fun fetchChapterTextRaw(
        bookId: String,
        chapterId: String
    ): MyResult<List<String>> {
        val result = handleApiCall {
            Shu69NetworkAPI.retrofit.fetchChapterHtml(bookId, chapterId)
        }
        when (result) {
            is MyResult.Success -> {
                val raw: String = result.data
                val document: Document = Jsoup.parse(raw)
                val textElement = document.getElementsByClass("txtnav").first()
                val text = textElement?.text()

                val lines = text?.split("  ")

                if (lines != null) {
                    return MyResult.Success(lines.subList(1, lines.size))
                } else {
                    return MyResult.Failure(Exception("Emmpty"))
                }
            }

            is MyResult.Failure -> return MyResult.Failure(Exception("Failed to fetch novelChapter"))
            else -> return MyResult.Failure(Exception("Failed to fetch novelChapter"))
        }
    }

    private suspend fun handleApiCall(apiCall: suspend () -> Response<ResponseBody>): MyResult<String> {
//    private suspend fun <T> handleApiCall(apiCall: suspend () -> Response<T>): MyResult<String> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val data = convertBodyToEncodedString(response.body(), "GBK")
                if (data != null) {
                    MyResult.Success(data)
                } else {
                    MyResult.Failure(Exception("Response body is null"))
                }
            } else {
                MyResult.Failure(Exception("Response not successful, code: ${response.code()}"))
            }
        } catch (e: Exception) {
            MyResult.Failure(e)
        }
    }

    private fun convertBodyToEncodedString(
        body: ResponseBody?,
        charsetName: String
    ): String? {
        // Manually decode the response using the GBK charset
        if (body != null) {
            val charset = Charset.forName(charsetName)
            return body.source().use { source ->
                source.readString(charset)
            }
        }
        return null
    }

    suspend fun fetchChapterTable(bookId: String): List<String> {

        val result = handleApiCall {
            Shu69NetworkAPI.retrofit.fetchChapterTableHtml(bookId)
        }

        when (result) {
            is MyResult.Success -> {
                val raw: String = result.data
                val document: Document = Jsoup.parse(raw)

                val elements = document.getElementsByTag("li")
                val chapterIds = mutableListOf<String>()
                for (el in elements) {
                    val href = el.select("a").attr("href")
                    val chapterId = extractChapterId(href)
                    chapterId?.also {
                        chapterIds.add(chapterId)
                    }
                }

                return chapterIds
            }

            is MyResult.Failure -> return emptyList()
            else -> return emptyList()
        }
    }

    private fun extractChapterNumber(input: String): Int? {
        val regex = Regex("第(\\d+)章")

        val matchMyResult = regex.find(input)
        return matchMyResult?.groups?.get(1)?.value?.toIntOrNull()
    }


    private fun convertToBookId(url: String): String? {
        val base = url.split("www.69shuba.com/book/").lastOrNull()
        return base?.let {
            base.split(".")[0]
        }
    }

    private fun extractMetaProperties(htmlBody: String): Map<String, String> {
        val document: Document = Jsoup.parse(htmlBody)

        val metaProperties = mutableMapOf<String, String>()

        // Select all meta elements with the "property" attribute
        val metaElements = document.select("meta[property]")

        for (element in metaElements) {
            val property = element.attr("property")
            val content = element.attr("content")
            metaProperties[property] = content
        }

        return metaProperties
    }

    private suspend fun fetchHTMLFromUrl(url: String): MyResult<String> {
        try {
            val response = Shu69NetworkAPI.retrofit.fetchHtmlFromUrl(url)
            return if (response.isSuccessful) {
                val data: String? =
                    convertBodyToEncodedString(response.body(), "GBK")
                if (data != null) {
                    MyResult.Success(data)
                } else {
                    MyResult.Failure(Exception("Empty response body"))
                }
            } else {
                MyResult.Failure(Exception("API response failed"))
            }
        } catch (e: Exception) {
            if (e is IOException) {
                return MyResult.Failure(e)
            } else {
                return MyResult.Failure(e)
            }
        }
    }

    private suspend fun fetchChapterHtml(
        bookId: String,
        chapterId: String
    ): MyResult<String> {
        try {
            val response =
                Shu69NetworkAPI.retrofit.fetchChapterHtml(bookId, chapterId)
            return if (response.isSuccessful) {
                convertBodyToEncodedString(response.body(), "GBK")?.let {
                    MyResult.Success(it)
                } ?: MyResult.Failure(Exception("Empty response body"))
            } else {
                MyResult.Failure(Exception("API response failed"))
            }
        } catch (e: Exception) {
            if (e is IOException) {
                return MyResult.Failure(e)
            } else {
                return MyResult.Failure(e)
            }
        }
    }
}
