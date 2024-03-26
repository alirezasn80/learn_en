package com.alirezasn80.learn_en.ui.common

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun HyperText(
    modifier: Modifier = Modifier,
    fullText: String,
    linkText: List<String>,
    linkTextColor: Color = MaterialTheme.colorScheme.primary,
    linkTextDecoration: TextDecoration = TextDecoration.None,
    onClick: () -> Unit,
) {
    val annotatedString = buildAnnotatedString {
        append(fullText)
        linkText.forEachIndexed { _, link ->
            val startIndex = fullText.indexOf(link)
            val endIndex = startIndex + link.length
            addStyle(
                style = SpanStyle(
                    color = linkTextColor,
                    textDecoration = linkTextDecoration
                ),
                start = startIndex,
                end = endIndex
            )
        }
        addStyle(
            style = SpanStyle(),
            start = 0,
            end = fullText.length
        )
    }
    ClickableText(
        modifier = modifier,
        text = annotatedString,
        onClick = {
            onClick()
        },
        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
    )
}