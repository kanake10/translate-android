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
package com.kanake10.translate_ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.kanake10.translate.domain.models.Language
import com.kanake10.translate_ui.BasePaparazziTest
import com.kanake10.translate_ui.vm.TranslationUiState
import org.junit.Test
import org.junit.runner.RunWith


//./gradlew recordPaparazziDebug
//./gradlew verifyPaparazziDebug
//./gradlew metalavaGenerateSignatureDebug

@RunWith(TestParameterInjector::class)
class TranslationScreenPaparazziTest : BasePaparazziTest() {

    private val languages = listOf(
        Language(code = "en", name = "English"),
        Language(code = "sw", name = "Swahili"),
        Language(code = "fr", name = "French"),
    )

    private val readyState = TranslationUiState.Ready(
        languages = languages,
        selectedSource = languages[0],
        selectedTarget = languages[1],
        inputText = "Hello World",
        translatedText = "Habari Dunia",
        isTranslating = false,
        error = null,
    )

    private val contentModifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()

    @Test
    fun translationScreen() {
        snapshot {
            TranslationScreenContent(
                state = readyState,
                modifier = contentModifier,
            )
        }
    }

    @Test
    fun translateLanguageSelector() {
        snapshot {
            TranslateLanguageSelector(
                languages = languages,
                selectedSource = languages[0],
                selectedTarget = languages[1],
                onSourceSelected = {},
                onTargetSelected = {},
            )
        }
    }

    @Test
    fun translateInputField() {
        snapshot {
            TranslateInputField(
                text = "Hello World",
                onTextChange = {},
            )
        }
    }

    @Test
    fun translationContent() {
        snapshot {
            TranslationContent(
                text = "Habari Dunia",
            )
        }
    }

    @Test
    fun translateButton() {
        snapshot {
            TranslateButton(
                isLoading = false,
                onClick = {},
            )
        }
    }

    @Test
    fun translateButtonLoading() {
        snapshot {
            TranslateButton(
                isLoading = true,
                onClick = {},
            )
        }
    }

    @Test
    fun translateErrorContent() {
        snapshot {
            TranslateErrorContent(
                error = "Network error",
            )
        }
    }

    @Test
    fun translationScreenWithError() {
        snapshot {
            TranslationScreenContent(
                state = readyState.copy(error = "Unable to translate text"),
                modifier = contentModifier,
            )
        }
    }

    @Test
    fun translationScreenLoading() {
        snapshot {
            TranslationScreenContent(
                state = readyState.copy(isTranslating = true),
                modifier = contentModifier,
            )
        }
    }

    @Test
    fun translationScreenWithoutTranslation() {
        snapshot {
            TranslationScreenContent(
                state = readyState.copy(translatedText = ""),
                modifier = contentModifier,
            )
        }
    }

    @Test
    fun translationScreenWithHeader() {
        snapshot {
            TranslationScreenContent(
                state = readyState,
                modifier = contentModifier,
                headerContent = {
                    Text(
                        text = "Translator",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
            )
        }
    }

    @Test
    fun translationScreenWithoutContentCopyIcon() {
        snapshot {
            TranslationScreenContent(
                state = readyState,
                modifier = contentModifier,
                translationContent = { translated ->
                    TranslationContent(
                        text = translated,
                        copyEnabled = false,
                    )
                },
            )
        }
    }

    @Test
    fun translateComposable() {
        snapshot {
            Translate(
                text = "Hello World",
            )
        }
    }
}