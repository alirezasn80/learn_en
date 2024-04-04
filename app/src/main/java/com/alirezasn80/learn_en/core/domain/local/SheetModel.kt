package com.alirezasn80.learn_en.core.domain.local

data class Define(
    val word: String,
    val synonyms: List<String>
)

data class Translation(
    val type: String,
    val defines: List<Define>,

    )

data class SheetModel(
    val define: String,
    val more: List<Translation>
)
