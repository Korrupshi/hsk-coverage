package data.model

data class TextHskWords(
    val source: TextSource, // category or novel
    val id: String,  // category string or bookId
    val hskWords : Set<String>
)

enum class TextSource {
    WEB_NOVEL, CATEGORY
}
