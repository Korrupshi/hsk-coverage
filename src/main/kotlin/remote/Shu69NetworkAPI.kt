package remote

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

/**
 * API to fetch YouTube channel image url
 */
enum class Shu69Category(val id: String) {
    FANTASY("1"),
    WUXIA("2"),
    ROMANCE("3"),
    HISTORY("4"),
    GAMING("5"),
    SCI_FI("6"),
    SUSPENSE("7"),
    FAN_FICTION("8"),
    CITY_LIFE("9"),
    CAREER("10"),
    YOUTH_LIFE("12"),
}

object Shu69NetworkAPI {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private const val baseUrl = "https://www.69shuba.com/"
    val retrofit: Shu69API by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .build()
            .create(Shu69API::class.java)
    }
}

interface Shu69API {
    // "https://www.69shuba.com/txt/$bookId/$chapterId"
    @GET("txt/{bookId}/{chapterId}")
    suspend fun fetchChapterHtml(
        @Path("bookId") bookId: String,
        @Path("chapterId") chapterId: String
    ): Response<ResponseBody>

    // https://www.69shuba.com/book/$bookId.htm
    @GET("book/{bookId}.htm")
    suspend fun fetchBiblioHtml(@Path("bookId") bookId: String): Response<ResponseBody>

    // https://www.69shuba.com/book/$bookId/
    @GET("book/{bookId}/")
    suspend fun fetchChapterTableHtml(@Path("bookId") bookId: String): Response<ResponseBody>

    @GET
    suspend fun fetchHtmlFromUrl(@Url url: String): Response<ResponseBody>

    // https://www.69shuba.com/novels/class/$categoryId.htm
    @GET("novels/class/{categoryId}.htm")
    suspend fun fetchCategoryHtml(@Path("categoryId") categoryId: String): Response<ResponseBody>
}
