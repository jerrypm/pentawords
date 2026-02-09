package com.jeripurnama.pentaword.presentation.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeripurnama.pentaword.R
import com.jeripurnama.pentaword.domain.StageConfig
import kotlinx.coroutines.delay

private val PrimaryColor = Color(0xFF025B62)
private val HighlightColor = Color(0xFFFFD700)
private val ErrorColor = Color(0xFFE53935)
private val SuccessColor = Color(0xFF4CAF50)

enum class MemoryGamePhase {
    SHOWING_SEQUENCE,
    PLAYER_TURN,
    CORRECT,
    WRONG,
    COMPLETED
}

@Composable
fun MemoryNumberScreen(
    stageId: Int,
    requirement: Int, // Number of positions to remember
    timeRemainingMs: Long,
    onComplete: (Long) -> Unit, // completionTime in ms
    onFailed: () -> Unit,
    onBack: () -> Unit
) {
    var gamePhase by remember { mutableStateOf(MemoryGamePhase.SHOWING_SEQUENCE) }
    val sequence = remember { mutableStateListOf<Int>() }
    var currentShowIndex by remember { mutableIntStateOf(-1) }
    var playerInputIndex by remember { mutableIntStateOf(0) }
    var highlightedButton by remember { mutableIntStateOf(-1) }
    var startTime by remember { mutableStateOf(0L) }

    // Initialize sequence
    LaunchedEffect(stageId) {
        sequence.clear()
        val positions = (0..8).shuffled().take(requirement)
        sequence.addAll(positions)
        gamePhase = MemoryGamePhase.SHOWING_SEQUENCE
        startTime = System.currentTimeMillis()
    }

    // Show sequence animation
    LaunchedEffect(gamePhase) {
        if (gamePhase == MemoryGamePhase.SHOWING_SEQUENCE) {
            delay(1000) // Initial delay
            for (i in sequence.indices) {
                currentShowIndex = i
                highlightedButton = sequence[i]
                delay(800) // Show each position
                highlightedButton = -1
                delay(300) // Gap between positions
            }
            currentShowIndex = -1
            gamePhase = MemoryGamePhase.PLAYER_TURN
            playerInputIndex = 0
        }
    }

    // Check for time out
    LaunchedEffect(timeRemainingMs) {
        if (timeRemainingMs <= 0 && gamePhase != MemoryGamePhase.COMPLETED) {
            onFailed()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar with back button and timer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_collapse),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = stringResource(R.string.label_stage, stageId),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Timer
            val minutes = (timeRemainingMs / 1000) / 60
            val seconds = (timeRemainingMs / 1000) % 60
            Text(
                text = String.format("%d:%02d", minutes, seconds),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (timeRemainingMs < 60000) ErrorColor else PrimaryColor
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Game title
        Text(
            text = stringResource(R.string.title_memory_game),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Status message
        Text(
            text = when (gamePhase) {
                MemoryGamePhase.SHOWING_SEQUENCE -> stringResource(R.string.msg_watch_carefully)
                MemoryGamePhase.PLAYER_TURN -> stringResource(R.string.msg_your_turn)
                MemoryGamePhase.CORRECT -> stringResource(R.string.msg_correct)
                MemoryGamePhase.WRONG -> stringResource(R.string.msg_wrong)
                MemoryGamePhase.COMPLETED -> stringResource(R.string.title_stage_complete)
            },
            fontSize = 16.sp,
            color = when (gamePhase) {
                MemoryGamePhase.CORRECT, MemoryGamePhase.COMPLETED -> SuccessColor
                MemoryGamePhase.WRONG -> ErrorColor
                else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            },
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Progress indicator
        Text(
            text = stringResource(R.string.label_progress, playerInputIndex, requirement),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 3x3 Grid of buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in 0..2) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (col in 0..2) {
                        val buttonIndex = row * 3 + col
                        MemoryButton(
                            index = buttonIndex,
                            isHighlighted = highlightedButton == buttonIndex,
                            isClickable = gamePhase == MemoryGamePhase.PLAYER_TURN,
                            gamePhase = gamePhase,
                            onClick = {
                                if (gamePhase == MemoryGamePhase.PLAYER_TURN) {
                                    highlightedButton = buttonIndex

                                    if (sequence[playerInputIndex] == buttonIndex) {
                                        // Correct
                                        playerInputIndex++
                                        if (playerInputIndex >= sequence.size) {
                                            gamePhase = MemoryGamePhase.COMPLETED
                                            val completionTime = System.currentTimeMillis() - startTime
                                            onComplete(completionTime)
                                        } else {
                                            gamePhase = MemoryGamePhase.CORRECT
                                        }
                                    } else {
                                        // Wrong
                                        gamePhase = MemoryGamePhase.WRONG
                                        onFailed()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Continue after correct or restart showing sequence
        LaunchedEffect(gamePhase) {
            if (gamePhase == MemoryGamePhase.CORRECT) {
                delay(500)
                highlightedButton = -1
                gamePhase = MemoryGamePhase.PLAYER_TURN
            }
        }

        // Show result buttons
        if (gamePhase == MemoryGamePhase.COMPLETED) {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor
                )
            ) {
                Text(
                    text = stringResource(R.string.btn_next_stage),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (gamePhase == MemoryGamePhase.WRONG) {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorColor
                )
            ) {
                Text(
                    text = stringResource(R.string.btn_back_to_stages),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun MemoryButton(
    index: Int,
    isHighlighted: Boolean,
    isClickable: Boolean,
    gamePhase: MemoryGamePhase,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isHighlighted && gamePhase == MemoryGamePhase.SHOWING_SEQUENCE -> HighlightColor
            isHighlighted && gamePhase == MemoryGamePhase.CORRECT -> SuccessColor
            isHighlighted && gamePhase == MemoryGamePhase.WRONG -> ErrorColor
            isHighlighted && gamePhase == MemoryGamePhase.PLAYER_TURN -> PrimaryColor
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(200),
        label = "buttonColor"
    )

    val borderColor = when {
        isHighlighted -> backgroundColor
        else -> PrimaryColor.copy(alpha = 0.3f)
    }

    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = isClickable) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = (index + 1).toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (isHighlighted) Color.White else PrimaryColor
        )
    }
}
