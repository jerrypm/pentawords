package com.jeripurnama.pentaword.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jeripurnama.pentaword.domain.StageConfig
import com.jeripurnama.pentaword.domain.StageType
import com.jeripurnama.pentaword.presentation.screens.GameScreen
import com.jeripurnama.pentaword.presentation.screens.LanguageSelectionScreen
import com.jeripurnama.pentaword.presentation.screens.MemoryNumberScreen
import com.jeripurnama.pentaword.presentation.screens.StageSelectionScreen
import com.jeripurnama.pentaword.presentation.viewmodel.LanguageViewModel
import com.jeripurnama.pentaword.presentation.viewmodel.StageViewModel
import kotlinx.coroutines.delay

object Routes {
    const val LANGUAGE_SELECTION = "language_selection"
    const val STAGE_SELECTION = "stage_selection"
    const val GAME = "game"
    const val STAGE_GAME = "stage_game/{stageId}/{stageType}/{requirement}"
    const val MEMORY_GAME = "memory_game/{stageId}/{requirement}"

    fun stageGame(stageId: Int, stageType: StageType, requirement: Int): String {
        return "stage_game/$stageId/${stageType.name}/$requirement"
    }

    fun memoryGame(stageId: Int, requirement: Int): String {
        return "memory_game/$stageId/$requirement"
    }
}

@Composable
fun PentawordNavGraph(
    languageViewModel: LanguageViewModel = viewModel(),
    stageViewModel: StageViewModel = viewModel()
) {
    val navController = rememberNavController()
    val hasSelectedLanguage by languageViewModel.hasSelectedLanguage.collectAsState()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()

    val startDestination = when (hasSelectedLanguage) {
        null -> return
        true -> Routes.STAGE_SELECTION
        false -> Routes.LANGUAGE_SELECTION
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LANGUAGE_SELECTION) {
            LanguageSelectionScreen(
                initialLanguage = selectedLanguage,
                onLanguageSelected = { language ->
                    languageViewModel.setLanguage(language)
                    navController.navigate(Routes.STAGE_SELECTION) {
                        popUpTo(Routes.LANGUAGE_SELECTION) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.STAGE_SELECTION) {
            StageSelectionScreen(
                onStageSelected = { stageId ->
                    val stageConfig = StageConfig.getStageConfig(stageId)
                    when (stageConfig.type) {
                        StageType.FIND_WORDS -> {
                            navController.navigate(
                                Routes.stageGame(stageId, stageConfig.type, stageConfig.requirement)
                            )
                        }
                        StageType.MEMORY_NUMBER -> {
                            navController.navigate(
                                Routes.memoryGame(stageId, stageConfig.requirement)
                            )
                        }
                    }
                },
                viewModel = stageViewModel
            )
        }

        composable(Routes.GAME) {
            GameScreen(selectedLanguage = selectedLanguage)
        }

        composable(
            route = Routes.STAGE_GAME,
            arguments = listOf(
                navArgument("stageId") { type = NavType.IntType },
                navArgument("stageType") { type = NavType.StringType },
                navArgument("requirement") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val stageId = backStackEntry.arguments?.getInt("stageId") ?: 1
            val requirement = backStackEntry.arguments?.getInt("requirement") ?: 1

            GameScreen(
                stageId = stageId,
                stageRequirement = requirement,
                selectedLanguage = selectedLanguage,
                onStageComplete = { completionTime ->
                    // Progress is saved in StageViewModel
                },
                onStageFailed = {
                    stageViewModel.onStageFailed()
                },
                onBack = {
                    navController.popBackStack(Routes.STAGE_SELECTION, inclusive = false)
                },
                stageViewModel = stageViewModel
            )
        }

        composable(
            route = Routes.MEMORY_GAME,
            arguments = listOf(
                navArgument("stageId") { type = NavType.IntType },
                navArgument("requirement") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val stageId = backStackEntry.arguments?.getInt("stageId") ?: 3
            val requirement = backStackEntry.arguments?.getInt("requirement") ?: 4

            var timeRemainingMs by remember { mutableLongStateOf(StageConfig.TIMER_DURATION_MS) }

            // Timer countdown for memory game
            LaunchedEffect(Unit) {
                while (timeRemainingMs > 0) {
                    delay(1000)
                    timeRemainingMs -= 1000
                    stageViewModel.updateTimer(timeRemainingMs)
                }
            }

            MemoryNumberScreen(
                stageId = stageId,
                requirement = requirement,
                timeRemainingMs = timeRemainingMs,
                onComplete = { completionTime ->
                    stageViewModel.updateProgress(requirement)
                },
                onFailed = {
                    stageViewModel.onStageFailed()
                },
                onBack = {
                    navController.popBackStack(Routes.STAGE_SELECTION, inclusive = false)
                }
            )
        }
    }
}
