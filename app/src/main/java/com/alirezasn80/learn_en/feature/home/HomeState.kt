package com.alirezasn80.learn_en.feature.home

import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.core.domain.entity.CategoryModel
import com.alirezasn80.learn_en.core.domain.entity.Items

sealed class Section(val name: Int, val key: String) {
    data object Default : Section(R.string.stories, "default")
    data object Favorite : Section(R.string.favorite, "favorite")
    data object Created : Section(R.string.documents, "created")
}

val sections = listOf(
    Section.Default,
    Section.Favorite,
    Section.Created,
)

data class HomeState(
    val selectedSection: Section = Section.Default,
    val categories: List<CategoryModel> = emptyList(),
    val favorites: List<Items> = emptyList(),
    )