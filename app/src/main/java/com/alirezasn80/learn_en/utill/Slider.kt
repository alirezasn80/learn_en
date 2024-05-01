package com.alirezasn80.learn_en.utill


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.ui.common.shimmerEffect
import com.alirezasn80.learn_en.ui.theme.ExtraSmallSpacer
import com.alirezasn80.learn_en.ui.theme.dimension
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SliderImage(images: List<String>) {
    val context = LocalContext.current
    val windowSize = rememberWindowSize()
    val height = remember { if (windowSize.widthType is WindowSize.WindowType.Expanded) 440.dp else 220.dp }
    val count = images.size
    val state = rememberPagerState(initialPage = 0, initialPageOffsetFraction = 0f) { images.size }
    val slideImage = remember { mutableStateOf(images[0]) }
    // Create a custom OkHttpClient with a timeout
    val okHttpClient = OkHttpClient.Builder()
        .callTimeout(5, TimeUnit.SECONDS) // Set the desired timeout here
        .build()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        HorizontalPager(
            state = state,
            reverseLayout = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(horizontal = dimension.medium)
                .clip(MaterialTheme.shapes.small),
            pageSpacing = dimension.small
        ) { page ->
            slideImage.value = images[page]
            var isShimmer by remember { mutableStateOf(true) }

            val model = remember {
                ImageRequest.Builder(context)
                    .data(slideImage.value)
                    .error(R.drawable.ic_img_not_load)
                    .crossfade(true)
                    .listener(
                        onSuccess = { _, _ ->
                            isShimmer = false
                        },
                        onError = { _, _ ->
                            isShimmer = false

                        }
                    )
                    .build()
            }


            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .fillMaxWidth()
                    .height(height)
                    .background(Color.LightGray)
                    .clip(MaterialTheme.shapes.small)
                    .shimmerEffect(enable = isShimmer)
            ) {

                AsyncImage(
                    model = model,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(10.dp)
                        .graphicsLayer {
                            alpha = 0.5f
                        },
                    contentScale = ContentScale.FillBounds
                )

                AsyncImage(
                    model = model,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }


        }
        ExtraSmallSpacer()
        DotsIndicator(
            totalDots = count,
            selectedIndex = state.currentPage,
            selectedColor = MaterialTheme.colorScheme.primary,
            unSelectedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )

    }

}

@Composable
private fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color,
    unSelectedColor: Color,
) {

    LazyRow(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .wrapContentHeight()
            .padding(8.dp),
        reverseLayout = true
    ) {

        items(totalDots) { index ->
            if (index == selectedIndex) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(selectedColor)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(unSelectedColor)
                )
            }

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}