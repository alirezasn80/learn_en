package com.alirezasn80.learn_en.feature.stories.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class Book(

    @SerializedName("book_id")
    val bookId: Int,

    @SerializedName("category_id")
    val categoryId: Int,

    @SerializedName("name")
    val name: String,
    val cover: String?,
    @SerializedName("file")
    val fileUrl: String,
    val type: String,
)

@Keep
data class BookResponse(
    @SerializedName("last_page")
    val lastPage: Int,
    @SerializedName("data")
    val books: List<Book>,
)