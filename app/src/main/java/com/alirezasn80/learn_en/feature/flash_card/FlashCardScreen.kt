package com.alirezasn80.learn_en.feature.flash_card

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.core.domain.local.Desc
import com.alirezasn80.learn_en.core.domain.local.SheetModel
import com.alirezasn80.learn_en.feature.content.ExampleSection
import com.alirezasn80.learn_en.feature.content.TypeSection
import com.alirezasn80.learn_en.ui.common.UI
import com.alirezasn80.learn_en.ui.common.shimmerEffect
import com.alirezasn80.learn_en.ui.theme.ExtraSmallSpacer
import com.alirezasn80.learn_en.ui.theme.Line
import com.alirezasn80.learn_en.ui.theme.Red100
import com.alirezasn80.learn_en.ui.theme.SmallSpacer
import com.alirezasn80.learn_en.ui.theme.dimension
import com.alirezasn80.learn_en.utill.DictCategory
import com.alirezasn80.learn_en.utill.Ltr


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FlashCardScreen(
    upPress: () -> Unit,
    viewModel: FlashCardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var currentPage by remember { mutableIntStateOf(1) }
    var showDict by remember { mutableStateOf(false) }

    UI(
        uiComponent = viewModel.uiComponents
    ) {

        Scaffold(
            topBar = {
                TopSection(
                    currentPage = currentPage,
                    totalPages = state.flashcards?.size,
                    upPress = upPress
                )
            }
        ) { paddingValues ->

            if (state.flashcards == null) {
                CardLoading(paddingValues)
            } else if (state.flashcards!!.isEmpty()) {
                EmptySection(paddingValues)
            } else {
                val pagerState = rememberPagerState(pageCount = { state.flashcards!!.size })

                LaunchedEffect(key1 = pagerState.currentPage) {
                    currentPage = pagerState.currentPage + 1
                }

                if (showDict) {
                    DictSection(
                        sheetModel = state.flashcards!![pagerState.currentPage],
                        selectedCategory = state.selectedCategory,
                        onDismiss = { showDict = false },
                        onWordSpeak = viewModel::wordSpeak,
                        onCategoryClick = viewModel::setSelectedDictCategory,
                    )
                }


                HorizontalPager(
                    state = pagerState,
                    reverseLayout = true,
                    modifier = Modifier
                        .fillMaxSize(),
                ) { page ->
                    var rotate by remember { mutableStateOf(false) }
                    val angle: Float by animateFloatAsState(
                        targetValue = if (rotate) 180f else 0f,
                        animationSpec = tween(durationMillis = 500),
                        label = ""
                    )

                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Box(
                            Modifier
                                .graphicsLayer { rotationY = angle }
                                .padding(paddingValues)
                                .fillMaxSize(0.7f)
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable { rotate = !rotate }
                        ) {
                            AnimatedContent(
                                targetState = rotate,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                                },
                                label = ""
                            ) {
                                if (it) {
                                    DefinitionSection(
                                        definition = state.flashcards!![page].define,
                                        onStarClick = {
                                            viewModel.removeFromFlashcards(state.flashcards!![page].word)
                                        },
                                        onDetailClick = {
                                            viewModel.refreshCategory()
                                            showDict = true
                                        }
                                    )
                                } else {
                                    WordSection(
                                        isFirst = page == 0,
                                        word = state.flashcards!![page].word,
                                        onStarClick = viewModel::removeFromFlashcards,
                                        onSoundClick = viewModel::wordSpeak
                                    )
                                }
                            }


                        }
                    }

                }

            }


        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DictSection(
    sheetModel: SheetModel,
    selectedCategory: DictCategory,
    onCategoryClick: (DictCategory) -> Unit,
    onWordSpeak: (String, Float) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 40.dp)
        ) {
            CategorySection(
                categories = sheetModel.getCategories(),
                selectedCategory = selectedCategory,
                onCategoryClick = onCategoryClick
            )
            when (selectedCategory) {

                DictCategory.Desc -> {
                    DescSection(sheetModel.descriptions)
                }

                DictCategory.Meaning -> {
                    MeaningSection(
                        sheetModel = sheetModel,
                        onWordSpeak = onWordSpeak,
                    )
                }

                DictCategory.Example -> {
                    ExampleSection(examples = sheetModel.examples)
                }
            }


        }

    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MeaningSection(
    sheetModel: SheetModel,
    onWordSpeak: (String, Float) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {


        sheetModel.apply {

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
                                                    onWordSpeak(word, 0.5f)
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

@Composable
private fun DescSection(descriptions: List<Desc>) {
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
private fun CategorySection(
    categories: List<DictCategory>,
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
private fun EmptySection(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .padding(dimension.medium)
            .fillMaxSize()
            .padding(dimension.large),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "هنوز لغتی به فلش کارت ها اضافه نشده",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CardLoading(paddingValues: PaddingValues) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(0.8f)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surface)
                .shimmerEffect()
        )
    }

}

@Composable
private fun DefinitionSection(
    definition: String,
    onStarClick: () -> Unit,
    onDetailClick: () -> Unit,
) {
    Box(
        Modifier
            .graphicsLayer { rotationY = 180f }
            .fillMaxSize()
    ) {
        IconButton(
            onClick = onStarClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(dimension.small)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = null,
                tint = Red100,
                modifier = Modifier.size(40.dp)

            )
        }

        Text(text = definition, modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.titleLarge)

        TextButton(
            onClick = onDetailClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dimension.large)
        ) {
            Text(
                text = stringResource(id = R.string.more_detail),
                color = MaterialTheme.colorScheme.primary,
            )
        }

    }
}

@Composable
private fun WordSection(
    isFirst: Boolean,
    word: String,
    onStarClick: (String) -> Unit,
    onSoundClick: (String, Float) -> Unit
) {
    Box(
        Modifier.fillMaxSize()
    ) {
        IconButton(
            onClick = { onStarClick(word) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(dimension.small)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_star_fill),
                contentDescription = null,
                tint = Color(0xFFFFBF00),
                modifier = Modifier.size(40.dp)
            )
        }

        IconButton(
            onClick = { onSoundClick(word, 1f) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(dimension.small)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_sound_max),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
        }

        Row(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(text = word, style = MaterialTheme.typography.titleLarge)
        }

        if (isFirst)
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_left_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(bottom = dimension.large)
                    .size(45.dp)
                    .align(Alignment.BottomCenter)
                    .alpha(0.5f)
            )

    }
}

@Composable
private fun TopSection(
    currentPage: Int,
    totalPages: Int?,
    upPress: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary),
        ) {
            IconButton(onClick = upPress, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(
                    imageVector = Icons.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }

            Text(
                text = stringResource(id = R.string.flash_card),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.Center)

            )

        }

        if (totalPages != null && totalPages != 0) {
            SmallSpacer()

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimension.medium)
            ) {
                Text(text = "$currentPage/$totalPages")
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(MaterialTheme.shapes.medium),
                    progress = (currentPage.toFloat() / totalPages),
                    strokeCap = StrokeCap.Round

                )
            }
        }


    }

}


