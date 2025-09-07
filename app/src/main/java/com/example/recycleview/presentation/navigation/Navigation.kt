package com.example.recycleview.presentation.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.recycleview.presentation.details.DetailsScreen
import com.example.recycleview.presentation.edit.EditScreen
import com.example.recycleview.presentation.home.HomeScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ScreenNavigation.HomeScreen.route
    ) {
        composable(
            route = ScreenNavigation.HomeScreen.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(durationMillis = 400)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 400)
                )
            }
        ) {
            HomeScreen(navController = navController)
        }

        composable(
            route = ScreenNavigation.EditScreen.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(durationMillis = 400)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 400)
                )
            }
        ) {
            EditScreen(plantId = null, navController = navController)
        }

        composable(
            route = ScreenNavigation.EditScreen.route + "/{plant_id}",
            arguments = listOf(
                navArgument("plant_id") {
                    type = NavType.StringType
                }
            ),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(
                    animationSpec = tween(durationMillis = 400)
                )
            },
            exitTransition = {
                slideOutHorizontally (
                    targetOffsetX = { 1000 },
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(
                    animationSpec = tween(durationMillis = 400)
                )
            },
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.getString("plant_id")?.let { plantId ->
                EditScreen(plantId = plantId, navController = navController)
            }
        }

        composable(
            route = ScreenNavigation.DetailsScreen.route + "/{plant_id}",
            arguments = listOf(
                navArgument("plant_id") {
                    type = NavType.StringType
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "greenhouse://plant/{plant_id}" }
            ),
            enterTransition = {
                fadeIn(
                    animationSpec = tween(durationMillis = 400)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 400)
                )
            }
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.getString("plant_id")?.let {
                DetailsScreen(plantId = it, navController = navController)
            }
        }
    }
}