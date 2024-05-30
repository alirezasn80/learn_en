package com.alirezasn80.learn_en.core.domain.remote

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RemoteModel(
    @SerializedName("success")
    val success: Boolean
)
