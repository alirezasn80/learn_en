package com.alirezasn80.learn_en.core.domain.local

data class Define(
    val word: String,
    val synonyms: List<String>
)

data class Synonym(
    val type: String,
    val defines: List<Define>,
)

data class Desc(
    val type: String,
    val texts: List<String>
)

data class SheetModel(
    val mainWord: String,
    val define: String,
    val synonyms: List<Synonym>,
    val descriptions: List<Desc>,
    val examples:List<String>,
    val isHighlight: Boolean,
    val images: List<String> = emptyList()
)
