package com.alirezasn80.learn_en.feature.stories

import com.alirezasn80.learn_en.core.domain.entity.Items

data class StoriesState(
    val title: String = "",
    val items: List<Items> = emptyList(),
    val isLastReadStory:Int = 0,
)
