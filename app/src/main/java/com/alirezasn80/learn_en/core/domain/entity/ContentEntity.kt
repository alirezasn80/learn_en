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
data class ContentEntity(
    @PrimaryKey(autoGenerate = true)
    val contentId: Int? = null,
    val categoryId: Int?,
    val title: String,
    val content: String,
    val translation: String?,
    val favorite: Int
)

data class Items(val categoryId: Int?, val contentId: Int?, val title: String)

fun ContentEntity.toItems() = Items(categoryId = categoryId, contentId = contentId, title = title)