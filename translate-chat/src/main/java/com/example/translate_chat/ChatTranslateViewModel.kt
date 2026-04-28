package com.example.translate_chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanake10.translate.TranslateClient
import com.kanake10.translate.domain.models.Language
import com.kanake10.translate.util.TranslateResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatTranslateViewModel() : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _languages = MutableStateFlow<List<Language>>(emptyList())
    val languages: StateFlow<List<Language>> = _languages

    private val _selectedSource = MutableStateFlow<Language?>(null)
    val selectedSource: StateFlow<Language?> = _selectedSource

    private val _selectedTarget = MutableStateFlow<Language?>(null)
    val selectedTarget: StateFlow<Language?> = _selectedTarget

    var isTranslating by mutableStateOf(false)
        private set

    var isLoadingLanguages by mutableStateOf(false)
        private set

    var languageLoadError by mutableStateOf<String?>(null)
        private set

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        isLoadingLanguages = true
        languageLoadError = null

        viewModelScope.launch {
            when (val result = TranslateClient.getSupportedLanguages()) {
                is TranslateResult.Success -> {
                    val langs = result.data
                    _languages.value = langs
                    _selectedSource.value = langs.find { it.code == "auto" }
                    _selectedTarget.value = langs.find { it.code == "en" }
                    isLoadingLanguages = false
                }
                is TranslateResult.Error -> {
                    languageLoadError = result.error.toMessage()
                    isLoadingLanguages = false
                }
            }
        }
    }

    fun sendMessage(text: String, sourceCode: String, targetCode: String) {
        if (text.isBlank()) return

        val message = ChatMessage(sourceText = text, isLoading = true)
        _messages.value = _messages.value + message

        viewModelScope.launch {
            isTranslating = true
            val result = TranslateClient.translate(text, sourceCode, targetCode)
            _messages.value = _messages.value.map {
                if (it.id == message.id) {
                    when (result) {
                        is TranslateResult.Success -> it.copy(
                            translatedText = result.data.translatedText,
                            isLoading = false
                        )
                        is TranslateResult.Error -> it.copy(
                            isLoading = false,
                            error = result.error.toMessage()
                        )
                    }
                } else it
            }
            isTranslating = false
        }
    }
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sourceText: String,
    val translatedText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
