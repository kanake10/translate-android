package com.kanake10.translate_ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.kanake10.translate.TranslateClient
import com.kanake10.translate.domain.models.Language
import com.kanake10.translate.repo.TranslateRepository
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
    val error: String? = null,
)

internal class TranslationViewModel(
    private val repository: TranslateRepository,
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
                is TranslateResult.Success -> _uiState.value = _uiState.value.copy(
                    languages = result.data,
                    selectedSource = result.data.find { it.code == "auto" },
                    selectedTarget = result.data.find { it.code == "en" },
                    isLoadingLanguages = false,
                )
                is TranslateResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoadingLanguages = false,
                    error = result.error.toMessage(),
                )
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
        if (state.inputText.isBlank()) return

        _uiState.value = state.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val result = repository.translate(
                text = state.inputText,
                source = state.selectedSource?.code ?: "auto",
                target = state.selectedTarget?.code ?: "en",
            )) {
                is TranslateResult.Success -> _uiState.value = _uiState.value.copy(
                    translatedText = result.data.translatedText,
                    isLoading = false,
                )
                is TranslateResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.error.toString(),
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
                TranslationViewModel(TranslateClient.getClient()) as T
        }
    }
}