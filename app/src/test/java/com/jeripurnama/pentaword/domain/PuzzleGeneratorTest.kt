package com.jeripurnama.pentaword.domain

import org.junit.Assert.*
import org.junit.Test

class PuzzleGeneratorTest {

    private val testDictionary = setOf(
        "PENTA", "PAINT", "POINT", "TALON", "PLANT", "PLAIN",
        "ATONE", "ALONE", "PATENT", "PLANET", "ANTIPOLE", "POTENTIAL",
        "TALE", "PALE", "LEAN", "NEAT", "LATE", "TONE", "POLE", "LONE"
    )

    @Test
    fun `generatePuzzle returns puzzle with 7 letters`() {
        val generator = PuzzleGenerator(testDictionary)
        val puzzle = generator.generatePuzzle()

        assertEquals(1, puzzle.centerLetter.toString().length)
        assertEquals(6, puzzle.outerLetters.size)
    }

    @Test
    fun `generatePuzzle returns puzzle with all unique letters`() {
        val generator = PuzzleGenerator(testDictionary)
        val puzzle = generator.generatePuzzle()

        val allLetters = listOf(puzzle.centerLetter) + puzzle.outerLetters
        assertEquals(7, allLetters.distinct().size)
    }

    @Test
    fun `generatePuzzle returns valid words that include center letter`() {
        val generator = PuzzleGenerator(testDictionary)
        val puzzle = generator.generatePuzzle()

        puzzle.validWords.forEach { word ->
            assertTrue(
                "Word $word should contain center letter ${puzzle.centerLetter}",
                word.uppercase().contains(puzzle.centerLetter.uppercaseChar())
            )
        }
    }

    @Test
    fun `generatePuzzle returns valid words with minimum 4 letters`() {
        val generator = PuzzleGenerator(testDictionary)
        val puzzle = generator.generatePuzzle()

        puzzle.validWords.forEach { word ->
            assertTrue(
                "Word $word should have at least 4 letters",
                word.length >= 4
            )
        }
    }

    @Test
    fun `generatePuzzle calculates maxScore correctly`() {
        val generator = PuzzleGenerator(testDictionary)
        val puzzle = generator.generatePuzzle()

        assertTrue("Max score should be positive", puzzle.maxScore > 0)
    }

    @Test
    fun `generatePuzzle identifies pangrams correctly`() {
        val generator = PuzzleGenerator(testDictionary)
        val puzzle = generator.generatePuzzle()

        val allLetters = (listOf(puzzle.centerLetter) + puzzle.outerLetters)
            .map { it.uppercaseChar() }
            .toSet()

        puzzle.pangrams.forEach { pangram ->
            val pangramLetters = pangram.uppercase().toSet()
            assertEquals(
                "Pangram $pangram should use all available letters",
                allLetters,
                pangramLetters
            )
        }
    }

    @Test
    fun `fallback puzzle is valid when no pangrams exist`() {
        val smallDictionary = setOf("TEST", "BEST", "REST")
        val generator = PuzzleGenerator(smallDictionary)
        val puzzle = generator.generatePuzzle()

        assertNotNull(puzzle.centerLetter)
        assertFalse(puzzle.outerLetters.isEmpty())
    }
}
