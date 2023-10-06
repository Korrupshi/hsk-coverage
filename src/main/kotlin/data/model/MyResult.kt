/**
 * A generic class that holds a value or an exception
 */
sealed class MyResult<out R> {
    data class Success<out T>(val data: T) : MyResult<T>()
    data class Failure(val exception: Exception) : MyResult<Nothing>()
}

/**
 * val successValue = resultSuccess.successOr("Default value for success")
 */
fun <T> MyResult<T>.successOr(fallback: T): T {
    return (this as? MyResult.Success<T>)?.data ?: fallback
}