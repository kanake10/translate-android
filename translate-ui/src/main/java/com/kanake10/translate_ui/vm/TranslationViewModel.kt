package com.kanake10.translate_ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kanake10.translate.domain.models.Language
import com.kanake10.translate.repo.TranslateRepository
import com.kanake10.translate.util.TranslateError
import com.kanake10.translate.util.TranslateResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TranslationUiState(
    val inputText: String = "",
    val translatedText: String = "",
    val languages: List<Language> = emptyList(),
    val selectedSource: Language? = null,
    val selectedTarget: Language? = null,
    val isLoading: Boolean = false,
    val isLoadingLanguages: Boolean = false,
    val error: String? = null
)

class TranslationViewModel(
    private val repository: TranslateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TranslationUiState())
    val uiState: StateFlow<TranslationUiState> = _uiState

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        _uiState.value = _uiState.value.copy(isLoadingLanguages = true)

        viewModelScope.launch {
            when (val result = repository.getSupportedLanguages()) {

                is TranslateResult.Success -> {
                    val languages = result.data

                    _uiState.value = _uiState.value.copy(
                        languages = languages,
                        selectedSource = languages.find { it.code == "auto" },
                        selectedTarget = languages.find { it.code == "en" },
                        isLoadingLanguages = false
                    )
                }

                is TranslateResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingLanguages = false,
                        error = result.error.toMessage()
                    )
                }
            }
        }
    }

    fun updateInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun selectSource(language: Language) {
        _uiState.value = _uiState.value.copy(selectedSource = language)
    }

    fun selectTarget(language: Language) {
        _uiState.value = _uiState.value.copy(selectedTarget = language)
    }

    fun translate() {
        val state = _uiState.value
        val text = state.inputText

        val source = state.selectedSource?.code ?: "auto"
        val target = state.selectedTarget?.code ?: "en"

        if (text.isBlank()) return

        _uiState.value = state.copy(isLoading = true, error = null)

        viewModelScope.launch {
            when (val result = repository.translate(text, source, target)) {

                is TranslateResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        translatedText = result.data.translatedText,
                        isLoading = false
                    )
                }

                is TranslateResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.error.toMessage()
                    )
                }
            }
        }
    }
}

fun TranslateError.toMessage(): String = when (this) {
    TranslateError.MissingApiKey -> "API key is missing"
    TranslateError.InvalidApiKey -> "Invalid API key"
    is TranslateError.InsufficientCredits -> "Not enough credits"
    TranslateError.RateLimitExceeded -> "Too many requests. Try again later"
    is TranslateError.BadRequest -> detail
    TranslateError.NotFound -> "Resource not found"
    TranslateError.InternalServerError -> "Server error"
    TranslateError.ServiceUnavailable -> "Service unavailable"
    TranslateError.NetworkError -> "No internet connection"
    is TranslateError.Unknown -> detail.ifEmpty { "Something went wrong" }
}

class TranslationViewModelFactory(
    private val repository: TranslateRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TranslationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TranslationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}