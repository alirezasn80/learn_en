package com.alirezasn80.learn_en.feature.create

import androidx.compose.ui.text.input.TextFieldValue
import com.alirezasn80.learn_en.core.domain.remote.Category

data class CreateState(
    val createdCategories:List<Category> = emptyList(),
    val title: String = "",
    val content: TextFieldValue = TextFieldValue(),

    )