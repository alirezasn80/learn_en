package com.alirezasn80.learn_en.core.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val categoryId: Int? = null,
    val title: String,
    val type:String
)