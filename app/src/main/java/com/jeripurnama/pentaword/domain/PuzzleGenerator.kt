package com.jeripurnama.pentaword.domain

class PuzzleGenerator(private val dictionary: Set<String>) {

    data class Puzzle(
        val centerLetter: Char,
        val outerLetters: List<Char>,
        val validWords: Set<String>,
        val pangrams: Set<String>,
        val maxScore: Int
    )

    fun generatePuzzle(): Puzzle {
        val pangrams = findPangrams()
        if (pangrams.isEmpty()) {
            return generateFallbackPuzzle()
        }

        val pangram = pangrams.random()
        val letters = pangram.uppercase().toSet().toList().shuffled()
        val centerLetter = letters.first()
        val outerLetters = letters.drop(1)

        val validWords = findValidWords(centerLetter, letters.toSet())
        val puzzlePangrams = validWords.filter {
            it.uppercase().toSet() == letters.map { l -> l.uppercaseChar() }.toSet()
        }.toSet()
        val maxScore = calculateMaxScore(validWords, puzzlePangrams)

        return Puzzle(
            centerLetter = centerLetter,
            outerLetters = outerLetters,
            validWords = validWords,
            pangrams = puzzlePangrams,
            maxScore = maxScore
        )
    }

    private fun findPangrams(): List<String> {
        return dictionary.filter { word ->
            word.length >= 7 && word.uppercase().toSet().size == 7
        }
    }

    private fun findValidWords(centerLetter: Char, allLetters: Set<Char>): Set<String> {
        val upperCenter = centerLetter.uppercaseChar()
        val upperLetters = allLetters.map { it.uppercaseChar() }.toSet()

        return dictionary.filter { word ->
            val upperWord = word.uppercase()
            upperWord.length >= 4 &&
            upperWord.contains(upperCenter) &&
            upperWord.all { it in upperLetters }
        }.toSet()
    }

    private fun calculateMaxScore(words: Set<String>, pangrams: Set<String>): Int {
        return words.sumOf { word ->
            val basePoints = if (word.length == 4) 1 else word.length
            if (word in pangrams) basePoints + 7 else basePoints
        }
    }

    private fun generateFallbackPuzzle(): Puzzle {
        val letters = listOf('P', 'E', 'N', 'T', 'A', 'W', 'O')
        return Puzzle(
            centerLetter = 'E',
            outerLetters = letters.filter { it != 'E' },
            validWords = setOf("PENTA", "WEPT", "PEAT", "NEAT", "TAPE", "ANTE", "PANT"),
            pangrams = emptySet(),
            maxScore = 30
        )
    }
}
