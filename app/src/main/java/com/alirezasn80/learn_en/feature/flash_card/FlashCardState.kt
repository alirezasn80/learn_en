package com.alirezasn80.learn_en.feature.flash_card

import com.alirezasn80.learn_en.core.domain.local.SheetModel
import com.alirezasn80.learn_en.utill.DictCategory

data class FlashCardState(
    val flashcards: List<SheetModel>? = null,
    val selectedCategory: DictCategory = DictCategory.Meaning,
)