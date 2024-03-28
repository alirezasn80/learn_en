package com.alirezasn80.learn_en.feature.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.app.navigation.NavigationState
import com.alirezasn80.learn_en.ui.common.PopUpMenu
import com.alirezasn80.learn_en.ui.common.UI
import com.alirezasn80.learn_en.ui.theme.SmallSpacer
import com.alirezasn80.learn_en.ui.theme.dimension
import com.alirezasn80.learn_en.utill.Rtl
import com.alirezasn80.learn_en.utill.debug
import com.google.mlkit.nl.translate.Translator

@Composable
fun ContentScreen(navigationState: NavigationState, viewModel: ContentViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    UI {
        Scaffold(
            topBar = {
                Header(
                    title = state.title,
                    isVisibleTranslate = state.isVisibleTranslate,
                    upPress = navigationState::upPress,
                    onTranslateClick = viewModel::onTranslateClick


                )
            },
            bottomBar = {
                BottomBar(
                    onSpeedClick = viewModel::onSpeedClick,
                    onPlayClick = viewModel::onPlayClick
                )
            }
        ) {
            LazyColumn(Modifier.padding(it)) {
                items(state.paragraphs) { paragraph ->

                    ParagraphSection(paragraph, state.isVisibleTranslate, viewModel::onWordClick)
                }
            }
        }
    }

}

@Composable
private fun BottomBar(onSpeedClick: (Float) -> Unit, onPlayClick: (Boolean) -> Unit) {
    var isPlay by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = dimension.extraSmall)
    ) {
        SpeedSection(onSpeedClick)
        Row(Modifier.align(Alignment.Center), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_backward), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            }
            SmallSpacer()
            IconButton(
                onClick = {
                    onPlayClick(isPlay)
                    isPlay = !isPlay
                }, modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Icon(imageVector = ImageVector.vectorResource(if (isPlay) R.drawable.ic_stop else R.drawable.ic_play), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            }
            SmallSpacer()
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_forward), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
private fun BoxScope.SpeedSection(onClick: (Float) -> Unit) {
    var selectedSpeed by remember {
        mutableStateOf("1x")
    }
    PopUpMenu(
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .padding(end = dimension.medium),
        mainIcon = selectedSpeed,
        titleMenuItems = listOf("0.5x", "0.75x", "1x", "1.5x", "2x"),
    ) {
        when (it) {
            0 -> {
                selectedSpeed = "0.5x"
                onClick(0.5f)
            }

            1 -> {
                selectedSpeed = "0.75x"
                onClick(0.75f)
            }

            2 -> {
                selectedSpeed = "1x"
                onClick(1f)
            }

            3 -> {
                selectedSpeed = "1.5x"
                onClick(1.5f)
            }

            4 -> {
                selectedSpeed = "2x"
                onClick(2f)
            }
        }
    }
}

@Composable
private fun ParagraphSection(
    paragraph: Paragraph,
    isVisibleTranslate: Boolean,
    onWordClick: (String) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(dimension.medium)
    ) {
        ClickableWordsText(
            paragraph.text, onClick = onWordClick
        )
        SmallSpacer()

        if (isVisibleTranslate)
            Rtl { Text(text = paragraph.translated, modifier = Modifier.fillMaxWidth()) }

    }
}

@Composable
private fun Header(title: String, isVisibleTranslate: Boolean, upPress: () -> Unit, onTranslateClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        IconButton(
            onClick = onTranslateClick,
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    if (isVisibleTranslate)
                        Color.White.copy(alpha = 0.3f)
                    else
                        Color.Unspecified
                )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_translate),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            text = title, modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically), textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleSmall
        )

        IconButton(onClick = upPress) {
            Icon(
                imageVector = Icons.Rounded.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
fun ClickableWordsText(text: String, onClick: (String) -> Unit) {
    val words = text.split(" ")
    val annotatedString = buildAnnotatedString {
        words.forEach { word ->
            val annotation = "clickable_word"
            pushStringAnnotation(tag = annotation, annotation = word)
            withStyle(
                style = SpanStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily(Font(R.font.vazir))
                )
            ) {
                append("$word ")
            }
            pop()
        }
    }

    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            // We retrieve the annotations at the click position
            annotatedString.getStringAnnotations(tag = "clickable_word", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    // Handle the click event on the word
                    onClick(annotation.item)
                }
        }
    )
}
