package com.alirezasn80.learn_en.core.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class WordEntity(
    @PrimaryKey
    val word: String,
    val definition: String
)