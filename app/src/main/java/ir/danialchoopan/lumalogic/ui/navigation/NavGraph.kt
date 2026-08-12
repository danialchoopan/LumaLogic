package ir.danialchoopan.lumalogic.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ir.danialchoopan.lumalogic.ui.screens.about.AboutScreen
import ir.danialchoopan.lumalogic.ui.screens.debug.DebugScreen
import ir.danialchoopan.lumalogic.ui.screens.editor.LevelEditorScreen
import ir.danialchoopan.lumalogic.ui.screens.game.GameScreen
import ir.danialchoopan.lumalogic.ui.screens.game.GameViewModel
import ir.danialchoopan.lumalogic.ui.screens.home.HomeScreen
import ir.danialchoopan.lumalogic.ui.screens.importexport.ExportLevelScreen
import ir.danialchoopan.lumalogic.ui.screens.importexport.ImportLevelScreen
import ir.danialchoopan.lumalogic.ui.screens.levelselect.LevelSelectScreen
import ir.danialchoopan.lumalogic.ui.screens.settings.SettingsScreen
import ir.danialchoopan.lumalogic.ui.screens.splash.SplashScreen

object LumaDestinations {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val LEVEL_SELECT = "level_select"
    const val GAME = "game"
    const val GAME_WITH_ID = "game/{levelId}"
    const val LEVEL_EDITOR = "level_editor"
    const val LEVEL_EDITOR_WITH_ID = "level_editor/{levelId}"
    const val IMPORT_LEVEL = "import_level"
    const val EXPORT_LEVEL = "export_level/{levelId}"
    const val DEBUG = "debug"
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
                onPlayClick = { navController.navigate(LumaDestinations.LEVEL_SELECT) },
                onLevelSelectClick = { navController.navigate(LumaDestinations.LEVEL_SELECT) },
                onLevelEditorClick = { navController.navigate(LumaDestinations.LEVEL_EDITOR) },
                onImportClick = { navController.navigate(LumaDestinations.IMPORT_LEVEL) },
                onSettingsClick = { navController.navigate(LumaDestinations.SETTINGS) },
                onAboutClick = { navController.navigate(LumaDestinations.ABOUT) }
            )
        }

        composable(LumaDestinations.LEVEL_SELECT) {
            LevelSelectScreen(
                onBackClick = { navController.popBackStack() },
                onLevelSelected = { levelId ->
                    navController.navigate("game/$levelId")
                },
                onCreateNewLevel = {
                    navController.navigate(LumaDestinations.LEVEL_EDITOR)
                },
                onEditLevel = { levelId ->
                    navController.navigate("level_editor/$levelId")
                },
                onExportLevel = { levelId ->
                    navController.navigate("export_level/$levelId")
                },
                onImportLevel = {
                    navController.navigate(LumaDestinations.IMPORT_LEVEL)
                }
            )
        }

        composable(
            route = LumaDestinations.GAME_WITH_ID,
            arguments = listOf(navArgument("levelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val levelId = backStackEntry.arguments?.getString("levelId")
            val gameViewModel: GameViewModel = viewModel()
            LaunchedEffect(levelId) {
                gameViewModel.loadLevel(levelId)
            }
            GameScreen(
                viewModel = gameViewModel,
                onBackClick = { navController.popBackStack() },
                onNextLevelClick = { nextLevelId ->
                    navController.navigate("game/$nextLevelId") {
                        popUpTo(LumaDestinations.LEVEL_SELECT)
                    }
                },
                onSettingsClick = { navController.navigate(LumaDestinations.SETTINGS) },
                onDebugClick = { navController.navigate(LumaDestinations.DEBUG) }
            )
        }

        composable(LumaDestinations.GAME) {
            val gameViewModel: GameViewModel = viewModel()
            GameScreen(
                viewModel = gameViewModel,
                onBackClick = { navController.popBackStack() },
                onNextLevelClick = { nextLevelId ->
                    navController.navigate("game/$nextLevelId") {
                        popUpTo(LumaDestinations.LEVEL_SELECT)
                    }
                },
                onSettingsClick = { navController.navigate(LumaDestinations.SETTINGS) },
                onDebugClick = { navController.navigate(LumaDestinations.DEBUG) }
            )
        }

        composable(LumaDestinations.LEVEL_EDITOR) {
            LevelEditorScreen(
                levelId = null,
                onBackClick = { navController.popBackStack() },
                onTestLevel = { levelId ->
                    navController.navigate("game/$levelId")
                }
            )
        }

        composable(
            route = LumaDestinations.LEVEL_EDITOR_WITH_ID,
            arguments = listOf(navArgument("levelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val levelId = backStackEntry.arguments?.getString("levelId")
            LevelEditorScreen(
                levelId = levelId,
                onBackClick = { navController.popBackStack() },
                onTestLevel = { testedId ->
                    navController.navigate("game/$testedId")
                }
            )
        }

        composable(LumaDestinations.IMPORT_LEVEL) {
            ImportLevelScreen(
                onBackClick = { navController.popBackStack() },
                onImportSuccess = { importedId ->
                    navController.navigate("game/$importedId") {
                        popUpTo(LumaDestinations.IMPORT_LEVEL) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = LumaDestinations.EXPORT_LEVEL,
            arguments = listOf(navArgument("levelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val levelId = backStackEntry.arguments?.getString("levelId") ?: ""
            ExportLevelScreen(
                levelId = levelId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(LumaDestinations.DEBUG) {
            val gameViewModel: GameViewModel = viewModel()
            DebugScreen(
                viewModel = gameViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(LumaDestinations.SETTINGS) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onOpenDebugClick = { navController.navigate(LumaDestinations.DEBUG) }
            )
        }

        composable(LumaDestinations.ABOUT) {
            AboutScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
