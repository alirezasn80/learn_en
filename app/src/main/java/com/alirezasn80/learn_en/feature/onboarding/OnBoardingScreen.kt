package com.alirezasn80.learn_en.feature.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.app.navigation.NavigationState
import com.alirezasn80.learn_en.feature.payment.IndicatorSection
import com.alirezasn80.learn_en.ui.theme.SmallSpacer
import com.alirezasn80.learn_en.ui.theme.dimension

private data class OnboardingModel(
    val image: Int,
    val desc: Int
)

private val onboardingItems = listOf(
    OnboardingModel(
        image = R.drawable.img_translate,
        desc = R.string.auto_translate_desc
    ),
    OnboardingModel(
        image = R.drawable.img_dictionary,
        desc = R.string.dictionary_desc
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(navigationState: NavigationState, viewModel: OnBoardingViewModel = hiltViewModel()) {
    val pagerState = rememberPagerState(pageCount = { onboardingItems.size })

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        // Icons above File
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(horizontal = dimension.medium)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,

                ) {
                val item = onboardingItems[it]
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painter = painterResource(id = item.image), contentDescription = null)
                    SmallSpacer()
                    Text(text = stringResource(id = item.desc))
                }

            }
            SmallSpacer()
            IndicatorSection(
                pagerState = pagerState,
                indicatorCount = onboardingItems.size
            )
        }

        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            SpecificButton {
                viewModel.hideOnBoarding()
                navigationState.navToHome()
            }


        }
    }

}

@Composable
private fun BoxScope.SpecificButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(10.dp)
            .border((0.2).dp, MaterialTheme.colorScheme.onPrimary, CircleShape)
            .padding(9.dp)
            .border((0.5).dp, MaterialTheme.colorScheme.onPrimary, CircleShape)
            .padding(7.dp)
            .border(1.dp, MaterialTheme.colorScheme.onPrimary, CircleShape)
            .padding(5.dp)
            .clip(CircleShape)
            .size(60.dp)
            .align(Alignment.BottomCenter)
            .background(brush = Brush.linearGradient(listOf(Color.White, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondary)), shape = CircleShape)
    ) {
        Icon(imageVector = Icons.Rounded.ArrowForward, contentDescription = "Arrow", tint = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
private fun BoxScope.DescriptionSection() {
    Column(
        Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
            .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.learn_english_with_story),
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(id = R.string.onboarding_desc),
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(dimension.medium),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 13.sp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BoxScope.PictureIcon() {
    Icon(
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_about),
        contentDescription = "Picture",
        modifier = Modifier
            .offset(x = (-40).dp, y = (-130).dp)
            .size(50.dp)
            .align(Alignment.BottomCenter),
        tint = Color.Unspecified
    )
}

@Composable
private fun BoxScope.MusicIcon() {
    Icon(
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_about),
        contentDescription = "Picture",
        modifier = Modifier
            .offset(x = (60).dp, y = (-160).dp)
            .size(50.dp)
            .align(Alignment.BottomCenter),
        tint = Color.Unspecified
    )
}

@Composable
private fun BoxScope.DocumentIcon() {
    Icon(
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_about),
        contentDescription = "Picture",
        modifier = Modifier
            .offset(x = (-30).dp, y = (-210).dp)
            .size(50.dp)
            .align(Alignment.BottomCenter),
        tint = Color.Unspecified
    )
}

@Composable
private fun BoxScope.VideoIcon() {
    Icon(
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_about),
        contentDescription = "Picture",
        modifier = Modifier
            .offset(x = (50).dp, y = (-250).dp)
            .size(50.dp)
            .align(Alignment.BottomCenter),
        tint = Color.Unspecified
    )
}

@Composable
private fun BoxScope.FolderIcon() {
    Icon(
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_about),
        contentDescription = "Folder",
        modifier = Modifier
            .size(150.dp)
            .align(Alignment.BottomCenter),
        tint = Color.Unspecified
    )
}

