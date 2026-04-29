/*
 * Copyright 2026 Ezra Kanake
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

/**
 * UI state for the translation screen.
 *
 * @param inputText Current text entered by the user.
 * @param translatedText Result of the translated text.
 * @param languages List of supported languages.
 * @param selectedSource Currently selected source language.
 * @param selectedTarget Currently selected target language.
 * @param isLoading Whether a translation request is in progress.
 * @param isLoadingLanguages Whether languages are being loaded.
 * @param error Optional error message to display.
 */
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

/**
 * ViewModel responsible for managing translation screen state.
 *
 * Handles:
 * - Loading supported languages
 * - Managing user input
 * - Performing translation requests
 * - Updating UI state (loading, success, error)
 */
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