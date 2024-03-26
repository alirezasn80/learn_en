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

    fun navToCleanup() = navController.navigate(Screen.Cleanup.route)

    fun navToSearchId() = navController.navigate(Screen.SearchId.route)

    fun navToAboutUs() = navController.navigate(Screen.AboutUs.route)

    fun navToPayment(key: String) = navController.navigate(Screen.Payment.route(key))

    fun navToItems(type: String) = navController.navigate(Screen.Items.route(type))

    fun navToOnBoarding() = navController.navigate(Screen.OnBoarding.route) { popUpTo(0) }

}