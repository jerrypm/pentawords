package com.jeripurnama.pentaword.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jeripurnama.pentaword.presentation.components.ActionButtons
import com.jeripurnama.pentaword.presentation.components.FoundWordsList
import com.jeripurnama.pentaword.presentation.components.Honeycomb
import com.jeripurnama.pentaword.presentation.components.ScoreDisplay
import com.jeripurnama.pentaword.presentation.components.WordDisplay
import com.jeripurnama.pentaword.presentation.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pentaword",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.generateNewPuzzle() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "New Puzzle"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Score and rank
                ScoreDisplay(
                    score = gameState.score,
                    maxScore = gameState.maxScore,
                    rank = gameState.rank,
                    wordsFound = gameState.foundWords.size
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Current word display
                WordDisplay(
                    currentWord = gameState.currentWord,
                    centerLetter = gameState.centerLetter,
                    message = gameState.message
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Honeycomb
                Honeycomb(
                    centerLetter = gameState.centerLetter,
                    outerLetters = gameState.outerLetters,
                    onLetterClick = { viewModel.onLetterClick(it) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Action buttons
                ActionButtons(
                    onDeleteClick = { viewModel.onDeleteClick() },
                    onShuffleClick = { viewModel.onShuffleClick() },
                    onEnterClick = { viewModel.onEnterClick() }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Found words list
                FoundWordsList(
                    foundWords = gameState.foundWords,
                    pangrams = gameState.pangrams
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
