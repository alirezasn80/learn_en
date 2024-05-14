package com.alirezasn80.learn_en.core.domain.entity

import androidx.compose.ui.text.toLowerCase
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alirezasn80.learn_en.utill.debug


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

fun CategoryEntity.toCategoryModel(isCover: Boolean = false) =
    CategoryModel(
        id = categoryId!!,
        title = title,
        image = if (isCover) title.toLowerCase().chooseCover() else null
    )

//todo(work on it)
private fun String.chooseCover(): String {
    debug(this)
    return when {
        contains("akbar - birbal") -> "https://flyap.ir/seyed/cover/Akbar-birbal.webp"
        contains("aladdin") -> "https://flyap.ir/seyed/cover/Aladdin.webp"
        contains("animal") -> "https://flyap.ir/seyed/cover/animal.webp"
        contains("bedtime") -> "https://flyap.ir/seyed/cover/bedtime.webp"
        contains("classic") -> "https://flyap.ir/seyed/cover/classic.webp"
        contains("comical") -> "https://flyap.ir/seyed/cover/comical.webp"
        contains("default") -> "https://flyap.ir/seyed/cover/default.webp"
        contains("education") -> "https://flyap.ir/seyed/cover/education.webp"
        contains("fable") -> "https://flyap.ir/seyed/cover/fable.webp"
        contains("family") -> "https://flyap.ir/seyed/cover/family.webp"
        contains("general") -> "https://flyap.ir/seyed/cover/general.webp"
        contains("inspirational") -> "https://flyap.ir/seyed/cover/inspirational.webp"
        contains("kid") -> "https://flyap.ir/seyed/cover/kid.webp"
        contains("life") -> "https://flyap.ir/seyed/cover/life.webp"
        contains("love") -> "https://flyap.ir/seyed/cover/love.webp"
        contains("moral") -> "https://flyap.ir/seyed/cover/moral.webp"
        contains("motivation") -> "https://flyap.ir/seyed/cover/motivation.webp"
        contains("new") -> "https://flyap.ir/seyed/cover/new.webp"
        contains("other") -> "https://flyap.ir/seyed/cover/other.webp"
        contains("panchatantra") -> "https://flyap.ir/seyed/cover/panchatantra.webp"
        contains("proverbs") -> "https://flyap.ir/seyed/cover/proverbs.webp"
        contains("quotes") -> "https://flyap.ir/seyed/cover/quotes.webp"
        contains("short") -> "https://flyap.ir/seyed/cover/short.webp"
        else -> "https://flyap.ir/seyed/cover/default.webp"
    }
}
