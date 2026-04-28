package com.kanake10.translate_ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanake10.translate.TranslateClient
import com.kanake10.translate.domain.models.Language
import com.kanake10.translate.util.TranslateResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

internal class TranslationViewModel : ViewModel() {

    private val _translateState = MutableStateFlow(TranslationUiState())
    val uiState = _translateState.asStateFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        _translateState.update {
            it.copy(isLoadingLanguages = true)
        }

        viewModelScope.launch {
            when (val result = TranslateClient.getSupportedLanguages()) {
                is TranslateResult.Success -> {
                    _translateState.update {
                        it.copy(
                            languages = result.data,
                            selectedSource = result.data.defaultSource(),
                            selectedTarget = result.data.defaultTarget(),
                            isLoadingLanguages = false,
                        )
                    }
                }

                is TranslateResult.Error -> {
                    _translateState.update {
                        it.copy(
                            isLoadingLanguages = false,
                            error = result.error.toMessage(),
                        )
                    }
                }
            }
        }
    }

    fun updateInputText(text: String) {
        _translateState.update {
            it.copy(
                inputText = text,
                error = null
            )
        }
    }

    fun selectSource(language: Language) {
        _translateState.update {
            it.copy(selectedSource = language)
        }
    }

    fun selectTarget(language: Language) {
        _translateState.update {
            it.copy(selectedTarget = language)
        }
    }

    fun translate() {
        val state = _translateState.value

        if (state.inputText.isBlank() || state.isLoading) return

        _translateState.update {
            it.copy(isLoading = true, error = null)
        }

        viewModelScope.launch {
            when (val result = TranslateClient.translate(
                text = state.inputText,
                source = state.selectedSource?.code ?: "auto",
                target = state.selectedTarget?.code ?: "en",
            )) {
                is TranslateResult.Success -> {
                    _translateState.update {
                        it.copy(
                            translatedText = result.data.translatedText,
                            isLoading = false,
                        )
                    }
                }

                is TranslateResult.Error -> {
                    _translateState.update {
                        it.copy(
                            isLoading = false,
                            error = result.error.toMessage(),
                        )
                    }
                }
            }
        }
    }

    private fun List<Language>.defaultSource() =
        find { it.code == "auto" }

    private fun List<Language>.defaultTarget() =
        find { it.code == "en" }

}