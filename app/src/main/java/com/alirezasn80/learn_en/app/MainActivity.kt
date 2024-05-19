package com.alirezasn80.learn_en.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alirezasn80.learn_en.app.navigation.Screen
import com.alirezasn80.learn_en.app.navigation.rememberNavigationState
import com.alirezasn80.learn_en.feature.aboutus.AboutUsScreen
import com.alirezasn80.learn_en.feature.content.ContentScreen
import com.alirezasn80.learn_en.feature.create.CreateScreen
import com.alirezasn80.learn_en.feature.flash_card.FlashCardScreen
import com.alirezasn80.learn_en.feature.home.HomeScreen
import com.alirezasn80.learn_en.feature.offline.OfflineScreen
import com.alirezasn80.learn_en.feature.onboarding.OnBoardingScreen
import com.alirezasn80.learn_en.feature.payment.PaymentScreen
import com.alirezasn80.learn_en.feature.splash.SplashScreen
import com.alirezasn80.learn_en.feature.stories.StoriesScreen
import com.alirezasn80.learn_en.ui.theme.Learn_enTheme
import com.alirezasn80.learn_en.ui.theme.ThemeViewModel
import com.alirezasn80.learn_en.utill.LocaleUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        LocaleUtils.updateResources(this, "fa")
        setContent {

            val navigationState = rememberNavigationState()
            val themeViewModel: ThemeViewModel = hiltViewModel()

            Learn_enTheme(themeViewModel) {
                NavHost(
                    navController = navigationState.navController,
                    startDestination = Screen.Splash.route,
                ) {


                    composable(Screen.OnBoarding.route) {
                        OnBoardingScreen(navigationState)
                    }

                    composable(Screen.Splash.route) {
                        SplashScreen(navigationState)
                    }

                    composable(Screen.Home.route) {
                        HomeScreen(navigationState, themeViewModel)
                    }

                    composable(Screen.Stories.route) {
                        StoriesScreen(navigationState)
                    }

                    composable(Screen.Content.route) {
                        ContentScreen(navigationState)
                    }

                    composable(Screen.Create.route) {
                        CreateScreen(navigationState)
                    }

                    composable(Screen.Payment.route) {
                        PaymentScreen(navigationState::upPress)
                    }

                    composable(Screen.Offline.route) {
                        OfflineScreen(navigationState)
                    }

                    composable(Screen.AboutUs.route) {
                        AboutUsScreen(navigationState::upPress)
                    }

                    composable(Screen.FlashCard.route) {
                        FlashCardScreen(navigationState::upPress)
                    }

                }
            }
        }
    }
}