package com.alirezasn80.learn_en.feature.home

import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.core.domain.entity.CategoryModel
import com.alirezasn80.learn_en.core.domain.entity.Items

sealed class Section(val name: Int, val key: String) {
    data object Default : Section(R.string.stories, "default")
    data object Favorite : Section(R.string.favorite, "favorite")
    data object Created : Section(R.string.created, "created")
}

val sections = listOf(
    Section.Default,
    Section.Favorite,
    Section.Created,
)

sealed interface HomeDialogKey {
    data object AskRate : HomeDialogKey
    data object BadRate : HomeDialogKey
    data object Hide : HomeDialogKey
}

data class HomeState(
    val selectedSection: Section = Section.Default,
    val categories: List<CategoryModel> = emptyList(),
    val favorites: List<Items> = emptyList(),
    val dialogKey: HomeDialogKey = HomeDialogKey.Hide,
    val openAppCount: Int = 0,
    val needUpdate: Boolean = false,
    val showNotificationAlert: Boolean = true,
    val showComment: Boolean = true,
    val lastReadCategory:Int =0,
)