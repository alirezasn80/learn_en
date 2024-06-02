package com.alirezasn80.learn_en.feature.home

import com.alirezasn80.learn_en.core.domain.remote.Category
import com.alirezasn80.learn_en.feature.stories.model.Book

sealed interface HomeDialogKey {
    data object AskRate : HomeDialogKey
    data object BadRate : HomeDialogKey
    data object Hide : HomeDialogKey
}

data class HomeState(
    val selectedTab: Tab = Tab.Default,
    val categories: List<Category> = emptyList(),
    val localCategories: List<Category> = emptyList(),
    val favorites: List<Book> = emptyList(),
    val dialogKey: HomeDialogKey = HomeDialogKey.Hide,
    val openAppCount: Int = 0,
    val needUpdate: Boolean = false,
    val showNotificationAlert: Boolean = true,
    val showComment: Boolean = true,
    val lastReadCategory: Int = 0,
)