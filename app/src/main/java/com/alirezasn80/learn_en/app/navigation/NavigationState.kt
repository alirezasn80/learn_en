package com.alirezasn80.learn_en.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun rememberNavigationState(navController: NavHostController = rememberNavController()) = remember(navController) {
    NavigationState(navController)
}

@Stable
class NavigationState(
    val navController: NavHostController
) {
    val currentRoute: String? get() = navController.currentDestination?.route

    val previousRoute: String? get() = navController.previousBackStackEntry?.destination?.route

    fun upPress() {
        navController.navigateUp()
    }

    fun popBackStack() {
        navController.popBackStack()
    }

    fun navToHome() = navController.navigate(Screen.Home.route) { popUpTo(0) }

    fun navToSplash() = navController.navigate(Screen.Splash.route) { popUpTo(0) }

    fun navToOffline() = navController.navigate(Screen.Offline.route) { popUpTo(0) }

    fun navToAboutUs() = navController.navigate(Screen.AboutUs.route)

    fun navToFlashCard() = navController.navigate(Screen.FlashCard.route)

    fun navToCreate() = navController.navigate(Screen.Create.route)

    fun navToPayment(key: String) = navController.navigate(Screen.Payment.route(key))

    fun navToStories(categoryId: Int, title: String) = navController.navigate(Screen.Stories.route(categoryId, title))

    fun navToContent(categoryId: Int, contentId: Int, key: String) = navController.navigate(Screen.Content.route(categoryId, contentId, key))

    fun navToOnBoarding() = navController.navigate(Screen.OnBoarding.route) { popUpTo(0) }

}