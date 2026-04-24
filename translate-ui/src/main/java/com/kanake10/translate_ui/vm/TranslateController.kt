package com.kanake10.translate_ui.vm

import com.kanake10.translate.repo.TranslateRepository
import com.kanake10.translate.util.TranslateResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class TranslateController internal constructor(
    private val repository: TranslateRepository,
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text

    private val _isTranslated = MutableStateFlow(false)
    val isTranslated: StateFlow<Boolean> = _isTranslated

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var originalText: String = ""

    fun setText(value: String) {
        _text.value = value
        if (_isTranslated.value) {
            _isTranslated.value = false
            originalText = ""
        }
    }

    fun toggleTranslate(target: String = "en") {
        if (_isTranslated.value) {
            _text.value = originalText
            _isTranslated.value = false
            return
        }

        if (_text.value.isBlank()) return

        _isLoading.value = true
        _error.value = null

        scope.launch {
            when (val result = repository.translate(
                text = _text.value,
                source = "auto",
                target = target,
            )) {
                is TranslateResult.Success -> {
                    originalText = _text.value
                    _text.value = result.data.translatedText
                    _isTranslated.value = true
                }
                is TranslateResult.Error -> {
                    _error.value = result.error.toMessage()
                }
            }
            _isLoading.value = false
        }
    }

    fun release() {
        scope.cancel()
    }
}