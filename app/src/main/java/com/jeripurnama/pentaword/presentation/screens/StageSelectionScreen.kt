package com.jeripurnama.pentaword.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jeripurnama.pentaword.R
import com.jeripurnama.pentaword.domain.StageType
import com.jeripurnama.pentaword.presentation.viewmodel.StageSelectionUiState
import com.jeripurnama.pentaword.presentation.viewmodel.StageUiModel
import com.jeripurnama.pentaword.presentation.viewmodel.StageViewModel

// Colors as specified
private val UnlockedColor = Color(0xFF025B62)
private val LockedColor = Color(0xFF949499)
private val CompletedColor = Color(0xFF025B62)

@Composable
fun StageSelectionScreen(
    onStageSelected: (Int) -> Unit,
    viewModel: StageViewModel = viewModel()
) {
    val uiState by viewModel.stageSelectionUiState.collectAsState()

    StageSelectionContent(
        uiState = uiState,
        onStageClicked = { stageId ->
            if (viewModel.isStageUnlocked(stageId)) {
                viewModel.startStage(stageId)
                onStageSelected(stageId)
            }
        }
    )
}

@Composable
private fun StageSelectionContent(
    uiState: StageSelectionUiState,
    onStageClicked: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.app_name),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.title_select_stage),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.subtitle_select_stage),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(uiState.stages) { stage ->
                    StageItem(
                        stage = stage,
                        onClick = { onStageClicked(stage.id) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.stage_legend_title),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.stage_legend_find_words),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Text(
                text = stringResource(R.string.stage_legend_memory),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun StageItem(
    stage: StageUiModel,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        stage.isCompleted -> CompletedColor
        stage.isUnlocked -> UnlockedColor
        else -> Color.Transparent
    }

    val borderColor = when {
        stage.isCompleted -> CompletedColor
        stage.isUnlocked -> UnlockedColor
        else -> LockedColor
    }

    val contentColor = when {
        stage.isCompleted -> Color.White
        stage.isUnlocked -> Color.White
        else -> LockedColor
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .border(2.dp, borderColor, CircleShape)
                .clickable(enabled = stage.isUnlocked) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (stage.isUnlocked) {
                if (stage.isCompleted) {
                    // Show star for completed stages
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(R.string.content_desc_completed),
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    // Show stage number for unlocked stages
                    Text(
                        text = stage.id.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
            } else {
                // Show lock icon for locked stages
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = stringResource(R.string.content_desc_locked),
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Show stage type icon/indicator
        Text(
            text = when (stage.type) {
                StageType.FIND_WORDS -> stringResource(R.string.stage_type_words, stage.requirement)
                StageType.MEMORY_NUMBER -> stringResource(R.string.stage_type_chimp, stage.requirement)
            },
            fontSize = 10.sp,
            color = if (stage.isUnlocked) UnlockedColor else LockedColor,
            textAlign = TextAlign.Center
        )

        // Show best time if completed
        if (stage.isCompleted && stage.bestTime != null) {
            val minutes = (stage.bestTime / 1000) / 60
            val seconds = (stage.bestTime / 1000) % 60
            Text(
                text = String.format("%d:%02d", minutes, seconds),
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}
