package com.alirezasn80.learn_en.feature.create

import androidx.compose.ui.text.input.TextFieldValue

data class CreateState(
    val title: String = "",
    val content: TextFieldValue = TextFieldValue(),

    )