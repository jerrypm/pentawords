package com.jeripurnama.pentaword.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jeripurnama.pentaword.presentation.screens.GameScreen
import com.jeripurnama.pentaword.presentation.screens.LanguageSelectionScreen
import com.jeripurnama.pentaword.presentation.viewmodel.LanguageViewModel

object Routes {
    const val LANGUAGE_SELECTION = "language_selection"
    const val GAME = "game"
}

@Composable
fun PentawordNavGraph(
    languageViewModel: LanguageViewModel = viewModel()
) {
    val navController = rememberNavController()
    val hasSelectedLanguage by languageViewModel.hasSelectedLanguage.collectAsState()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()

    val startDestination = when (hasSelectedLanguage) {
        null -> return
        true -> Routes.GAME
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
                    navController.navigate(Routes.GAME) {
                        popUpTo(Routes.LANGUAGE_SELECTION) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.GAME) {
            GameScreen(selectedLanguage = selectedLanguage)
        }
    }
}
