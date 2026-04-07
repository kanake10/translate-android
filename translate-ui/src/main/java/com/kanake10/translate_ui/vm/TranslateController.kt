package com.kanake10.translate_ui.vm

import com.kanake10.translate.repo.TranslateRepository
import com.kanake10.translate.util.TranslateResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TranslateController(
    val repository: TranslateRepository
) {

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

        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.translate(
                text = _text.value,
                source = "auto",
                target = target
            )

            withContext(Dispatchers.Main) {
                _isLoading.value = false

                when (result) {
                    is TranslateResult.Success -> {
                        originalText = _text.value
                        _text.value = result.data.translatedText
                        _isTranslated.value = true
                    }

                    is TranslateResult.Error -> {
                        _error.value = result.error.toMessage()
                    }
                }
            }
        }
    }
}