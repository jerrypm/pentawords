package com.jeripurnama.pentaword.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jeripurnama.pentaword.R
import com.jeripurnama.pentaword.domain.Language
import com.jeripurnama.pentaword.domain.StageConfig
import com.jeripurnama.pentaword.presentation.components.ActionButtons
import com.jeripurnama.pentaword.presentation.components.FoundWordsList
import com.jeripurnama.pentaword.presentation.components.Honeycomb
import com.jeripurnama.pentaword.presentation.components.ScoreDisplay
import com.jeripurnama.pentaword.presentation.components.WordDisplay
import com.jeripurnama.pentaword.presentation.viewmodel.GameViewModel
import com.jeripurnama.pentaword.presentation.viewmodel.StageViewModel
import kotlinx.coroutines.delay

private val PrimaryColor = Color(0xFF025B62)
private val ErrorColor = Color(0xFFE53935)
private val SuccessColor = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    stageId: Int = 0, // 0 means free play mode (no stage)
    stageRequirement: Int = 0, // Number of words to find
    selectedLanguage: Language = Language.INDONESIAN,
    onStageComplete: (Long) -> Unit = {},
    onStageFailed: () -> Unit = {},
    onBack: () -> Unit = {},
    gameViewModel: GameViewModel = viewModel(),
    stageViewModel: StageViewModel = viewModel()
) {
    val gameState by gameViewModel.gameState.collectAsState()
    val isLoading by gameViewModel.isLoading.collectAsState()

    val isStageMode = stageId > 0
    var timeRemainingMs by remember { mutableLongStateOf(StageConfig.TIMER_DURATION_MS) }
    var wordsFoundInStage by remember { mutableStateOf(0) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    var showFailedDialog by remember { mutableStateOf(false) }
    var stageStartTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Timer countdown
    LaunchedEffect(isStageMode) {
        if (isStageMode) {
            stageStartTime = System.currentTimeMillis()
            timeRemainingMs = StageConfig.TIMER_DURATION_MS
            wordsFoundInStage = 0

            while (timeRemainingMs > 0 && !showCompleteDialog && !showFailedDialog) {
                delay(1000)
                timeRemainingMs -= 1000
                stageViewModel.updateTimer(timeRemainingMs)
            }

            if (timeRemainingMs <= 0 && !showCompleteDialog) {
                showFailedDialog = true
            }
        }
    }

    // Check stage completion
    LaunchedEffect(gameState.foundWords.size) {
        if (isStageMode && !showCompleteDialog && !showFailedDialog) {
            wordsFoundInStage = gameState.foundWords.size
            stageViewModel.updateProgress(wordsFoundInStage)

            if (wordsFoundInStage >= stageRequirement) {
                val completionTime = System.currentTimeMillis() - stageStartTime
                showCompleteDialog = true
                onStageComplete(completionTime)
            }
        }
    }

    // Stage Complete Dialog
    if (showCompleteDialog) {
        StageResultDialog(
            isSuccess = true,
            onDismiss = onBack
        )
    }

    // Stage Failed Dialog
    if (showFailedDialog) {
        StageResultDialog(
            isSuccess = false,
            onDismiss = {
                onStageFailed()
                onBack()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isStageMode) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.label_stage, stageId),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = stringResource(R.string.label_progress, wordsFoundInStage, stageRequirement),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.app_name),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    if (isStageMode) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_collapse)
                            )
                        }
                    }
                },
                actions = {
                    if (isStageMode) {
                        // Timer display
                        val minutes = (timeRemainingMs / 1000) / 60
                        val seconds = (timeRemainingMs / 1000) % 60
                        Text(
                            text = String.format("%d:%02d", minutes, seconds),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (timeRemainingMs < 60000) ErrorColor else PrimaryColor,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    } else {
                        IconButton(
                            onClick = { gameViewModel.generateNewPuzzle() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.cd_new_puzzle)
                            )
                        }
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

                // Score and rank (only show in free play mode)
                if (!isStageMode) {
                    ScoreDisplay(
                        score = gameState.score,
                        maxScore = gameState.maxScore,
                        rank = gameState.rank,
                        wordsFound = gameState.foundWords.size
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Stage progress indicator
                if (isStageMode) {
                    StageProgressIndicator(
                        current = wordsFoundInStage,
                        target = stageRequirement
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

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
                    onLetterClick = { gameViewModel.onLetterClick(it) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Action buttons
                ActionButtons(
                    onDeleteClick = { gameViewModel.onDeleteClick() },
                    onShuffleClick = { gameViewModel.onShuffleClick() },
                    onEnterClick = { gameViewModel.onEnterClick() }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Found words list
                FoundWordsList(
                    foundWords = gameState.foundWords,
                    pangrams = gameState.pangrams,
                    selectedLanguage = selectedLanguage
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun StageProgressIndicator(
    current: Int,
    target: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        Text(
            text = "$current / $target",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = if (current >= target) SuccessColor else PrimaryColor
        )
        Text(
            text = stringResource(R.string.stage_type_words, target),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun StageResultDialog(
    isSuccess: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(24.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isSuccess) {
                    stringResource(R.string.title_stage_complete)
                } else {
                    stringResource(R.string.title_stage_failed)
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSuccess) SuccessColor else ErrorColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isSuccess) {
                    stringResource(R.string.msg_stage_complete)
                } else {
                    stringResource(R.string.msg_stage_failed)
                },
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSuccess) SuccessColor else PrimaryColor
                )
            ) {
                Text(
                    text = if (isSuccess) {
                        stringResource(R.string.btn_next_stage)
                    } else {
                        stringResource(R.string.btn_back_to_stages)
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
