package com.jeripurnama.pentaword.data

import android.content.Context
import com.jeripurnama.pentaword.domain.Language

class TranslationRepository(private val context: Context) {

    private val translationsCache = mutableMapOf<Language, Map<String, String>>()

    fun getTranslation(word: String, language: Language): String? {
        val translations = getTranslationsForLanguage(language)
        return translations[word.lowercase()]
    }

    private fun getTranslationsForLanguage(language: Language): Map<String, String> {
        return translationsCache.getOrPut(language) {
            loadTranslationsFromAsset(language)
        }
    }

    private fun loadTranslationsFromAsset(language: Language): Map<String, String> {
        val fileName = "translations_${language.code}.txt"
        val translations = mutableMapOf<String, String>()

        try {
            context.assets.open(fileName).bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val parts = line.split("=", limit = 2)
                    if (parts.size == 2) {
                        val englishWord = parts[0].trim().lowercase()
                        val translation = parts[1].trim()
                        translations[englishWord] = translation
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return translations
    }
}
