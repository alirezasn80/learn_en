package com.alirezasn80.learn_en.feature.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alirezasn80.learn_en.R
import com.alirezasn80.learn_en.app.navigation.NavigationState
import com.alirezasn80.learn_en.app.navigation.Screen
import com.alirezasn80.learn_en.ui.common.UI
import com.alirezasn80.learn_en.ui.theme.LargeSpacer
import com.alirezasn80.learn_en.ui.theme.SmallSpacer
import com.alirezasn80.learn_en.ui.theme.dimension
import com.alirezasn80.learn_en.utill.Destination
import com.alirezasn80.learn_en.utill.getVersionName

@Composable
fun SplashScreen(
    navigationState: NavigationState,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsStateWithLifecycle()
    val context = LocalContext.current


    LaunchedEffect(key1 = destination) {

        when (viewModel.destination.value) {

            Destination.Home -> navigationState.navToHome()

            Destination.OnBoarding -> navigationState.navToOnBoarding()

            Destination.Offline -> navigationState.navToOffline()

            else -> Unit
        }
    }
    UI {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {

            LogoSection()

            // Version
            Text(
                text = "نسخه ${context.getVersionName()}",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = dimension.medium),
                color = MaterialTheme.colorScheme.onBackground
            )

        }
    }


}

@Composable
private fun BoxScope.LogoSection() {
    Column(
        Modifier
            .wrapContentSize()
            .align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.img_logo),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape),
            tint = Color.Unspecified

        )
        SmallSpacer()
        Text(text = "داستان های انگلیسی", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
        LargeSpacer()
        LinearProgressIndicator(
            Modifier
                .clip(MaterialTheme.shapes.small)
                .fillMaxWidth(0.5f)
        )

    }
}
