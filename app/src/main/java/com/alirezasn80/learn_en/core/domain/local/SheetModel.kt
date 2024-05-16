package com.alirezasn80.learn_en.core.domain.local

import com.alirezasn80.learn_en.utill.DictCategory

data class Define(
    val word: String,
    val synonyms: List<String>
)

data class Synonym(
    val type: String,
    val defines: List<Define>,
)

data class Translate(
    val main:String,
    val translate:String
)

data class Desc(
    val type: String,
    val texts: List<Translate>
)

data class SheetModel(
    val word: String,
    val define: String,
    val synonyms: List<Synonym>,
    val descriptions: List<Desc>,
    val examples: List<String>,
    val isHighlight: Boolean,
    val images: List<String> = emptyList(),
) {
    fun getCategories(): List<DictCategory> {
        // create categories
        val dictCategories = listOf<DictCategory>(DictCategory.Meaning).toMutableList()
        if (descriptions.isNotEmpty()) dictCategories.add(DictCategory.Desc)
        if (examples.isNotEmpty()) dictCategories.add(DictCategory.Example)
        return dictCategories.toList()
    }
}
