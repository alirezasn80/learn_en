package com.alirezasn80.learn_en.feature.stories


import com.alirezasn80.learn_en.feature.stories.model.Book

data class StoriesState(
    val title: String = "",
    val isLastReadStory: Int = 0,
    val page: Int = 1,
    val totalPage: Int = 1,
    val localBooks: List<Book> = emptyList()
)