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
 * LoadingLanguages: fetching supported languages
 * Ready: interactive translation screen
 * Error: fatal/loading failure state
 */
sealed interface TranslationUiState {

    /** Loading supported languages */
    data object LoadingLanguages : TranslationUiState

    /**
     * Active translation screen state
     */
    data class Ready(
        val inputText: String = "",
        val translatedText: String = "",
        val languages: List<Language> = emptyList(),
        val selectedSource: Language? = null,
        val selectedTarget: Language? = null,
        val isTranslating: Boolean = false,
        val error: String? = null,
    ) : TranslationUiState

    /**
     * Error state
     */
    data class Error(
        val message: String
    ) : TranslationUiState
}

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
    private val _translateState = MutableStateFlow<TranslationUiState>(TranslationUiState.LoadingLanguages)
    val translateState = _translateState.asStateFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        viewModelScope.launch {
            when (val result = TranslateClient.getSupportedLanguages()) {

                is TranslateResult.Success -> {
                    _translateState.value = TranslationUiState.Ready(
                        languages = result.data,
                        selectedSource = result.data.defaultSource(),
                        selectedTarget = result.data.defaultTarget(),
                    )
                }

                is TranslateResult.Error -> {
                    _translateState.value = TranslationUiState.Error(
                        message = result.error.toMessage()
                    )
                }
            }
        }
    }

    fun updateInputText(text: String) {
        _translateState.update { state ->
            when (state) {
                is TranslationUiState.Ready ->
                    state.copy(inputText = text, error = null)

                else -> state
            }
        }
    }

    fun selectSource(language: Language) {
        _translateState.update { state ->
            when (state) {
                is TranslationUiState.Ready ->
                    state.copy(selectedSource = language)

                else -> state
            }
        }
    }

    fun selectTarget(language: Language) {
        _translateState.update { state ->
            when (state) {
                is TranslationUiState.Ready ->
                    state.copy(selectedTarget = language)

                else -> state
            }
        }
    }

    fun translate() {
        val state = _translateState.value
        if (state !is TranslationUiState.Ready) return
        if (state.inputText.isBlank() || state.isTranslating) return

        _translateState.value = state.copy(isTranslating = true)

        viewModelScope.launch {
            when (val result = TranslateClient.translate(
                text = state.inputText,
                source = state.selectedSource?.code ?: "auto",
                target = state.selectedTarget?.code ?: "en",
            )) {

                is TranslateResult.Success -> {
                    _translateState.update {
                        (it as? TranslationUiState.Ready)?.copy(
                            translatedText = result.data.translatedText,
                            isTranslating = false
                        ) ?: it
                    }
                }

                is TranslateResult.Error -> {
                    _translateState.update {
                        (it as? TranslationUiState.Ready)?.copy(
                            isTranslating = false,
                            error = result.error.toMessage()
                        ) ?: it
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