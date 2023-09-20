package ru.vtb24.mobilebanking.android.locator.presentation.common

sealed class Screen(val route: String) {
    data object Onboarding : Screen(route = "onboarding")
    data object LoginScreen : Screen(route = "login_screen")
    data object Home : Screen(route = "home_screen")
    data object History : Screen(route = "history")
    data object Cart : Screen(route = "cart")
    data object Profile : Screen(route = "profile")
    data object RestaurantDetails: Screen(route = "restaurant_details")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
