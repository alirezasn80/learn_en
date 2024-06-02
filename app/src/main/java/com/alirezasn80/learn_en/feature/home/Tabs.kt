package com.alirezasn80.learn_en.feature.home

import com.alirezasn80.learn_en.R

sealed class Tab(val name: Int, val key: String) {
    data object Default : Tab(R.string.stories, "default")
    data object Favorite : Tab(R.string.favorite, "favorite")
    data object Local : Tab(R.string.local, "local")
}

val tabs = listOf(
    Tab.Default,
    Tab.Favorite,
    Tab.Local,
)
