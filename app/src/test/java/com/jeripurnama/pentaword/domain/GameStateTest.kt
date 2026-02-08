package com.jeripurnama.pentaword.domain

import org.junit.Assert.*
import org.junit.Test

class GameStateTest {

    @Test
    fun `allLetters returns center letter plus outer letters`() {
        val state = GameState(
            centerLetter = 'E',
            outerLetters = listOf('P', 'N', 'T', 'A', 'L', 'O')
        )
        val expected = listOf('E', 'P', 'N', 'T', 'A', 'L', 'O')
        assertEquals(expected, state.allLetters)
    }

    @Test
    fun `hasAllLetters returns true when word uses all letters`() {
        val state = GameState(
            centerLetter = 'E',
            outerLetters = listOf('P', 'N', 'T', 'A', 'L', 'O')
        )
        assertTrue(state.hasAllLetters("PENTOLA"))
    }

    @Test
    fun `hasAllLetters returns false when word missing letters`() {
        val state = GameState(
            centerLetter = 'E',
            outerLetters = listOf('P', 'N', 'T', 'A', 'L', 'O')
        )
        assertFalse(state.hasAllLetters("PENTA"))
    }

    @Test
    fun `Rank fromScore returns BEGINNER for 0 score`() {
        assertEquals(Rank.BEGINNER, Rank.fromScore(0, 100))
    }

    @Test
    fun `Rank fromScore returns GENIUS for 70 percent`() {
        assertEquals(Rank.GENIUS, Rank.fromScore(70, 100))
    }

    @Test
    fun `Rank fromScore returns QUEEN_BEE for 100 percent`() {
        assertEquals(Rank.QUEEN_BEE, Rank.fromScore(100, 100))
    }

    @Test
    fun `Rank fromScore handles edge cases`() {
        assertEquals(Rank.BEGINNER, Rank.fromScore(0, 0))
        assertEquals(Rank.GOOD_START, Rank.fromScore(2, 100))
        assertEquals(Rank.MOVING_UP, Rank.fromScore(5, 100))
        assertEquals(Rank.GOOD, Rank.fromScore(8, 100))
        assertEquals(Rank.SOLID, Rank.fromScore(15, 100))
        assertEquals(Rank.NICE, Rank.fromScore(25, 100))
        assertEquals(Rank.GREAT, Rank.fromScore(40, 100))
        assertEquals(Rank.AMAZING, Rank.fromScore(50, 100))
    }

    @Test
    fun `Message types have correct error flags`() {
        assertTrue(Message.TooShort.isError)
        assertTrue(Message.MissingCenter.isError)
        assertTrue(Message.NotInWordList.isError)
        assertTrue(Message.AlreadyFound.isError)
        assertFalse(Message.WordFound("TEST", 5, false).isError)
    }

    @Test
    fun `WordFound message shows pangram text`() {
        val pangramMessage = Message.WordFound("PENTOLA", 14, true)
        assertTrue(pangramMessage.text.contains("Pangram"))
    }
}
