package com.jeripurnama.pentaword.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jeripurnama.pentaword.domain.GameProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.gameDataStore: DataStore<Preferences> by preferencesDataStore(name = "game_progress")

class GameProgressRepository(private val context: Context) {

    companion object {
        private val CURRENT_STAGE_KEY = intPreferencesKey("current_stage")
        private val UNLOCKED_STAGES_KEY = stringPreferencesKey("unlocked_stages")
        private val COMPLETED_STAGES_KEY = stringPreferencesKey("completed_stages")
        private val BEST_TIMES_KEY = stringPreferencesKey("best_times")
    }

    /**
     * Flow of the current game progress
     */
    val gameProgress: Flow<GameProgress> = context.gameDataStore.data.map { preferences ->
        val currentStage = preferences[CURRENT_STAGE_KEY] ?: 1
        val unlockedStages = parseIntSet(preferences[UNLOCKED_STAGES_KEY] ?: "1")
        val completedStages = parseIntSet(preferences[COMPLETED_STAGES_KEY] ?: "")
        val bestTimes = parseLongMap(preferences[BEST_TIMES_KEY] ?: "")

        GameProgress(
            currentStage = currentStage,
            unlockedStages = unlockedStages,
            completedStages = completedStages,
            stageBestTimes = bestTimes
        )
    }

    /**
     * Save the current game progress
     */
    suspend fun saveProgress(progress: GameProgress) {
        context.gameDataStore.edit { preferences ->
            preferences[CURRENT_STAGE_KEY] = progress.currentStage
            preferences[UNLOCKED_STAGES_KEY] = progress.unlockedStages.joinToString(",")
            preferences[COMPLETED_STAGES_KEY] = progress.completedStages.joinToString(",")
            preferences[BEST_TIMES_KEY] = progress.stageBestTimes.entries.joinToString(",") { "${it.key}:${it.value}" }
        }
    }

    /**
     * Complete a stage and save progress
     */
    suspend fun completeStage(stageId: Int, completionTime: Long) {
        context.gameDataStore.edit { preferences ->
            // Get current state
            val unlockedStages = parseIntSet(preferences[UNLOCKED_STAGES_KEY] ?: "1").toMutableSet()
            val completedStages = parseIntSet(preferences[COMPLETED_STAGES_KEY] ?: "").toMutableSet()
            val bestTimes = parseLongMap(preferences[BEST_TIMES_KEY] ?: "").toMutableMap()

            // Mark stage as completed
            completedStages.add(stageId)

            // Unlock next stage if available
            val nextStage = stageId + 1
            if (nextStage <= 16) {
                unlockedStages.add(nextStage)
            }

            // Update best time
            val currentBest = bestTimes[stageId]
            if (currentBest == null || completionTime < currentBest) {
                bestTimes[stageId] = completionTime
            }

            // Save updated values
            preferences[UNLOCKED_STAGES_KEY] = unlockedStages.joinToString(",")
            preferences[COMPLETED_STAGES_KEY] = completedStages.joinToString(",")
            preferences[BEST_TIMES_KEY] = bestTimes.entries.joinToString(",") { "${it.key}:${it.value}" }
        }
    }

    /**
     * Reset all progress (when player fails)
     */
    suspend fun resetProgress() {
        context.gameDataStore.edit { preferences ->
            preferences[CURRENT_STAGE_KEY] = 1
            preferences[UNLOCKED_STAGES_KEY] = "1"
            preferences[COMPLETED_STAGES_KEY] = ""
            preferences[BEST_TIMES_KEY] = ""
        }
    }

    /**
     * Update current stage (for navigation)
     */
    suspend fun setCurrentStage(stageId: Int) {
        context.gameDataStore.edit { preferences ->
            preferences[CURRENT_STAGE_KEY] = stageId
        }
    }

    /**
     * Parse comma-separated string to Set<Int>
     */
    private fun parseIntSet(value: String): Set<Int> {
        if (value.isBlank()) return emptySet()
        return value.split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
    }

    /**
     * Parse "key:value,key:value" string to Map<Int, Long>
     */
    private fun parseLongMap(value: String): Map<Int, Long> {
        if (value.isBlank()) return emptyMap()
        return value.split(",")
            .filter { it.contains(":") }
            .mapNotNull { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) {
                    val key = parts[0].trim().toIntOrNull()
                    val longValue = parts[1].trim().toLongOrNull()
                    if (key != null && longValue != null) key to longValue else null
                } else null
            }
            .toMap()
    }
}
