package com.alirezasn80.learn_en.feature.home

import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.core.domain.entity.CategoryEntity
import com.alirezasn80.learn_en.core.domain.entity.CategoryModel

sealed class Section(val name: Int) {
    data object All : Section(R.string.all)
    data object Favorite : Section(R.string.favorite)
    data object Created : Section(R.string.created)
}

val categories = listOf(
    Section.All,
    Section.Favorite,
    Section.Created,
)

data class HomeState(
    val selectedSection: Section = Section.All,
    val categories: List<CategoryModel> = emptyList(),

    )