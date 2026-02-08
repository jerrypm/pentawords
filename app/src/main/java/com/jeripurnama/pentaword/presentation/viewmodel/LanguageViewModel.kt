package com.jeripurnama.pentaword.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jeripurnama.pentaword.data.LanguagePreferences
import com.jeripurnama.pentaword.domain.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LanguageViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = LanguagePreferences(application)

    private val _selectedLanguage = MutableStateFlow(Language.INDONESIAN)
    val selectedLanguage: StateFlow<Language> = _selectedLanguage.asStateFlow()

    private val _hasSelectedLanguage = MutableStateFlow<Boolean?>(null)
    val hasSelectedLanguage: StateFlow<Boolean?> = _hasSelectedLanguage.asStateFlow()

    init {
        loadLanguagePreference()
    }

    private fun loadLanguagePreference() {
        viewModelScope.launch {
            _hasSelectedLanguage.value = preferences.hasSelectedLanguage.first()
            _selectedLanguage.value = preferences.selectedLanguage.first()
        }
    }

    fun setLanguage(language: Language) {
        viewModelScope.launch {
            preferences.setLanguage(language)
            _selectedLanguage.value = language
            _hasSelectedLanguage.value = true
        }
    }
}
