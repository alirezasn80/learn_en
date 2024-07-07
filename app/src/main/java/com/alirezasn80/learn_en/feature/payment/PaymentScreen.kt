package com.alirezasn80.learn_en.feature.payment

import android.app.Activity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.ui.common.BaseTopBar
import com.alirezasn80.learn_en.ui.common.UI
import com.alirezasn80.learn_en.ui.theme.Gold100
import com.alirezasn80.learn_en.ui.theme.LargeSpacer
import com.alirezasn80.learn_en.ui.theme.MediumSpacer
import com.alirezasn80.learn_en.ui.theme.Red100
import com.alirezasn80.learn_en.ui.theme.SmallSpacer
import com.alirezasn80.learn_en.ui.theme.dimension
import com.alirezasn80.learn_en.utill.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

private data class PaymentSliderModel(
    val image: Int,
    val title: Int,
    val desc: Int
)

private val payment_slider = listOf(
    PaymentSliderModel(
        image = R.drawable.img_translate,
        title = R.string.auto_translate,
        desc = R.string.auto_translate_desc
    ),
    PaymentSliderModel(
        image = R.drawable.img_dictionary,
        title = R.string.dictionary,
        desc = R.string.dictionary_desc
    ),
    PaymentSliderModel(
        image = R.drawable.img_note,
        title = R.string.create_story,
        desc = R.string.create_story_desc
    )
)

private val pagerItems by lazy { payment_slider }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PaymentScreen(upPress: () -> Unit, viewModel: PaymentViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val registry = LocalActivityResultRegistryOwner.current?.activityResultRegistry
    val pagerState = rememberPagerState(pageCount = { pagerItems.size })


    LaunchedEffect(state.successPayment) {
        if (state.successPayment) {
            context.showToast(R.string.you_now_vip)
            upPress()
        }
    }


    // Auto Pager
    LaunchedEffect(pagerState.currentPage) {
        launch {
            delay(3.seconds)
            pagerState.apply {
                val target = if (currentPage < pageCount - 1) currentPage + 1 else 0
                pagerState.scrollToPage(target)
            }


        }
    }

    Box {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                BaseTopBar(
                    title = R.string.vip_user,
                    upPress = upPress,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    containerColor = MaterialTheme.colorScheme.background
                )
            },
            bottomBar = {
                Column(Modifier.padding(dimension.medium)) {

                    PaymentButton(
                        text = R.string.vip_payment,
                        textColor = MaterialTheme.colorScheme.onPrimary,
                        backgroundColor = MaterialTheme.colorScheme.primary
                    ) {
                        viewModel.buyProduct(context as Activity, "vip")
                    }
                }

            }
        ) {
            Column(
                Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = dimension.medium)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .padding(horizontal = dimension.medium)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,

                    ) { PagerCell(pagerItems[it]) }
                SmallSpacer()
                IndicatorSection(
                    pagerState = pagerState,
                    indicatorCount = pagerItems.size
                )
                LargeSpacer()
                DescSection()

                /*if (ABOVE_Q && state.testCleaner != null)
                    CheckValidVersion(
                        cleaner = state.testCleaner!!,
                        setValidSAFPermission = viewModel::setValidSAFPermission
                    )*/

                /*LargeSpacer()
                PaymentButton(
                    text = R.string.mounch_12,
                    textColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    val productId = if (BELOW_R) "VIP1_12" else "VIP2_12"
                    viewModel.buy(registry, productId)
                }
                MediumSpacer()
                PaymentButton(text = R.string.mounch_6) {
                    val productId = if (BELOW_R) "VIP1_6" else "VIP2_6"
                    viewModel.buy(registry, productId)
                }
                MediumSpacer()
                PaymentButton(text = R.string.mounch_3) {
                    val productId = if (BELOW_R) "VIP1_3" else "VIP2_3"
                    viewModel.buy(registry, productId)
                }*/
            }
        }

        if (state.isLoading) {
            LoadingPage()
        }
    }


}

@Composable
fun LoadingPage() {

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.75f))
            .blur(0.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
            .clickable(enabled = false) {}
            .shadow(1000.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }


}

@Composable
private fun PaymentButton(
    text: Int,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = dimension.medium)
            .clip(MaterialTheme.shapes.small)
            .clickable { onClick() }
            .fillMaxWidth()
            .background(backgroundColor)
            .border(1.dp, MaterialTheme.colorScheme.onPrimary, MaterialTheme.shapes.small)
            .padding(dimension.medium),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(id = text), style = MaterialTheme.typography.titleSmall, color = textColor)
    }
}

@Composable
private fun DescSection() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(dimension.medium)
    ) {
        pagerItems.forEach {
            DescCell(it.desc)
        }
    }
}

@Composable
private fun DescCell(desc: Int) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = dimension.small), verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.CheckCircle, contentDescription = null,
            modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary
        )
        SmallSpacer()
        Text(
            text = stringResource(id = desc),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall
        )
    }
}


@Composable
private fun PagerCell(item: PaymentSliderModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = item.image),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit
        )
        MediumSpacer()
        Text(
            text = stringResource(id = item.title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IndicatorSection(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    indicatorCount: Int = 5,
    indicatorSize: Dp = 8.dp,
    indicatorShape: Shape = CircleShape,
    space: Dp = 4.dp,
    activeColor: Color = MaterialTheme.colorScheme.onBackground,
    inActiveColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
    onClick: ((Int) -> Unit)? = null
) {
    val listState = rememberLazyListState()
    val totalWidth: Dp = indicatorSize * indicatorCount + space * (indicatorCount - 1)
    val widthInPx = LocalDensity.current.run { indicatorSize.toPx() }
    val currentItem by remember { derivedStateOf { pagerState.currentPage } }
    val itemCount = pagerState.pageCount

    LaunchedEffect(key1 = currentItem) {
        val viewportSize = listState.layoutInfo.viewportSize
        listState.animateScrollToItem(
            currentItem,
            (widthInPx / 2 - viewportSize.width / 2).toInt()
        )
    }

    LazyRow(
        modifier = modifier.width(totalWidth),
        state = listState,
        contentPadding = PaddingValues(vertical = space),
        horizontalArrangement = Arrangement.spacedBy(space),
        userScrollEnabled = false
    ) {
        items(itemCount) { index ->
            val isSelected = (index == currentItem)
            // Index of item in center when odd number of indicators are set
            // for 5 indicators this is 2nd indicator place
            val centerItemIndex = indicatorCount / 2
            val right1 = (currentItem < centerItemIndex && index >= indicatorCount - 1)
            val right2 = (currentItem >= centerItemIndex && index >= currentItem + centerItemIndex && index <= itemCount - centerItemIndex + 1)
            val isRightEdgeItem = right1 || right2
            // Check if this item's distance to center item is smaller than half size of
            // the indicator count when current indicator at the center or
            // when we reach the end of list.
            val isCenterItem = abs(currentItem - index) <= centerItemIndex
            val isLeftEdgeItem = !isRightEdgeItem && !isCenterItem
            val offset = pagerState.currentPageOffsetFraction
            val factor = abs(offset) / centerItemIndex
            val calculatedSize = indicatorSize * (1 - factor)
            val calculatedColor = lerp(activeColor, inActiveColor, factor)

            Box(
                modifier = Modifier
                    .size(calculatedSize)
                    .clip(indicatorShape)
                    .background(calculatedColor)
                    .clickable { onClick?.invoke(index) },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(indicatorSize / 2)
                            .clip(indicatorShape)
                            .background(inActiveColor)
                    )
                }
            }
        }
    }
}

