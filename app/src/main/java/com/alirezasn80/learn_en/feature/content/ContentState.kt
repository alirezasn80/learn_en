package com.alirezasn80.learn_en.feature.content


import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.core.domain.local.SheetModel
import com.alirezasn80.learn_en.utill.DictCategory

data class Paragraph(
    val text: String,
    val translated: String
)

data class ContentState(
    val isVisibleTranslate: Boolean = false,
    val title: String = "",
    val paragraphs: List<Paragraph> = emptyList(),
    val highlights: List<String> = emptyList(),
    val readableIndex: Int = 0,
    val isPlay: Boolean = false,
    val isMute: Boolean = false,
    val isBookmark: Boolean = false,
    val sheetModel: SheetModel? = null,
    val selectedCategory: DictCategory = DictCategory.Meaning,
    val selectedFontFamily: Int = R.font.helvetica,
    val selectedFontSize: Int = 14
)
