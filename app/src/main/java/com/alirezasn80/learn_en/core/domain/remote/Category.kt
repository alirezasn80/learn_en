package com.alirezasn80.learn_en.core.domain.remote

import androidx.annotation.Keep

@Keep
data class Category(
    val id:Int,
    val name:String,
    val cover:String?,
    val tag:String?
)
