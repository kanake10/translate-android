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

import android.content.ClipData
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kanake10.translate.domain.models.Language
import com.kanake10.translate_ui.vm.TranslateController
import com.kanake10.translate_ui.vm.TranslationUiState
import com.kanake10.translate_ui.vm.TranslationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private const val DELAY = 2_000L
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
 * @param translateButton Composable used to trigger translation.
 * Receives the loading state and a callback to start translation.
 * @param translateErrorContent Composable for displaying error messages.
 */

@Suppress("LongMethod","LongParameterList")
@Composable
fun TranslationScreen(
    modifier: Modifier = Modifier,
    translateFrom: String? = null,
    translateTo: String? = null,
    headerContent: @Composable (() -> Unit)? = null,
    translateLanguageSelector: @Composable (
        languages: List<Language>,
        selectedSource: Language?,
        selectedTarget: Language?,
        onSourceSelected: (Language) -> Unit,
        onTargetSelected: (Language) -> Unit,
    ) -> Unit = { languages, source, target, onSource, onTarget ->
        TranslateLanguageSelector(
            languages,
            source,
            target,
            onSource,
            onTarget,
        )
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
        TranslationContent(text = translated)
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
    val translationUiState by viewModel.translateState.collectAsStateWithLifecycle()

    when (val state = translationUiState) {

        is TranslationUiState.Error -> {
            translateErrorContent(state.message)
        }

        is TranslationUiState.Ready -> {

            LaunchedEffect(state.languages, translateFrom, translateTo) {
                translateFrom?.let { code ->
                    state.languages.find { it.code == code }?.let {
                        viewModel.selectSource(it)
                    }
                }

                translateTo?.let { code ->
                    state.languages.find { it.code == code }?.let {
                        viewModel.selectTarget(it)
                    }
                }
            }

            TranslationScreenContent(
                modifier = modifier,
                state = state,
                headerContent = headerContent,
                onSourceSelected = viewModel::selectSource,
                onTargetSelected = viewModel::selectTarget,
                onInputTextChanged = viewModel::updateInputText,
                onTranslateClick = viewModel::translate,
                translateLanguageSelector = translateLanguageSelector,
                translateInputField = translateInputField,
                translationContent = translationContent,
                translateButton = translateButton,
                translateErrorContent = translateErrorContent,
            )
        }

        else -> Unit
    }
}

@Suppress("LongParameterList")
@Composable
internal fun TranslationScreenContent(
    state: TranslationUiState.Ready,
    modifier: Modifier = Modifier,
    headerContent: @Composable (() -> Unit)? = null,
    onSourceSelected: (Language) -> Unit = {},
    onTargetSelected: (Language) -> Unit = {},
    onInputTextChanged: (String) -> Unit = {},
    onTranslateClick: () -> Unit = {},
    translateLanguageSelector: @Composable (
        languages: List<Language>,
        selectedSource: Language?,
        selectedTarget: Language?,
        onSourceSelected: (Language) -> Unit,
        onTargetSelected: (Language) -> Unit,
    ) -> Unit = { languages, source, target, onSource, onTarget ->
        TranslateLanguageSelector(
            languages,
            source,
            target,
            onSource,
            onTarget,
        )
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
        TranslationContent(text = translated)
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (headerContent == null) {
            Spacer(modifier = Modifier.height(24.dp))
        }

        headerContent?.let {
            it()
            Spacer(Modifier.height(12.dp))
        }

        translateLanguageSelector(
            state.languages,
            state.selectedSource,
            state.selectedTarget,
            onSourceSelected,
            onTargetSelected,
        )

        Spacer(Modifier.height(16.dp))
        translateInputField(state.inputText, onInputTextChanged)
        Spacer(Modifier.height(16.dp))

        translationContent(state.translatedText)

        Spacer(Modifier.height(16.dp))

        state.error?.let {
            translateErrorContent(it)
            Spacer(Modifier.height(12.dp))
        }

        translateButton(state.isTranslating, onTranslateClick)
    }
}

@Suppress("LongMethod")
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

            selectedSource?.name?.let {
                OutlinedTextField(
                    value = it,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("From") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                )
            }

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

            selectedTarget?.name?.let {
                OutlinedTextField(
                    value = it,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("To") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                )
            }

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

@Suppress("LongMethod","LongParameterList")
@Composable
internal fun TranslationContent(
    text: String,
    copyEnabled: Boolean = true,
    onCopied: () -> Unit = {},
    copyIcon: @Composable (copied: Boolean) -> Unit = { copied ->
        Icon(
            imageVector = if (copied) Icons.Outlined.Check else Icons.Outlined.ContentCopy,
            contentDescription = if (copied) "Copied" else "Copy translation",
            modifier = Modifier.size(16.dp),
            tint = if (copied) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            },
        )
    },
) {
    if (text.isNotEmpty()) {
        val clipboard = LocalClipboard.current
        val scope = rememberCoroutineScope()
        var copied by remember { mutableStateOf(false) }

        LaunchedEffect(copied) {
            if (copied) {
                delay(DELAY)
                copied = false
            }
        }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Translation",
                    style = MaterialTheme.typography.titleMedium,
                )

                if (copyEnabled) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                clipboard.setClipEntry(
                                    ClipEntry(
                                        ClipData.newPlainText(
                                            "translation",
                                            text
                                        )
                                    )
                                )

                                copied = true
                                onCopied()
                            }
                        },
                        modifier = Modifier.size(22.dp),
                    ) {
                        Crossfade(
                            targetState = copied,
                            label = "copy_icon",
                        ) { isCopied ->
                            copyIcon(isCopied)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
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
 * @param text The source text to display and translate.
 * @param modifier Modifier applied to the outer Column container.
 * @param textStyle Style applied to the displayed text.
 * @param onTranslated Callback invoked whenever the displayed text changes,
 * either translated or reverted.
 * @param button Slot used to render the translate action UI.
 * The slot receives:
 * - whether the text is currently translated,
 * - whether a translation request is in progress,
 * - and a callback used to trigger translation.
 */
@Composable
fun Translate(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    onTranslated: (String) -> Unit = {},
    button: @Composable (
        isTranslated: Boolean,
        isLoading: Boolean,
        onClick: () -> Unit,
    ) -> Unit = { isTranslated, isLoading, onClick ->
        DefaultTranslateButton(
            isTranslated = isTranslated,
            isLoading = isLoading,
            onClick = onClick,
        )
    },
) {
    val scope = rememberCoroutineScope()
    val controller = remember { TranslateController(scope) }
    val state by controller.state.collectAsStateWithLifecycle()

    LaunchedEffect(text) { controller.setText(text) }

    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(state.displayText) {
        if (!initialized) {
            initialized = true
            return@LaunchedEffect
        }

        onTranslated(state.displayText)
    }

    Column(modifier = modifier) {
        Text(
            text = state.displayText,
            style = textStyle,
        )

        Spacer(modifier = Modifier.height(8.dp))

        button(
            state.isTranslated,
            state.isLoading,
        ) {
            controller.toggleTranslate()
        }

        state.error?.let { error ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun DefaultTranslateButton(
    isTranslated: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        enabled = !isLoading,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                strokeWidth = 2.dp,
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(if (isTranslated) "Undo translation" else "Translate")
    }
}