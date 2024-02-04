package domain.contracts

import data.model.HskWord

interface HskDictionary {
    val size : Int
    fun init()
    fun filterHsk(data: Collection<String>): Set<String>
    fun filterUnknownHsk(knownWords: Collection<String>): Set<String>
    fun search(word: String): HskWord?
    fun contains(word: String): Boolean
    fun getHskLevel(word: String): String?
    fun loadHskDictionary(): Map<String, HskWord>
}