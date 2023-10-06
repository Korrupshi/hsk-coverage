package data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * A data model for entries of CC-CEDICT chinese dictionary.
 * Each entry contains simplified, traditional, pinyin, and definition.
 */
data class DictEntry(
    @field:SerializedName("sim")
    val simplified: String,
    @field:SerializedName("tra")
    val traditional: String,
    @field:SerializedName("pin")
    val pinyin: String,
    @field:SerializedName("def")
    val definition: String,
    @field:SerializedName("hsk")
    val hsk: String
) : Serializable
