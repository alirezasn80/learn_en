package com.alirezasn80.learn_en.feature.home

import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.core.domain.entity.CategoryEntity

sealed class Category(val name: Int) {
    data object All : Category(R.string.all)
    data object Favorite : Category(R.string.favorite)
    data object Created : Category(R.string.created)
}

val categories = listOf(
    Category.All,
    Category.Favorite,
    Category.Created,
)

data class HomeState(
    val selectedCategory: Category = Category.All,
    val categories: List<CategoryEntity> = emptyList(),

)