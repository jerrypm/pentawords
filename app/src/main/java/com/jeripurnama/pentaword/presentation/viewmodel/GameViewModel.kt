package com.jeripurnama.pentaword.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jeripurnama.pentaword.data.DictionaryRepository
import com.jeripurnama.pentaword.domain.GameState
import com.jeripurnama.pentaword.domain.Message
import com.jeripurnama.pentaword.domain.PuzzleGenerator
import com.jeripurnama.pentaword.domain.Rank
import com.jeripurnama.pentaword.domain.ValidationResult
import com.jeripurnama.pentaword.domain.WordValidator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DictionaryRepository(application)
    private var wordValidator: WordValidator? = null
    private var puzzleGenerator: PuzzleGenerator? = null
    private var validWords: Set<String> = emptySet()

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        initializeGame()
    }

    private fun initializeGame() {
        viewModelScope.launch {
            _isLoading.value = true
            val dictionary = repository.loadDictionary()
            wordValidator = WordValidator(dictionary)
            puzzleGenerator = PuzzleGenerator(dictionary)
            generateNewPuzzle()
            _isLoading.value = false
        }
    }

    fun generateNewPuzzle() {
        viewModelScope.launch {
            val puzzle = puzzleGenerator?.generatePuzzle() ?: return@launch
            validWords = puzzle.validWords
            _gameState.value = GameState(
                centerLetter = puzzle.centerLetter,
                outerLetters = puzzle.outerLetters,
                maxScore = puzzle.maxScore,
                pangrams = puzzle.pangrams
            )
        }
    }

    fun onLetterClick(letter: Char) {
        _gameState.update { state ->
            state.copy(
                currentWord = state.currentWord + letter.uppercaseChar(),
                message = null
            )
        }
    }

    fun onDeleteClick() {
        _gameState.update { state ->
            if (state.currentWord.isNotEmpty()) {
                state.copy(
                    currentWord = state.currentWord.dropLast(1),
                    message = null
                )
            } else {
                state
            }
        }
    }

    fun onShuffleClick() {
        _gameState.update { state ->
            state.copy(
                outerLetters = state.outerLetters.shuffled(),
                message = null
            )
        }
    }

    fun onEnterClick() {
        val state = _gameState.value
        val validator = wordValidator ?: return

        val result = validator.validate(
            word = state.currentWord,
            centerLetter = state.centerLetter,
            allLetters = state.allLetters.toSet(),
            foundWords = state.foundWords.toSet()
        )

        viewModelScope.launch {
            when (result) {
                is ValidationResult.TooShort -> {
                    showMessage(Message.TooShort)
                }
                is ValidationResult.MissingCenter -> {
                    showMessage(Message.MissingCenter)
                }
                is ValidationResult.InvalidLetters -> {
                    showMessage(Message.NotInWordList)
                }
                is ValidationResult.AlreadyFound -> {
                    showMessage(Message.AlreadyFound)
                }
                is ValidationResult.NotInWordList -> {
                    showMessage(Message.NotInWordList)
                }
                is ValidationResult.Valid -> {
                    val newScore = state.score + result.points
                    val newFoundWords = state.foundWords + state.currentWord.uppercase()
                    _gameState.update {
                        it.copy(
                            foundWords = newFoundWords,
                            score = newScore,
                            rank = Rank.fromScore(newScore, state.maxScore),
                            currentWord = "",
                            message = Message.WordFound(
                                word = state.currentWord.uppercase(),
                                points = result.points,
                                isPangram = result.isPangram
                            )
                        )
                    }
                    delay(2000)
                    clearMessage()
                }
            }
        }
    }

    private suspend fun showMessage(message: Message) {
        _gameState.update { it.copy(message = message, currentWord = "") }
        delay(1500)
        clearMessage()
    }

    private fun clearMessage() {
        _gameState.update { it.copy(message = null) }
    }

    fun getFoundWordsCount(): Int = _gameState.value.foundWords.size

    fun getTotalWordsCount(): Int = validWords.size
}
