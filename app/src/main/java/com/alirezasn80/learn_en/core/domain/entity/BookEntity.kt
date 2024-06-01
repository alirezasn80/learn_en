package com.alirezasn80.learn_en.core.domain.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    val title: String,
    val bookPath: String,
    val translationPath: String?,
    val isFavorite: Int
)