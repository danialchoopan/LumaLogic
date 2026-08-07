package ir.danialchoopan.lumalogic.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ir.danialchoopan.lumalogic.ui.screens.about.AboutScreen
import ir.danialchoopan.lumalogic.ui.screens.game.GameScreen
import ir.danialchoopan.lumalogic.ui.screens.home.HomeScreen
import ir.danialchoopan.lumalogic.ui.screens.settings.SettingsScreen
import ir.danialchoopan.lumalogic.ui.screens.splash.SplashScreen

object LumaDestinations {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val GAME = "game"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
}

@Composable
fun LumaNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = LumaDestinations.SPLASH,
        enterTransition = {
            fadeIn(animationSpec = tween(400)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(400)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(400)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(400)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            )
        }
    ) {
        composable(LumaDestinations.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(LumaDestinations.HOME) {
                        popUpTo(LumaDestinations.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(LumaDestinations.HOME) {
            HomeScreen(
                onPlayClick = { navController.navigate(LumaDestinations.GAME) },
                onSettingsClick = { navController.navigate(LumaDestinations.SETTINGS) },
                onAboutClick = { navController.navigate(LumaDestinations.ABOUT) }
            )
        }

        composable(LumaDestinations.GAME) {
            GameScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(LumaDestinations.SETTINGS) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(LumaDestinations.ABOUT) {
            AboutScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
