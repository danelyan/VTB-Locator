package ru.vtb24.mobilebanking.android.locator.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.vtb24.mobilebanking.android.locator.presentation.common.Screen
import ru.vtb24.mobilebanking.android.locator.presentation.history.History
import ru.vtb24.mobilebanking.android.locator.presentation.home.BottomBar
import ru.vtb24.mobilebanking.android.locator.presentation.home.HomeScreen
import ru.vtb24.mobilebanking.android.locator.presentation.onboarding.OnBoardingScreen


@Composable
fun NavigationGraph(
    navController: NavHostController,
    startDestination: String,
    scrollState: LazyListState
) {

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            route = Screen.Onboarding.route,
        ) {
            OnBoardingScreen(navController = navController)
        }
//        composable(
//            route = Screen.LoginScreen.route,
//        ) {
//            LoginScreen(navController = navController)
//        }

        composable(
            route = Screen.Home.route
        ) {
            HomeScreen(navController = navController, scrollState = scrollState)
        }
        composable(
            route = Screen.History.route
        ) {
            History(navHostController = navController)
        }
//        composable(
//            route = Screen.Cart.route
//        ) {
//            Cart(navController = navController)
//        }
//        composable(
//            route = Screen.Profile.route
//        ) {
//            Profile(navController = navController)
//        }
//
//        composable(
//            route = Screen.Onboarding.route,
//        ) {
//            OnBoardingScreen(navController = navController)
//        }
//        composable(
//            route = Screen.RestaurantDetails.route,
//        ) {
//            RestaurantDetail(
//                navController = navController
//            )
//        }
    }

}

@Composable
fun SetupNavigation(startDestination: String) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scrollState = rememberLazyListState()
    val state by remember { derivedStateOf { scrollState.firstVisibleItemIndex == 0 } }

    Scaffold(
        bottomBar = {
            if ((currentRoute == Screen.Home.route || currentRoute == Screen.History.route) && state) {

                Column(
                    modifier = Modifier.padding(115.dp, 25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        BottomBar(navController = navController)
                        Column {
                            FloatingActionButton(
                                onClick = {
                                    navController.navigate(Screen.Cart.route)

                                },
                                backgroundColor = MaterialTheme.colors.primary
                            ) {
                                Icon(Icons.Outlined.ShoppingCart, "Cart")
                            }
                            Spacer(modifier = Modifier.height(26.dp))
                        }
                    }
                }
            }
        }
    ) { _ ->
        NavigationGraph(
            navController = navController,
            scrollState = scrollState,
            startDestination = startDestination
        )
    }
}



