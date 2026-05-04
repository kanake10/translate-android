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
package com.kanake10.translate_ui.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kanake10.translate.domain.models.Language
import com.kanake10.translate_ui.vm.TranslateController
import com.kanake10.translate_ui.vm.TranslationViewModel


/**
 * A full translation screen composable that handles text input, language selection,
 * translation execution and result display.
 *
 * This component is backed by a ViewModel and manages translation state,
 * loading and error handling internally.
 *
 * @param modifier Modifier applied to the root container.
 * @param translateFrom Optional source language code to preselect (e.g. "en").
 * @param translateTo Optional target language code to preselect (e.g. "fr").
 * @param headerContent Optional composable displayed at the top of the screen.
 * @param translateLanguageSelector Composable used to render language selection UI.
 * Provides available languages, selected source/target, and selection callbacks.
 * @param translateInputField Composable for entering text to translate.
 * Provides current text and a callback for text changes.
 * @param translationContent Composable for displaying the translated text.
 * @param translateButton Composable for triggering the translation action.
 * Provides loading state and click handler.
 * @param translateErrorContent Composable for displaying error messages.
 */
@Composable
fun TranslationScreen(
    modifier: Modifier = Modifier,
    translateFrom: String? = null,
    translateTo: String? = null,
    headerContent: @Composable (() -> Unit)? = null,
    translateLanguageSelector: (@Composable (
        languages: List<Language>,
        selectedSource: Language?,
        selectedTarget: Language?,
        onSourceSelected: (Language) -> Unit,
        onTargetSelected: (Language) -> Unit,
    ) -> Unit)? = { languages, source, target, onSource, onTarget ->
        TranslateLanguageSelector(languages, source, target, onSource, onTarget)
    },
    translateInputField: @Composable (
        text: String,
        onTextChange: (String) -> Unit,
    ) -> Unit = { text, onChange ->
        TranslateInputField(text, onChange)
    },
    translationContent: @Composable (
        translatedText: String,
    ) -> Unit = { translated ->
        TranslationContent(translated)
    },
    translateButton: @Composable (
        isLoading: Boolean,
        onClick: () -> Unit,
    ) -> Unit = { isLoading, onClick ->
        TranslateButton(isLoading, onClick)
    },
    translateErrorContent: @Composable (String) -> Unit = { error ->
        TranslateErrorContent(error)
    },
) {

    val viewModel: TranslationViewModel = viewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.languages) {
        translateFrom?.let { code ->
            uiState.languages.find { it.code == code }?.let {
                viewModel.selectSource(it)
            }
        }

        translateTo?.let { code ->
            uiState.languages.find { it.code == code }?.let {
                viewModel.selectTarget(it)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        headerContent?.let {
            it()
            Spacer(modifier = Modifier.height(12.dp))
        }

        translateLanguageSelector?.invoke(
            uiState.languages,
            uiState.selectedSource,
            uiState.selectedTarget,
            viewModel::selectSource,
            viewModel::selectTarget,
        )

        Spacer(modifier = Modifier.height(16.dp))

        translateInputField(
            uiState.inputText,
            viewModel::updateInputText,
        )

        Spacer(modifier = Modifier.height(16.dp))

        translationContent(uiState.translatedText)

        Spacer(modifier = Modifier.height(16.dp))

        uiState.error?.let {
            translateErrorContent(it)
            Spacer(modifier = Modifier.height(12.dp))
        }

        translateButton(
            uiState.isLoading,
            viewModel::translate,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TranslateLanguageSelector(
    languages: List<Language>,
    selectedSource: Language?,
    selectedTarget: Language?,
    onSourceSelected: (Language) -> Unit,
    onTargetSelected: (Language) -> Unit
) {
    var sourceExpanded by remember { mutableStateOf(false) }
    var targetExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        ExposedDropdownMenuBox(
            expanded = sourceExpanded,
            onExpandedChange = { sourceExpanded = !sourceExpanded },
            modifier = Modifier.weight(1f)
        ) {

            OutlinedTextField(
                value = selectedSource?.name ?: "Auto Detect",
                onValueChange = {},
                readOnly = true,
                label = { Text("From") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = sourceExpanded,
                onDismissRequest = { sourceExpanded = false }
            ) {
                languages.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language.name) },
                        onClick = {
                            onSourceSelected(language)
                            sourceExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        ExposedDropdownMenuBox(
            expanded = targetExpanded,
            onExpandedChange = { targetExpanded = !targetExpanded },
            modifier = Modifier.weight(1f)
        ) {

            OutlinedTextField(
                value = selectedTarget?.name ?: "English",
                onValueChange = {},
                readOnly = true,
                label = { Text("To") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = targetExpanded,
                onDismissRequest = { targetExpanded = false }
            ) {
                languages.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language.name) },
                        onClick = {
                            onTargetSelected(language)
                            targetExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun TranslateInputField(
    text: String,
    onTextChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Enter text",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        )
    }
}

@Composable
internal fun TranslationContent(
    text: String
) {
    if (text.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = "Translation",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
internal fun TranslateButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
    ) {

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text("Translate")
        }
    }
}

@Composable
internal fun TranslateErrorContent(
    error: String
) {
    Text(
        text = error,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium
    )
}

/**
 * A composable that provides a simple translate/undo action for a given text.
 *
 * Internally manages translation state, loading state, and toggling between
 * original and translated text. When the translation result changes
 * [onTranslated] is invoked with the current text.
 *
 * @param postText The text to be translated. When this changes, the internal state resets.
 * @param modifier Modifier for styling and layout of the container.
 * @param onTranslated Callback invoked when the displayed text changes
 * (either translated or reverted to original).
 * @param buttonContent Slot for customizing the translate button UI.
 * Provides:
 * - [isTranslated]: whether the text is currently translated
 * - [isLoading]: whether a translation request is in progress
 * - [onClick]: triggers the translate/undo action
 */
@Composable
fun Translate(
    postText: String,
    modifier: Modifier = Modifier,
    onTranslated: (String) -> Unit = {},
    buttonContent: @Composable (
        isTranslated: Boolean,
        isLoading: Boolean,
        onClick: () -> Unit,
    ) -> Unit = { isTranslated, isLoading, onClick ->
        Button(
            onClick = onClick,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(if (isTranslated) "Undo" else "Translate")
            }
        }
    },
) {
    val scope = rememberCoroutineScope()

    val controller = remember {
        TranslateController(
            scope = scope
        )
    }

    val state by controller.state.collectAsStateWithLifecycle()

    val isInitialized = remember { mutableStateOf(false) }

    LaunchedEffect(postText) {
        controller.setText(postText)
        isInitialized.value = false
    }

    LaunchedEffect(state.text, state.isTranslated) {
        if (!isInitialized.value) {
            isInitialized.value = true
            return@LaunchedEffect
        }
        onTranslated(state.text)
    }

    Box(modifier = modifier) {
        buttonContent(
            state.isTranslated,
            state.isLoading
        ) {
            controller.toggleTranslate()
        }
    }
}