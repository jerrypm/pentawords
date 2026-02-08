package com.jeripurnama.pentaword.domain

class WordValidator(private val dictionary: Set<String>) {

    fun validate(
        word: String,
        centerLetter: Char,
        allLetters: Set<Char>,
        foundWords: Set<String>
    ): ValidationResult {
        val upperWord = word.uppercase()
        val upperCenter = centerLetter.uppercaseChar()
        val upperLetters = allLetters.map { it.uppercaseChar() }.toSet()

        return when {
            upperWord.length < 4 -> ValidationResult.TooShort
            !upperWord.contains(upperCenter) -> ValidationResult.MissingCenter
            !upperWord.all { it in upperLetters } -> ValidationResult.InvalidLetters
            upperWord in foundWords.map { it.uppercase() } -> ValidationResult.AlreadyFound
            !dictionary.contains(upperWord) -> ValidationResult.NotInWordList
            else -> {
                val isPangram = upperWord.toSet() == upperLetters
                val points = calculatePoints(upperWord, isPangram)
                ValidationResult.Valid(points, isPangram)
            }
        }
    }

    private fun calculatePoints(word: String, isPangram: Boolean): Int {
        val basePoints = if (word.length == 4) 1 else word.length
        return if (isPangram) basePoints + 7 else basePoints
    }
}

sealed class ValidationResult {
    data object TooShort : ValidationResult()
    data object MissingCenter : ValidationResult()
    data object InvalidLetters : ValidationResult()
    data object AlreadyFound : ValidationResult()
    data object NotInWordList : ValidationResult()
    data class Valid(val points: Int, val isPangram: Boolean) : ValidationResult()
}
