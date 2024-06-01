package com.alirezasn80.learn_en.app.navigation

import com.alirezasn80.learn_en.utill.Arg
import com.alirezasn80.learn_en.utill.argumentCount
import com.alirezasn80.learn_en.utill.arguments

sealed class Screen(val route: String) {

    fun route(vararg args: Any?): String {
        var safeRoute = route

        require(args.size == safeRoute.argumentCount) {
            "Provided ${args.count()} parameters, was expected ${safeRoute.argumentCount} parameters!"
        }

        safeRoute.arguments().forEachIndexed { index, matchResult ->
            safeRoute = safeRoute.replace(matchResult.value, args[index].toString())
        }

        return safeRoute
    }

    data object Splash : Screen("Splash")

    data object Offline : Screen("Offline")

    data object OnBoarding : Screen("OnBoarding")


    data object AboutUs : Screen("AboutUs")

    data object FlashCard : Screen("FlashCard")

    data object Create : Screen("Create")

    data object Payment : Screen("Payment/{${Arg.Key}}")

    data object Stories : Screen("Stories/{${Arg.CATEGORY_ID}}/{${Arg.TITLE}}")

    data object Content : Screen("Content/{${Arg.IS_TRIAL}}")

    data object Home : Screen("Home")
}
