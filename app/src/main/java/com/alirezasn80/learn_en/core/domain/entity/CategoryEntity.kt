package com.alirezasn80.learn_en.core.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alirezasn80.learn_en.core.domain.remote.Category

@Entity
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int? = null,
    val title: String,
    val tag: String,
    val cover: String?,
)

fun CategoryEntity.toCategory() = Category(
    id = categoryId!!,
    name = title,
    cover = cover,
    tag = tag
)
