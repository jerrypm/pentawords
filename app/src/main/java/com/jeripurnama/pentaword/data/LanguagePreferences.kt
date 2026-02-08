package com.jeripurnama.pentaword.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jeripurnama.pentaword.domain.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LanguagePreferences(private val context: Context) {

    private val languageKey = stringPreferencesKey("selected_language")
    private val hasSelectedKey = booleanPreferencesKey("has_selected_language")

    val selectedLanguage: Flow<Language> = context.dataStore.data.map { preferences ->
        val code = preferences[languageKey] ?: Language.INDONESIAN.code
        Language.fromCode(code)
    }

    val hasSelectedLanguage: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[hasSelectedKey] ?: false
    }

    suspend fun setLanguage(language: Language) {
        context.dataStore.edit { preferences ->
            preferences[languageKey] = language.code
            preferences[hasSelectedKey] = true
        }
    }
}
