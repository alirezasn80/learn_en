package com.alirezasn80.learn_en.feature.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.app.navigation.NavigationState
import com.alirezasn80.learn_en.ui.theme.LargeSpacer
import com.alirezasn80.learn_en.ui.theme.MediumSpacer
import com.alirezasn80.learn_en.ui.theme.dimension

private data class OnboardingModel(
    val image: Int,
    val desc: Int
)

private val onboardingItems = listOf(
    OnboardingModel(
        image = R.drawable.img_books,
        desc = R.string.various_stories
    ),
    OnboardingModel(
        image = R.drawable.img_translator,
        desc = R.string.translate_and_dectionary_onboarding
    ),
    OnboardingModel(
        image = R.drawable.img_note2,
        desc = R.string.create_story_onbloarding
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(navigationState: NavigationState, viewModel: OnBoardingViewModel = hiltViewModel()) {
    val pagerState = rememberPagerState(pageCount = { onboardingItems.size })

    Scaffold(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        bottomBar = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                SpecificButton {
                    viewModel.hideOnBoarding()
                    navigationState.navToHome()
                }
            }
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(it),
            verticalArrangement = Arrangement.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(horizontal = dimension.medium)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val item = onboardingItems[it]
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = item.image),
                        contentDescription = null,
                        modifier = Modifier.size(150.dp)

                    )
                    LargeSpacer()
                    Text(
                        text = stringResource(id = item.desc),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

            }
            LargeSpacer()
            HorizontalPagerIndicator(
                indicatorCount = onboardingItems.size,
                currentPage = pagerState.currentPage
            )
        }


    }

}

@Composable
private fun SpecificButton(onClick: () -> Unit) {
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
            .background(brush = Brush.linearGradient(listOf(Color.White, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondary)), shape = CircleShape)
    ) {
        Icon(imageVector = Icons.Rounded.ArrowForward, contentDescription = "Arrow", tint = MaterialTheme.colorScheme.onPrimary)
    }
}


@Composable
fun HorizontalPagerIndicator(
    indicatorCount: Int,
    currentPage: Int,
    indicatorColor: Color = Color.LightGray,
    activeIndicatorColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Row(horizontalArrangement = Arrangement.spacedBy(dimension.extraSmall)) {
            repeat(indicatorCount) { index ->
                val color = if (index == currentPage) activeIndicatorColor else indicatorColor
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color = color, shape = CircleShape)

                )
            }
        }
    }

}



