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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

private val PrimaryColor = Color(0xFF025B62)
private val ErrorColor = Color(0xFFE53935)
private val SuccessColor = Color(0xFF4CAF50)
private val CellNumberColor = Color.White            // WHITE boxes like Human Benchmark
private val CellEmptyColor = Color(0xFFF5F5F5)       // Almost invisible background

/**
 * Game phases for Chimp Test
 */
enum class ChimpTestPhase {
    SHOWING_NUMBERS,  // All numbers visible, waiting for player to click "1"
    NUMBERS_HIDDEN,   // Numbers hidden, player clicking remaining numbers
    WRONG,            // Player clicked wrong position
    COMPLETED         // All numbers clicked correctly
}

/**
 * Represents a cell in the grid
 */
data class GridCell(
    val position: Int,        // Position in grid (0-19 for 4x5 grid)
    val number: Int?,         // The number displayed (1-N), null if empty cell
    val isClicked: Boolean = false
)

@Composable
fun MemoryNumberScreen(
    stageId: Int,
    requirement: Int, // Number of numbers to remember (5-9)
    timeRemainingMs: Long,
    onComplete: (Long) -> Unit,
    onFailed: () -> Unit,
    onBack: () -> Unit
) {
    val gridRows = 5
    val gridCols = 4
    val totalCells = gridRows * gridCols

    var gamePhase by remember { mutableStateOf(ChimpTestPhase.SHOWING_NUMBERS) }
    var nextExpectedNumber by remember { mutableIntStateOf(1) }
    var startTime by remember { mutableStateOf(0L) }
    var lastClickedPosition by remember { mutableIntStateOf(-1) }

    // Generate random positions for numbers
    val gridCells = remember(stageId, requirement) {
        val randomPositions = (0 until totalCells).shuffled().take(requirement)
        val cells = MutableList(totalCells) { pos ->
            GridCell(position = pos, number = null)
        }
        randomPositions.forEachIndexed { index, pos ->
            cells[pos] = GridCell(position = pos, number = index + 1)
        }
        cells
    }

    // Track which cells have been clicked
    var clickedCells by remember { mutableStateOf(setOf<Int>()) }

    // Initialize game
    LaunchedEffect(stageId) {
        gamePhase = ChimpTestPhase.SHOWING_NUMBERS
        nextExpectedNumber = 1
        clickedCells = emptySet()
        startTime = System.currentTimeMillis()
    }

    // Check for time out
    LaunchedEffect(timeRemainingMs) {
        if (timeRemainingMs <= 0 && gamePhase != ChimpTestPhase.COMPLETED && gamePhase != ChimpTestPhase.WRONG) {
            gamePhase = ChimpTestPhase.WRONG
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
        // Top bar with back button, stage info, and timer
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

        Spacer(modifier = Modifier.height(24.dp))

        // Game title
        Text(
            text = stringResource(R.string.title_chimp_test),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Instructions / Status
        Text(
            text = when (gamePhase) {
                ChimpTestPhase.SHOWING_NUMBERS -> stringResource(R.string.msg_chimp_click_one)
                ChimpTestPhase.NUMBERS_HIDDEN -> stringResource(R.string.msg_chimp_continue, nextExpectedNumber)
                ChimpTestPhase.WRONG -> stringResource(R.string.msg_wrong)
                ChimpTestPhase.COMPLETED -> stringResource(R.string.title_stage_complete)
            },
            fontSize = 16.sp,
            color = when (gamePhase) {
                ChimpTestPhase.COMPLETED -> SuccessColor
                ChimpTestPhase.WRONG -> ErrorColor
                else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            },
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Progress indicator
        Text(
            text = stringResource(R.string.label_progress, nextExpectedNumber - 1, requirement),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4x5 Grid
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            for (row in 0 until gridRows) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0 until gridCols) {
                        val cellIndex = row * gridCols + col
                        val cell = gridCells[cellIndex]

                        ChimpCell(
                            cell = cell,
                            showNumber = when {
                                cell.number == null -> false
                                gamePhase == ChimpTestPhase.SHOWING_NUMBERS -> true
                                cellIndex in clickedCells -> false
                                else -> false
                            },
                            isClickable = gamePhase == ChimpTestPhase.SHOWING_NUMBERS ||
                                    gamePhase == ChimpTestPhase.NUMBERS_HIDDEN,
                            isClicked = cellIndex in clickedCells,
                            isWrongClick = gamePhase == ChimpTestPhase.WRONG && lastClickedPosition == cellIndex,
                            hasNumber = cell.number != null,
                            onClick = {
                                if (gamePhase == ChimpTestPhase.WRONG || gamePhase == ChimpTestPhase.COMPLETED) {
                                    return@ChimpCell
                                }

                                val clickedNumber = cell.number

                                if (clickedNumber == null) {
                                    // Clicked empty cell - ignore (like Human Benchmark)
                                    return@ChimpCell
                                }

                                if (clickedNumber == nextExpectedNumber) {
                                    // Correct!
                                    clickedCells = clickedCells + cellIndex

                                    if (nextExpectedNumber == 1) {
                                        // First number clicked, hide all other numbers
                                        gamePhase = ChimpTestPhase.NUMBERS_HIDDEN
                                    }

                                    nextExpectedNumber++

                                    if (nextExpectedNumber > requirement) {
                                        // All numbers clicked correctly!
                                        gamePhase = ChimpTestPhase.COMPLETED
                                        val completionTime = System.currentTimeMillis() - startTime
                                        onComplete(completionTime)
                                    }
                                } else {
                                    // Wrong number clicked
                                    lastClickedPosition = cellIndex
                                    gamePhase = ChimpTestPhase.WRONG
                                    onFailed()
                                }
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Result buttons
        when (gamePhase) {
            ChimpTestPhase.COMPLETED -> {
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SuccessColor
                    )
                ) {
                    Text(
                        text = stringResource(R.string.btn_next_stage),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            ChimpTestPhase.WRONG -> {
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
            else -> { /* No button shown during gameplay */ }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ChimpCell(
    cell: GridCell,
    showNumber: Boolean,
    isClickable: Boolean,
    isClicked: Boolean,
    isWrongClick: Boolean,
    hasNumber: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isWrongClick -> ErrorColor
            isClicked -> CellEmptyColor              // Clicked cells fade out
            hasNumber -> CellNumberColor             // WHITE boxes for number cells
            else -> CellEmptyColor                   // Empty cells almost invisible
        },
        animationSpec = tween(150),
        label = "cellColor"
    )

    val textColor = when {
        isWrongClick -> Color.White
        showNumber -> PrimaryColor                   // Teal text on white background
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .width(70.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = if (hasNumber && !isClicked) 2.dp else 0.dp,
                color = if (hasNumber && !isClicked) PrimaryColor.copy(alpha = 0.3f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = isClickable && !isClicked) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (showNumber && cell.number != null) {
            Text(
                text = cell.number.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
