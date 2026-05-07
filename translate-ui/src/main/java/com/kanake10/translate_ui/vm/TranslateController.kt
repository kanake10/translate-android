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

import com.kanake10.translate.TranslateClient.translate
import com.kanake10.translate.util.TranslateResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

/**
* @param displayText    Text currently shown — either original or translated.
* @param originalText   Unmodified source text; never changes after [TranslateController.setText].
* @param isTranslated   True when the displayed text is the translated version.
* @param isLoading      True while a translation network request is in progress.
* @param error          Non-null when the last translation attempt failed.
* @param targetLanguage BCP-47 language tag used as the translation target.
*                       Defaults to [Locale.getDefault().language].
*/
data class TranslateBtnState(
    val displayText: String = "",
    val originalText: String = "",
    val isTranslated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val targetLanguage: String = Locale.getDefault().language,
)

/**
 * Controller responsible for managing translation logic for the [com.kanake10.translate_ui.ui.Translate] composable.
 *
 * Handles:
 *   Storing and resetting the source text via [setText]
 *   Toggling between original and translated text via [toggleTranslate]
 *   Performing async translation requests and surfacing loading / error state
 *   Restoring the original text instantly on undo (no network call)
 *
 * @param scope Coroutine scope used to perform async operations.
 */
internal class TranslateController(private val scope: CoroutineScope) {

    private val _state = MutableStateFlow(TranslateBtnState())
    val state: StateFlow<TranslateBtnState> = _state.asStateFlow()

    fun setText(value: String) {
        _state.update {
            TranslateBtnState(
                displayText = value,
                originalText = value,
                targetLanguage = it.targetLanguage,
            )
        }
    }

    fun toggleTranslate() {
        val current = _state.value

        if (current.isTranslated) {
            _state.update {
                it.copy(displayText = current.originalText, isTranslated = false)
            }
            return
        }

        if (current.displayText.isBlank() || current.isLoading) return

        _state.update { it.copy(isLoading = true, error = null) }

        scope.launch {
            when (val result = translate(
                text = current.originalText,
                source = "auto",
                target = current.targetLanguage,
            )) {
                is TranslateResult.Success -> _state.update {
                    it.copy(
                        displayText = result.data.translatedText,
                        isTranslated = true,
                        isLoading = false,
                    )
                }

                is TranslateResult.Error -> _state.update {
                    it.copy(
                        error = result.error.toMessage(),
                        isLoading = false,
                    )
                }
            }
        }
    }
}