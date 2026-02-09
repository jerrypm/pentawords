package com.jeripurnama.pentaword.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jeripurnama.pentaword.data.GameProgressRepository
import com.jeripurnama.pentaword.domain.GameProgress
import com.jeripurnama.pentaword.domain.Stage
import com.jeripurnama.pentaword.domain.StageConfig
import com.jeripurnama.pentaword.domain.StageType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UI state for stage selection screen
 */
data class StageSelectionUiState(
    val stages: List<StageUiModel> = emptyList(),
    val isLoading: Boolean = true
)

/**
 * UI model for a single stage
 */
data class StageUiModel(
    val id: Int,
    val type: StageType,
    val requirement: Int,
    val isUnlocked: Boolean,
    val isCompleted: Boolean,
    val bestTime: Long? = null
)

/**
 * UI state for active game stage
 */
data class StageGameUiState(
    val stageId: Int = 1,
    val stageType: StageType = StageType.FIND_WORDS,
    val requirement: Int = 1,
    val timeRemainingMs: Long = StageConfig.TIMER_DURATION_MS,
    val progress: Int = 0, // Words found or correct positions for memory
    val isCompleted: Boolean = false,
    val isFailed: Boolean = false,
    val isPaused: Boolean = false
)

class StageViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameProgressRepository(application)

    private val _gameProgress = MutableStateFlow(GameProgress())

    /**
     * Combined flow of stage selection UI state
     */
    val stageSelectionUiState: StateFlow<StageSelectionUiState> = combine(
        repository.gameProgress,
        MutableStateFlow(StageConfig.getAllStages())
    ) { progress, stages ->
        StageSelectionUiState(
            stages = stages.map { stage ->
                StageUiModel(
                    id = stage.id,
                    type = stage.type,
                    requirement = stage.requirement,
                    isUnlocked = progress.isStageUnlocked(stage.id),
                    isCompleted = progress.isStageCompleted(stage.id),
                    bestTime = progress.stageBestTimes[stage.id]
                )
            },
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StageSelectionUiState()
    )

    private val _stageGameUiState = MutableStateFlow(StageGameUiState())
    val stageGameUiState: StateFlow<StageGameUiState> = _stageGameUiState.asStateFlow()

    private val _currentProgress = MutableStateFlow(GameProgress())
    val currentProgress: StateFlow<GameProgress> = _currentProgress.asStateFlow()

    init {
        viewModelScope.launch {
            repository.gameProgress.collect { progress ->
                _gameProgress.value = progress
                _currentProgress.value = progress
            }
        }
    }

    /**
     * Start a stage game
     */
    fun startStage(stageId: Int) {
        val stageConfig = StageConfig.getStageConfig(stageId)
        _stageGameUiState.value = StageGameUiState(
            stageId = stageId,
            stageType = stageConfig.type,
            requirement = stageConfig.requirement,
            timeRemainingMs = StageConfig.TIMER_DURATION_MS,
            progress = 0,
            isCompleted = false,
            isFailed = false,
            isPaused = false
        )
        viewModelScope.launch {
            repository.setCurrentStage(stageId)
        }
    }

    /**
     * Update timer (called every second)
     */
    fun updateTimer(remainingMs: Long) {
        _stageGameUiState.value = _stageGameUiState.value.copy(
            timeRemainingMs = remainingMs
        )
        if (remainingMs <= 0) {
            onStageFailed()
        }
    }

    /**
     * Update progress (e.g., word found or correct memory position)
     */
    fun updateProgress(newProgress: Int) {
        val currentState = _stageGameUiState.value
        _stageGameUiState.value = currentState.copy(progress = newProgress)

        // Check if stage is completed
        if (newProgress >= currentState.requirement) {
            onStageCompleted()
        }
    }

    /**
     * Called when player completes a stage
     */
    private fun onStageCompleted() {
        val currentState = _stageGameUiState.value
        val completionTime = StageConfig.TIMER_DURATION_MS - currentState.timeRemainingMs

        _stageGameUiState.value = currentState.copy(isCompleted = true)

        viewModelScope.launch {
            repository.completeStage(currentState.stageId, completionTime)
        }
    }

    /**
     * Called when player fails a stage (time runs out)
     */
    fun onStageFailed() {
        _stageGameUiState.value = _stageGameUiState.value.copy(isFailed = true)

        viewModelScope.launch {
            repository.resetProgress()
        }
    }

    /**
     * Pause the game
     */
    fun pauseGame() {
        _stageGameUiState.value = _stageGameUiState.value.copy(isPaused = true)
    }

    /**
     * Resume the game
     */
    fun resumeGame() {
        _stageGameUiState.value = _stageGameUiState.value.copy(isPaused = false)
    }

    /**
     * Check if a stage is unlocked
     */
    fun isStageUnlocked(stageId: Int): Boolean {
        return _gameProgress.value.isStageUnlocked(stageId)
    }

    /**
     * Get the next stage after current
     */
    fun getNextStageId(): Int? {
        val currentStageId = _stageGameUiState.value.stageId
        return if (currentStageId < StageConfig.TOTAL_STAGES) currentStageId + 1 else null
    }

    /**
     * Reset the stage game state for retry
     */
    fun resetStageGame() {
        val currentStageId = _stageGameUiState.value.stageId
        startStage(currentStageId)
    }
}
