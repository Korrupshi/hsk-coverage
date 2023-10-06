package remote

import MyResult
import data.model.HskWord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Response

object HskService {


    suspend fun fetchHskWordsOld(hskLevel: Int): List<String> = withContext(
        Dispatchers.IO) {
        val url = "https://hewgill.com/hsk/hsk$hskLevel.html"
        val result = handleApiCall {
            Shu69NetworkAPI.retrofit.fetchHtmlFromUrl(url)
        }
        return@withContext when (result) {
            is MyResult.Failure -> emptyList()
            is MyResult.Success -> extractHskWordsFromHtml(result.data)
            else -> emptyList()
        }
    }

    private fun extractHskWordsFromHtml(data: String): List<String> {
        val words = mutableListOf<String>()
        val document: Document = Jsoup.parse(data)
        val wordElements = document
            .getElementsByClass("char")

        for (el in wordElements) {
            words.add(el.text())
        }

        return words
    }

    private suspend fun handleApiCall(apiCall: suspend () -> Response<ResponseBody>): MyResult<String> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val data = response.body()?.string()
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
}