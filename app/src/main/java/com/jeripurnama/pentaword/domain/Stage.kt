package com.jeripurnama.pentaword.domain

/**
 * Represents the type of game for each stage
 */
enum class StageType {
    FIND_WORDS,      // Player must find a certain number of words
    MEMORY_NUMBER    // Player must remember and repeat number sequence
}

/**
 * Represents a single stage in the game
 */
data class Stage(
    val id: Int,
    val type: StageType,
    val requirement: Int,  // For FIND_WORDS: number of words to find, for MEMORY_NUMBER: sequence length
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val bestTime: Long? = null  // Best completion time in milliseconds
)

/**
 * Configuration for all 16 stages in version 1.0.0
 * Pattern: 2 FIND_WORDS stages, then 1 MEMORY_NUMBER stage, repeat
 */
object StageConfig {
    const val TOTAL_STAGES = 16
    const val TIMER_DURATION_MS = 5 * 60 * 1000L  // 5 minutes in milliseconds

    /**
     * Get the stage configuration for a given stage ID (1-16)
     */
    fun getStageConfig(stageId: Int): Stage {
        require(stageId in 1..TOTAL_STAGES) { "Stage ID must be between 1 and $TOTAL_STAGES" }

        val type = getStageType(stageId)
        val requirement = getRequirement(stageId, type)

        return Stage(
            id = stageId,
            type = type,
            requirement = requirement,
            isUnlocked = stageId == 1  // Only stage 1 is unlocked by default
        )
    }

    /**
     * Get all stages configuration
     */
    fun getAllStages(): List<Stage> {
        return (1..TOTAL_STAGES).map { getStageConfig(it) }
    }

    /**
     * Determine stage type based on stage ID
     * Pattern: Stage 1-2 = FIND_WORDS, Stage 3 = MEMORY, Stage 4-5 = FIND_WORDS, Stage 6 = MEMORY, etc.
     */
    private fun getStageType(stageId: Int): StageType {
        // Every 3rd stage (3, 6, 9, 12, 15) is MEMORY_NUMBER
        return if (stageId % 3 == 0) {
            StageType.MEMORY_NUMBER
        } else {
            StageType.FIND_WORDS
        }
    }

    /**
     * Get requirement based on stage ID and type
     * - FIND_WORDS: Stage 1 = 1 word, Stage 2 = 2 words, Stage 4 = 3 words, Stage 5 = 4 words, etc.
     * - MEMORY_NUMBER: Stage 3 = 4 positions, Stage 6 = 5 positions, Stage 9 = 6 positions, etc.
     */
    private fun getRequirement(stageId: Int, type: StageType): Int {
        return when (type) {
            StageType.FIND_WORDS -> {
                // Calculate word requirement
                // Stage 1 = 1, Stage 2 = 2, Stage 4 = 3, Stage 5 = 4, Stage 7 = 5, Stage 8 = 6, etc.
                val groupIndex = (stageId - 1) / 3  // 0 for 1-3, 1 for 4-6, 2 for 7-9, etc.
                val positionInGroup = (stageId - 1) % 3  // 0, 1, or 2 within group
                groupIndex * 2 + positionInGroup + 1
            }
            StageType.MEMORY_NUMBER -> {
                // Stage 3 = 4 positions, Stage 6 = 5, Stage 9 = 6, Stage 12 = 7, Stage 15 = 8
                3 + (stageId / 3)
            }
        }
    }
}

/**
 * Represents the current game progress
 */
data class GameProgress(
    val currentStage: Int = 1,
    val unlockedStages: Set<Int> = setOf(1),
    val completedStages: Set<Int> = emptySet(),
    val stageBestTimes: Map<Int, Long> = emptyMap()
) {
    /**
     * Check if a stage is unlocked
     */
    fun isStageUnlocked(stageId: Int): Boolean = stageId in unlockedStages

    /**
     * Check if a stage is completed
     */
    fun isStageCompleted(stageId: Int): Boolean = stageId in completedStages

    /**
     * Get the next stage after completing current
     */
    fun getNextStage(currentStageId: Int): Int? {
        val next = currentStageId + 1
        return if (next <= StageConfig.TOTAL_STAGES) next else null
    }

    /**
     * Create updated progress after completing a stage
     */
    fun completeStage(stageId: Int, completionTime: Long): GameProgress {
        val nextStage = getNextStage(stageId)
        val newUnlocked = if (nextStage != null) unlockedStages + nextStage else unlockedStages
        val newCompleted = completedStages + stageId
        val newBestTimes = if (stageBestTimes[stageId] == null || completionTime < stageBestTimes[stageId]!!) {
            stageBestTimes + (stageId to completionTime)
        } else {
            stageBestTimes
        }

        return copy(
            unlockedStages = newUnlocked,
            completedStages = newCompleted,
            stageBestTimes = newBestTimes
        )
    }

    /**
     * Reset progress (when player fails)
     */
    fun reset(): GameProgress {
        return GameProgress()
    }
}
