package com.alirezasn80.learn_en.core.domain.remote

import androidx.annotation.Keep


@Keep
data class Response(
    val success: Boolean,
    val message: String,
    val code: Int,
)