package com.alirezasn80.learn_en.feature.flash_card

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.alirezasn80.learn_en.ui.theme.SmallSpacer
import com.alirezasn80.learn_en.ui.theme.dimension


//todo(work on animation and other things..)
@Composable
fun FlashCardScreen(
    upPress: () -> Unit,
    viewModel: FlashCardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var rotate by remember { mutableStateOf(false) }
    val angle: Float by animateFloatAsState(
        targetValue = if (rotate) 180f else 0f,
        animationSpec = tween(durationMillis = 1000), label = ""
    )

    Scaffold(
        topBar = {
            TopSection(upPress)
        },
        bottomBar = {
            BottomSection { rotate = !rotate }

        }
    ) {
        WordSection(it, angle)
    }
}

@Composable
fun BottomSection(
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(dimension.medium), horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = onClick) {
            Text(text = stringResource(id = R.string.show_translate))
        }
    }
}

@Composable
fun WordSection(paddingValues: PaddingValues, angle: Float) {
    Box(
        Modifier
            .graphicsLayer {
                rotationY = angle
            }
            .padding(paddingValues)
            .padding(dimension.medium)
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(vertical = dimension.large)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)

    ) {
        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_star_fill),
                contentDescription = null,
                tint = Color(0xFFFFBF00),

                )
        }

        Text(text = "Cat", modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.titleLarge)

        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_left_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(bottom = dimension.large)
                .size(55.dp)
                .align(Alignment.BottomCenter)
                .alpha(0.5f)
        )

    }
}

@Composable
private fun TopSection(upPress: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
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
                text = stringResource(id = R.string.flash_card), modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically), textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleSmall
            )
        }

        SmallSpacer()

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = dimension.medium)
        ) {
            Text(text = "1/12")
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(MaterialTheme.shapes.medium),
                progress = 0.1f,
                strokeCap = StrokeCap.Round

            )
        }
    }

}


