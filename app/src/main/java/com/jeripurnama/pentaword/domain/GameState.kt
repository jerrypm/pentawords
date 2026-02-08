package com.jeripurnama.pentaword.domain

data class GameState(
    val centerLetter: Char = ' ',
    val outerLetters: List<Char> = emptyList(),
    val currentWord: String = "",
    val foundWords: List<String> = emptyList(),
    val score: Int = 0,
    val maxScore: Int = 0,
    val rank: Rank = Rank.BEGINNER,
    val message: Message? = null,
    val pangrams: Set<String> = emptySet()
) {
    val allLetters: List<Char>
        get() = listOf(centerLetter) + outerLetters

    fun hasAllLetters(word: String): Boolean {
        val wordLetters = word.uppercase().toSet()
        val availableLetters = allLetters.map { it.uppercaseChar() }.toSet()
        return wordLetters == availableLetters
    }
}

enum class Rank(val displayName: String, val threshold: Double) {
    BEGINNER("Beginner", 0.0),
    GOOD_START("Good Start", 0.02),
    MOVING_UP("Moving Up", 0.05),
    GOOD("Good", 0.08),
    SOLID("Solid", 0.15),
    NICE("Nice", 0.25),
    GREAT("Great", 0.40),
    AMAZING("Amazing", 0.50),
    GENIUS("Genius", 0.70),
    QUEEN_BEE("Queen Bee", 1.0);

    companion object {
        fun fromScore(score: Int, maxScore: Int): Rank {
            if (maxScore == 0) return BEGINNER
            val percentage = score.toDouble() / maxScore
            return entries.reversed().firstOrNull { percentage >= it.threshold } ?: BEGINNER
        }
    }
}

sealed class Message(val text: String, val isError: Boolean = false) {
    data object TooShort : Message("Too short", true)
    data object MissingCenter : Message("Missing center letter", true)
    data object NotInWordList : Message("Not in word list", true)
    data object AlreadyFound : Message("Already found", true)
    data class WordFound(val word: String, val points: Int, val isPangram: Boolean) :
        Message(if (isPangram) "Pangram! +$points" else "+$points")
}
