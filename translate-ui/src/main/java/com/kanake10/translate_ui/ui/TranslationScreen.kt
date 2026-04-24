package com.kanake10.translate_ui.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kanake10.translate.TranslateClient
import com.kanake10.translate.domain.models.Language
import com.kanake10.translate_ui.vm.TranslateController
import com.kanake10.translate_ui.vm.TranslationViewModel

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
    val viewModel: TranslationViewModel =
        viewModel(factory = TranslationViewModel.Factory)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.languages) {
        translateFrom?.let { code ->
            uiState.languages.find { it.code == code }?.let { viewModel.selectSource(it) }
        }
        translateTo?.let { code ->
            uiState.languages.find { it.code == code }?.let { viewModel.selectTarget(it) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
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
                    .menuAnchor()
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
                    .menuAnchor()
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
        Column {
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

@Composable
fun Translate(
    postText: String,
    modifier: Modifier = Modifier,
    onTranslated: (translatedText: String) -> Unit = {},
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
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text(if (isTranslated) "Undo" else "Translate")
            }
        }
    },
) {
    val controller = remember { TranslateController(TranslateClient.getClient()) }

    DisposableEffect(Unit) { onDispose { controller.release() } }
    LaunchedEffect(postText) { controller.setText(postText) }

    val text by controller.text.collectAsStateWithLifecycle()
    val isTranslated by controller.isTranslated.collectAsStateWithLifecycle()
    val isLoading by controller.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(text, isTranslated) {
        if (isTranslated) {
            onTranslated(text)
        }
    }

    Box(modifier = modifier) {
        buttonContent(isTranslated, isLoading) { controller.toggleTranslate() }
    }
}