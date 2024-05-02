package com.alirezasn80.learn_en.core.domain.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["word"],
            childColumns = ["word"]
        )
    ]
)
data class WordImgEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val word: String,
    val url: String
)