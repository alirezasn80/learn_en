package com.alirezasn80.learn_en.feature.create

import androidx.compose.ui.text.input.TextFieldValue
import com.alirezasn80.learn_en.core.domain.entity.CategoryModel

data class CreateState(
    val createdCategories:List<CategoryModel> = emptyList(),
    val title: String = "",
    val content: TextFieldValue = TextFieldValue(),

    )