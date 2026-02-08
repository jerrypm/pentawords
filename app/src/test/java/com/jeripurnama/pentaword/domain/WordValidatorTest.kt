package com.jeripurnama.pentaword.domain

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WordValidatorTest {

    private lateinit var validator: WordValidator
    private val testDictionary = setOf(
        "PENTA", "PAINT", "POINT", "TALON", "PLANT", "PLAIN",
        "ATONE", "ALONE", "PATENT", "PLANET", "POTENTIAL"
    )

    @Before
    fun setup() {
        validator = WordValidator(testDictionary)
    }

    @Test
    fun `validate returns TooShort for words less than 4 letters`() {
        val result = validator.validate(
            word = "PEN",
            centerLetter = 'E',
            allLetters = setOf('P', 'E', 'N', 'T', 'A', 'L', 'O'),
            foundWords = emptySet()
        )
        assertEquals(ValidationResult.TooShort, result)
    }

    @Test
    fun `validate returns MissingCenter when center letter not in word`() {
        val result = validator.validate(
            word = "PAINT",
            centerLetter = 'O',
            allLetters = setOf('P', 'A', 'I', 'N', 'T', 'O', 'L'),
            foundWords = emptySet()
        )
        assertEquals(ValidationResult.MissingCenter, result)
    }

    @Test
    fun `validate returns InvalidLetters when word contains letters not available`() {
        // Word "POIZE" contains center 'O' but has 'Z' which is invalid
        val result = validator.validate(
            word = "POIZE",
            centerLetter = 'O',
            allLetters = setOf('P', 'O', 'I', 'N', 'T', 'A', 'L'),
            foundWords = emptySet()
        )
        assertEquals(ValidationResult.InvalidLetters, result)
    }

    @Test
    fun `validate returns AlreadyFound for duplicate words`() {
        val result = validator.validate(
            word = "PENTA",
            centerLetter = 'E',
            allLetters = setOf('P', 'E', 'N', 'T', 'A', 'L', 'O'),
            foundWords = setOf("PENTA")
        )
        assertEquals(ValidationResult.AlreadyFound, result)
    }

    @Test
    fun `validate returns NotInWordList for invalid dictionary words`() {
        val result = validator.validate(
            word = "PENTAL",
            centerLetter = 'E',
            allLetters = setOf('P', 'E', 'N', 'T', 'A', 'L', 'O'),
            foundWords = emptySet()
        )
        assertEquals(ValidationResult.NotInWordList, result)
    }

    @Test
    fun `validate returns Valid with 1 point for 4 letter words`() {
        val result = validator.validate(
            word = "PENTA",
            centerLetter = 'E',
            allLetters = setOf('P', 'E', 'N', 'T', 'A', 'L', 'O'),
            foundWords = emptySet()
        )
        assertTrue(result is ValidationResult.Valid)
        assertEquals(5, (result as ValidationResult.Valid).points)
    }

    @Test
    fun `validate returns Valid with length points for 5+ letter words`() {
        val result = validator.validate(
            word = "PLANET",
            centerLetter = 'E',
            allLetters = setOf('P', 'L', 'A', 'N', 'E', 'T', 'O'),
            foundWords = emptySet()
        )
        assertTrue(result is ValidationResult.Valid)
        assertEquals(6, (result as ValidationResult.Valid).points)
    }

    @Test
    fun `validate detects pangram and adds 7 bonus points`() {
        val result = validator.validate(
            word = "POTENTIAL",
            centerLetter = 'E',
            allLetters = setOf('P', 'O', 'T', 'E', 'N', 'I', 'A', 'L'),
            foundWords = emptySet()
        )
        assertTrue(result is ValidationResult.Valid)
        val valid = result as ValidationResult.Valid
        assertTrue(valid.isPangram)
        assertEquals(9 + 7, valid.points) // 9 letters + 7 bonus
    }

    @Test
    fun `validate is case insensitive`() {
        val result = validator.validate(
            word = "penta",
            centerLetter = 'e',
            allLetters = setOf('p', 'e', 'n', 't', 'a', 'l', 'o'),
            foundWords = emptySet()
        )
        assertTrue(result is ValidationResult.Valid)
    }
}
