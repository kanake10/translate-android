package com.kanake10.translate_ui.vm

import com.kanake10.translate.TranslateClient.translate
import com.kanake10.translate.util.TranslateResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale


data class TranslateBtnState(
    val text: String = "",
    val isTranslated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val phoneLanguage: String = Locale.getDefault().language,
    val originalText: String = "",
)

internal class TranslateController(
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(TranslateBtnState())
    val state: StateFlow<TranslateBtnState> = _state

    private var originalText: String = ""

    fun setText(value: String) {
        _state.update {
            it.copy(
                text = value,
                originalText = value,
                isTranslated = false,
                error = null
            )
        }
    }

    fun toggleTranslate() {
        val current = _state.value

        if (current.isTranslated) {
            _state.update {
                it.copy(
                    text = current.originalText,
                    isTranslated = false
                )
            }
            return
        }

        if (current.text.isBlank() || current.isLoading) return

        _state.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }

        scope.launch {
            when (val result =translate(
                text = current.text,
                source = "auto",
                target = current.phoneLanguage,
            )) {
                is TranslateResult.Success -> {
                    originalText = current.text
                    _state.update {
                        it.copy(
                            text = result.data.translatedText,
                            isTranslated = true,
                            isLoading = false
                        )
                    }
                }

                is TranslateResult.Error -> {
                    _state.update {
                        it.copy(
                            error = result.error.toMessage(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}