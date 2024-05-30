package com.alirezasn80.learn_en.feature.stories

import com.alirezasn80.learn_en.core.domain.entity.Items
import com.alirezasn80.learn_en.feature.stories.model.Book
import com.alirezasn80.learn_en.utill.Progress

data class StoriesState(
    val title: String = "",
    val items: List<Items> = emptyList(),
    val isLastReadStory: Int = 0,


    val page: Int = 1,
    val totalPage: Int = 1,
    val tickets: List<Book> = emptyList(),
)