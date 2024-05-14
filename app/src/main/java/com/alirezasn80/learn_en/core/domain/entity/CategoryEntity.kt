package com.alirezasn80.learn_en.core.domain.entity

import androidx.compose.ui.text.toLowerCase
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val categoryId: Int? = null,
    val title: String,
    val type: String
)

data class CategoryModel(
    val id: Int,
    val title: String,
    val image: String? = null
)

fun CategoryEntity.toCategoryModel(isCover: Boolean = false) = CategoryModel(
    id = categoryId!!,
    title = title,
    image = if (isCover) title.toLowerCase().chooseCover() else null
)

//todo(work on it)
private fun String.chooseCover(): String {
    return when {
        contains("akbar-birbal") -> "cover/Akbar-birbal.webp"
        contains("aladdin") -> "cover/Aladdin.webp"
        contains("animal") -> "cover/animal.webp"
        contains("bedtime") -> "cover/bedtime.webp"
        contains("classic") -> "cover/classic.webp"
        contains("comical") -> "cover/comical.webp"
        contains("default") -> "cover/default.webp"
        contains("education") -> "cover/education.webp"
        contains("fable") -> "cover/fable.webp"
        contains("family") -> "cover/family.webp"
        contains("general") -> "cover/general.webp"
        contains("inspirational") -> "cover/inspirational.webp"
        contains("kid") -> "cover/kid.webp"
        contains("life") -> "cover/life.webp"
        contains("love") -> "cover/love.webp"
        contains("moral") -> "cover/moral.webp"
        contains("motivation") -> "cover/motivation.webp"
        contains("new") -> "cover/new.webp"
        contains("other") -> "cover/other.webp"
        contains("panchatantra") -> "cover/panchatantra.webp"
        contains("proverbs") -> "cover/proverbs.webp"
        contains("quotes") -> "cover/quotes.webp"
        contains("short") -> "https://covers.storytel.com/jpg-640/9781667970233.6655babc-1c48-4bce-aa76-0431f44eddfe?quality=70"
        else -> "cover/default.webp"
    }
}
