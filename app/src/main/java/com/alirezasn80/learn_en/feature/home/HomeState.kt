package com.alirezasn80.learn_en.feature.home

import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.core.domain.entity.Items
import com.alirezasn80.learn_en.core.domain.remote.Category

sealed class Tab(val name: Int, val key: String) {
    data object Default : Tab(R.string.stories, "default")
    data object Favorite : Tab(R.string.favorite, "favorite")
   // data object Created : Section(R.string.created, "created")
}

val tabs = listOf(
    Tab.Default,
    Tab.Favorite,
    //Section.Created,
)

sealed interface HomeDialogKey {
    data object AskRate : HomeDialogKey
    data object BadRate : HomeDialogKey
    data object Hide : HomeDialogKey
}

data class HomeState(
    val selectedTab: Tab = Tab.Default,
    val categories: List<Category> = emptyList(),
    val favorites: List<Items> = emptyList(),
    val dialogKey: HomeDialogKey = HomeDialogKey.Hide,
    val openAppCount: Int = 0,
    val needUpdate: Boolean = false,
    val showNotificationAlert: Boolean = true,
    val showComment: Boolean = true,
    val lastReadCategory:Int =0,
)