package com.alirezasn80.learn_en.utill

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.transition.CrossfadeTransition
import com.alirezasn80.learn_en.R

@ExperimentalCoilApi
@Composable
fun CoilImageWithBlur(
    modifier: Modifier,
    data: Any,
    @DrawableRes holderImage: Int = R.drawable.ic_img_loading,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val request = ImageRequest
            .Builder(LocalContext.current)
            .placeholder(holderImage)
            .error(holderImage)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .data(data)
            .build()

        val painter = rememberAsyncImagePainter(request)

        Image(
            painter = painter,
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.5f)
                .zIndex(1f)
                .blur(if (painter.state is AsyncImagePainter.State.Success) 10.dp else 0.dp)
        )

        Image(
            painter = painter,
            contentDescription = "Image",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxHeight()
                .zIndex(2f),
        )
    }
}

@ExperimentalCoilApi
@Composable
fun CoilImage(
    modifier: Modifier,
    data: Any,
    contentScale: ContentScale = ContentScale.Crop,
    placeHolder: Int = R.drawable.ic_img_loading,
) {
    val request = ImageRequest.Builder(context = LocalContext.current)
        .data(data)
        .placeholder(placeHolder)
        .error(placeHolder)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .transitionFactory(CrossfadeTransition.Factory(150))
        .build()

    AsyncImage(
        model = request,
        contentDescription = "Image",
        contentScale = contentScale,
        modifier = modifier
    )

}