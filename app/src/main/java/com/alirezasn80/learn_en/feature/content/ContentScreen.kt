package com.alirezasn80.learn_en.feature.content

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.app.navigation.NavigationState
import com.alirezasn80.learn_en.core.domain.local.Desc
import com.alirezasn80.learn_en.core.domain.local.SheetModel
import com.alirezasn80.learn_en.ui.common.PopUpMenu
import com.alirezasn80.learn_en.ui.common.UI
import com.alirezasn80.learn_en.ui.common.shimmerEffect
import com.alirezasn80.learn_en.ui.theme.ExitRed
import com.alirezasn80.learn_en.ui.theme.ExtraSmallSpacer
import com.alirezasn80.learn_en.ui.theme.Line
import com.alirezasn80.learn_en.ui.theme.Red100
import com.alirezasn80.learn_en.ui.theme.SmallSpacer
import com.alirezasn80.learn_en.ui.theme.dimension
import com.alirezasn80.learn_en.ui.theme.highlighterColor
import com.alirezasn80.learn_en.utill.Destination
import com.alirezasn80.learn_en.utill.DictCategory
import com.alirezasn80.learn_en.utill.LoadingKey
import com.alirezasn80.learn_en.utill.Ltr
import com.alirezasn80.learn_en.utill.Progress
import com.alirezasn80.learn_en.utill.Rtl
import com.alirezasn80.learn_en.utill.User
import com.alirezasn80.learn_en.utill.cleanWord
import com.alirezasn80.learn_en.utill.debug
import kotlinx.coroutines.launch
import kotlin.random.Random

data class ReadMode(
    val key: String,
    val icon: Int
)

private var wordSpeakCounter = 0

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ContentScreen(navigationState: NavigationState, viewModel: ContentViewModel = hiltViewModel()) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    val bottomSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    val destination = viewModel.destination


    // Close Bottom Sheet
    BackHandler(bottomSheetState.bottomSheetState.isVisible) {
        if (bottomSheetState.bottomSheetState.isVisible)
            scope.launch { bottomSheetState.bottomSheetState.hide() }
    }

    LaunchedEffect(destination) {
        if (destination is Destination.Payment) navigationState.navToPayment("TRANSLATE")
    }

    UI(
        checkOnline = true,
        onRefresh = {
            viewModel.getContent()
        },
        uiComponent = viewModel.uiComponents
    ) {
        BottomSheetScaffold(
            scaffoldState = bottomSheetState,
            sheetPeekHeight = 0.dp,
            sheetContainerColor = MaterialTheme.colorScheme.primary,
            sheetContent = {
                if (viewModel.progress[LoadingKey.DICT] is Progress.Idle && state.sheetModel == null) {
                    SideEffect { scope.launch { bottomSheetState.bottomSheetState.hide() } }
                } else if (state.sheetModel == null) {
                    SheetLoading()
                } else {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        CategorySection(
                            selectedCategory = state.selectedCategory,
                            onCategoryClick = viewModel::setSelectedDictCategory
                        )
                        when (state.selectedCategory) {

                            DictCategory.Desc -> {
                                DescSection(state.sheetModel!!.descriptions)
                            }

                            DictCategory.Meaning -> {
                                MeaningSection(
                                    sheetModel = state.sheetModel!!,
                                    onWordSpeak = viewModel::wordSpeak,
                                    onChangeHighlightMode = viewModel::changeHighlightMode,
                                )
                            }

                            DictCategory.Example -> {
                                ExampleSection(examples = state.sheetModel!!.examples)
                            }
                        }


                    }


                }


            }
        ) {
            Scaffold(
                topBar = {
                    Header(
                        title = state.title,
                        isVisibleTranslate = state.isVisibleTranslate,
                        isMute = state.isMute,
                        isBookmark = state.isBookmark,
                        upPress = navigationState::upPress,
                        onTranslateClick = {
                            if (User.isVipUser || viewModel.isTrial)
                                viewModel.onTranslateClick()
                            else
                                navigationState.navToPayment("TRANSLATE")
                        },
                        onMuteClick = viewModel::onMuteClick,
                        onBookmarkClick = viewModel::onBookmarkClick


                    )
                },
                bottomBar = {
                    if (!state.isMute)
                        BottomBar(
                            isPlay = state.isPlay,
                            onSpeedClick = viewModel::setReadSpeed,
                            onPlayClick = viewModel::readParagraph,
                            onBackwardClick = viewModel::onBackwardClick,
                            onForwardClick = viewModel::onForwardClick,
                            onReadModeClick = viewModel::onReadModeClick
                        )
                }
            ) { scaffoldPadding ->
                Ltr {
                    if (viewModel.progress[""] is Progress.Loading) {
                        MainLoading(scaffoldPadding)
                    } else
                        LazyColumn(Modifier.padding(scaffoldPadding)) {
                            itemsIndexed(state.paragraphs) { index, paragraph ->
                                ParagraphSection(
                                    highlights = state.highlights,
                                    isFocus = state.readableIndex == index,
                                    paragraph = paragraph,
                                    isVisibleTranslate = state.isVisibleTranslate,
                                    onWordClick = {
                                        if (User.isVipUser || viewModel.isTrial) {
                                            scope.launch { bottomSheetState.bottomSheetState.expand() }
                                            debug("word : $it")
                                            viewModel.onWordClick(it.lowercase())
                                        } else {
                                            navigationState.navToPayment("DICT")
                                        }
                                    },
                                    onClick = { viewModel.onParagraphClick(index) }
                                )
                            }
                        }
                }


            }
        }
    }

}

@Composable
fun DescSection(descriptions: List<Desc>) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Ltr {
            descriptions.forEach { desc ->
                ExtraSmallSpacer()
                TypeSection(desc.type)
                ExtraSmallSpacer()
                Column {
                    desc.texts.forEach {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(dimension.medium)
                        ) {
                            Text(text = it, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Line(thickness = 2.dp)
                    }
                }

            }
        }

    }
}

@Composable
fun ExampleSection(examples: List<String>) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Ltr {
            examples.forEach { example ->
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(dimension.medium)
                    ) {
                        StyledText(text = example)
                    }
                    Line(thickness = 2.dp)

                }

            }
        }

    }
}

@Composable
private fun StyledText(text: String) {
    val annotatedString = buildAnnotatedString {
        val regex = Regex("<b>(.*?)</b>")
        val matches = regex.findAll(text)
        var lastIndex = 0

        matches.forEach { match ->
            append(text.substring(lastIndex, match.range.first))
            withStyle(style = SpanStyle(color = Red100, fontWeight = FontWeight.Bold)) {
                append(match.groups[1]?.value ?: "")
            }
            lastIndex = match.range.last + 1
        }

        if (lastIndex < text.length) {
            append(text.substring(lastIndex, text.length))
        }
    }

    Text(text = annotatedString, color = MaterialTheme.colorScheme.onSurface)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MeaningSection(
    sheetModel: SheetModel,
    onWordSpeak: (String, Float) -> Unit,
    onChangeHighlightMode: (String, Boolean) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {

        /*if (viewModel.progress[LoadingKey.IMG] is Progress.Loading)
            SliderLoading()
        else if (state.sheetModel?.images?.isNotEmpty() == true) {
            SliderImage(images = state.sheetModel!!.images)
        }*/

        sheetModel.apply {


            HeadSection(
                word = mainWord,
                define = define,
                isHighlight = isHighlight,
                onSoundClick = {
                    val speed = if (wordSpeakCounter == 0) {
                        wordSpeakCounter++
                        1f
                    } else {
                        wordSpeakCounter = 0
                        0.5f
                    }
                    onWordSpeak(mainWord, speed)
                },
                onHighlightClick = {
                    onChangeHighlightMode(mainWord, it)
                }
            )

            Line()

            synonyms.forEach { translation ->
                ExtraSmallSpacer()
                TypeSection(translation.type)
                ExtraSmallSpacer()
                Column {
                    translation.defines.forEach {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(dimension.medium)
                        ) {
                            Text(text = it.word, color = MaterialTheme.colorScheme.onSurface)
                            SmallSpacer()
                            Ltr {
                                FlowRow(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    it.synonyms.forEach { word ->
                                        Text(
                                            text = word, modifier = Modifier
                                                .padding(horizontal = 2.dp)
                                                .clickable {
                                                    val speed = if (wordSpeakCounter == 0) {
                                                        wordSpeakCounter++
                                                        1f
                                                    } else {
                                                        wordSpeakCounter = 0
                                                        0.5f
                                                    }
                                                    onWordSpeak(word, speed)
                                                }
                                                .background(
                                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                                                    MaterialTheme.shapes.extraSmall
                                                )
                                                .padding(horizontal = dimension.extraSmall)
                                        )


                                    }

                                }
                            }


                        }
                        Line(thickness = 2.dp)
                    }
                }

            }

        }
    }

}


private val categories = listOf(
    DictCategory.Meaning,
    DictCategory.Desc,
    DictCategory.Example
)

@Composable
fun CategorySection(
    selectedCategory: DictCategory,
    onCategoryClick: (DictCategory) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimension.small),
        horizontalArrangement = Arrangement.spacedBy(dimension.small)
    ) {
        categories.forEach { category ->
            val isSelected = category.id == selectedCategory.id

            Text(
                text = category.title,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                    .clickable { onCategoryClick(category) }
                    .padding(horizontal = dimension.small, vertical = dimension.extraSmall)
            )

        }
    }
}

@Composable
fun SliderLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(horizontal = dimension.medium)
            .clip(MaterialTheme.shapes.small)
            .background(Color.LightGray)
            .shimmerEffect()
    )
}

@Composable
fun TypeSection(value: String) {
    Text(
        text = value, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimension.medium),
        textAlign = TextAlign.End,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun HeadSection(
    word: String,
    define: String,
    isHighlight: Boolean,
    onSoundClick: () -> Unit,
    onHighlightClick: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(dimension.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            /*Icon(
                painter = painterResource(id = R.drawable.ic_sound_max),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onSoundClick() }
            )*/
            Icon(
                painter = painterResource(id = if (isHighlight) R.drawable.ic_star_fill else R.drawable.ic_star_outline),
                contentDescription = null,
                tint = Color(0xFFFFBF00),
                modifier = Modifier
                    .size(30.dp)
                    .clickable { onHighlightClick(!isHighlight) }
            )
            ExtraSmallSpacer()
            Text(
                text = define, modifier = Modifier,
                color = MaterialTheme.colorScheme.onSurface
            )
        }


        Text(
            text = word,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .clickable { onSoundClick() }
                .background(
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                    MaterialTheme.shapes.extraSmall
                )
                .padding(horizontal = dimension.extraSmall)
        )


    }

}

@Composable
fun SheetLoading() {
    repeat(3) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 1.dp)
                .background(MaterialTheme.colorScheme.surface)
                .padding(dimension.medium),
            verticalArrangement = Arrangement.spacedBy(dimension.extraSmall)
        ) {
            repeat(3) { LoadingLine() }
        }
    }

}

@Composable
private fun MainLoading(paddingValues: PaddingValues) {
    Column(
        Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        repeat(15) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(dimension.medium),
                verticalArrangement = Arrangement.spacedBy(dimension.extraSmall)
            ) {
                repeat(3) { LoadingLine() }
            }
        }
    }
}

@Composable
private fun LoadingLine() {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .fillMaxWidth(
                Random
                    .nextDouble(0.20, 0.99)
                    .toFloat()
            )
            .height(13.dp)
            .shimmerEffect()
            .background(Color.LightGray.copy(alpha = 0.5f))
    )
}

@Composable
private fun BottomBar(
    isPlay: Boolean,
    onSpeedClick: (Float) -> Unit,
    onPlayClick: (Boolean) -> Unit,
    onForwardClick: () -> Unit,
    onBackwardClick: () -> Unit,
    onReadModeClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = dimension.extraSmall)
    ) {
        SpeedSection(onSpeedClick)

        MediaControllerSection(onBackwardClick, onForwardClick, onPlayClick, isPlay)

        ReadableStateSection(onClick = onReadModeClick)
    }
}

var selectedIndex = 0

@Composable
fun BoxScope.ReadableStateSection(onClick: (String) -> Unit) {
    var selectedIcon by remember { mutableStateOf(ReadMode("default", R.drawable.ic_default_read)) }
    var clicked by remember { mutableStateOf(false) }

    if (clicked) {
        selectedIcon = when (selectedIndex) {
            0 -> {
                ReadMode("default", R.drawable.ic_default_read)
            }

            1 -> {
                ReadMode("repeat", R.drawable.ic_repeat)
            }

            2 -> ReadMode("play_stop", R.drawable.ic_play_stop)

            else -> ReadMode("default", R.drawable.ic_default_read)
        }
        onClick(selectedIcon.key)
        clicked = false
    }



    IconButton(
        modifier = Modifier
            .align(Alignment.CenterStart),
        onClick = {
            if (selectedIndex < 2) selectedIndex++ else selectedIndex = 0
            clicked = true

        }) {
        Icon(imageVector = ImageVector.vectorResource(selectedIcon.icon), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
fun BoxScope.MediaControllerSection(onBackwardClick: () -> Unit, onForwardClick: () -> Unit, onPlayClick: (Boolean) -> Unit, isPlay: Boolean) {
    Row(
        Modifier.align(Alignment.Center),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onForwardClick) {
            Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_forward), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
        }
        SmallSpacer()
        IconButton(
            onClick = {
                onPlayClick(!isPlay)
            }, modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        ) {
            Icon(imageVector = ImageVector.vectorResource(if (isPlay) R.drawable.ic_stop else R.drawable.ic_play), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
        }
        SmallSpacer()
        IconButton(onClick = onBackwardClick) {
            Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_backward), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
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
    highlights: List<String>,
    isFocus: Boolean,
    paragraph: Paragraph,
    isVisibleTranslate: Boolean,
    onWordClick: (String) -> Unit,
    onClick: () -> Unit
) {
    Column(
        Modifier
            .clickable { onClick() }
            .fillMaxWidth()
             .padding(vertical =if (isVisibleTranslate)1.dp else 0.dp)
            .background(MaterialTheme.colorScheme.surface)
            .background(if (isFocus) MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f) else Color.Unspecified)
            .padding(horizontal = dimension.medium, vertical = dimension.small)
    ) {
        ClickableWordsText(
            text = paragraph.text,
            onClick = { onWordClick(it.cleanWord()) },
            highlights = highlights
        )
        SmallSpacer()

        Rtl {
            if (isVisibleTranslate) {
                Text(text = paragraph.translated, modifier = Modifier.fillMaxWidth())
               // SmallSpacer()
               // Line()
            }
        }

    }
}

@Composable
private fun Header(
    isMute: Boolean,
    title: String,
    isVisibleTranslate: Boolean,
    isBookmark: Boolean,
    upPress: () -> Unit,
    onTranslateClick: () -> Unit,
    onMuteClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = upPress) {
            Icon(
                imageVector = Icons.Rounded.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }

        Text(
            text = title, modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically), textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleSmall
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = dimension.medium)
        ) {


            // Mute
            Icon(
                modifier = Modifier
                    .clickable { onMuteClick() }
                    .clip(CircleShape)
                    .background(
                        if (isMute)
                            Color.White.copy(alpha = 0.3f)
                        else
                            Color.Unspecified
                    )
                    .padding(dimension.extraSmall),
                imageVector = ImageVector.vectorResource(if (isMute) R.drawable.ic_sound_mute else R.drawable.ic_sound_max),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )

            SmallSpacer()


            // Translate
            Icon(
                modifier = Modifier
                    .clickable { onTranslateClick() }
                    .clip(CircleShape)
                    .background(
                        if (isVisibleTranslate)
                            Color.White.copy(alpha = 0.3f)
                        else
                            Color.Unspecified
                    )
                    .padding(dimension.extraSmall),
                imageVector = ImageVector.vectorResource(R.drawable.ic_translate),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )

            SmallSpacer()

            // Bookmark
            Icon(
                modifier = Modifier
                    .clickable { onBookmarkClick() }
                    .clip(CircleShape)
                    .background(
                        if (isBookmark)
                            Color.White.copy(alpha = 0.3f)
                        else
                            Color.Unspecified
                    )
                    .padding(dimension.extraSmall),
                imageVector = ImageVector.vectorResource(if (isBookmark) R.drawable.ic_enable_bookmark else R.drawable.ic_disable_bookmark),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )

        }

    }
}

@Composable
fun ClickableWordsText(highlights: List<String>, text: String, onClick: (String) -> Unit) {
    val words = text.split(" ")
    val annotatedString = buildAnnotatedString {
        words.forEach { word ->
            val annotation = "clickable_word"
            pushStringAnnotation(tag = annotation, annotation = word)
            withStyle(
                style = SpanStyle(
                    fontSize = 16.sp,
                    color = if (word.cleanWord().lowercase() in highlights) highlighterColor() else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (word.cleanWord().lowercase() in highlights) FontWeight.ExtraBold else FontWeight.Normal
                )
            ) {
                append("$word")
            }
            append(" ")
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
        },
        style = MaterialTheme.typography.bodyLarge
    )
}
