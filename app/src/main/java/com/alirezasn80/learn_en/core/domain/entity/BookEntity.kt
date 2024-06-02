package com.alirezasn80.learn_en.core.domain.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.alirezasn80.learn_en.feature.stories.model.Book

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val bookId: Int? = null,
    val categoryId: Int?,
    val name: String,
    val cover: String?,
    val isFavorite: Int,
    val type: String,
    val fileUrl: String,
)

fun BookEntity.toBook() = Book(
    bookId = this.bookId!!,
    categoryId = categoryId!!,
    name = this.name,
    cover = this.cover,
    type = this.type,
    fileUrl = fileUrl
)

fun Book.toBookEntity(isFavorite: Int) = BookEntity(
    bookId = bookId,
    categoryId = categoryId,
    name = name,
    cover = cover,
    isFavorite = isFavorite,
    type = type,
    fileUrl = fileUrl

)

