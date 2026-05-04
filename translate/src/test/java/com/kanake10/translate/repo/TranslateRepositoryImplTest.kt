package com.kanake10.translate.repo

import com.kanake10.translate.remote.api.TranslateApi
import com.kanake10.translate.remote.dtos.text.TranslateResponse
import com.kanake10.translate.remote.dtos.text.TranslationDto
import com.kanake10.translate.util.BaseTest
import com.kanake10.translate.util.TranslateResult
import com.kanake10.translate.util.TranslateTestSamples
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TranslateRepositoryImplTest : BaseTest() {

    private val api = mockk<TranslateApi>()
    private lateinit var repository: TranslateRepositoryImpl

    @Before
    fun setUp() {
        repository = TranslateRepositoryImpl(api)
    }

    @Test
    fun translate_returns_error_when_text_is_blank() = runTest {
        val result = repository.translate("", target = "fr")
        assertTrue(result is TranslateResult.Error)
        coVerify(exactly = 0) { api.translate(any()) }
    }

    @Test
    fun translate_returns_error_when_api_throws() = runTest {

        coEvery { api.translate(any()) } throws RuntimeException("Network error")

        val result = repository.translate("Hello", "en", "fr")

        assertTrue(result is TranslateResult.Error)

        coVerify(exactly = 1) { api.translate(any()) }
    }

    @Test
    fun translate_returns_success_when_api_succeeds() = runTest {

        coEvery { api.translate(any()) } returns TranslateResponse(
            translations = TranslationDto(
                text = "Hello",
                translation = "Bonjour",
                source = "en",
                target = "fr"
            ),
            details = emptyMap()
        )
        val result = repository.translate("Hello", "en", "fr")
        assertTrue(result is TranslateResult.Success)
        coVerify(exactly = 1) { api.translate(any()) }
    }

    @Test
    fun batchTranslate_returns_error_when_empty_list() = runTest {
        val result = repository.batchTranslate(emptyList(), target = "fr")
        assertTrue(result is TranslateResult.Error)
        coVerify(exactly = 0) { api.batchTranslate(any()) }
    }

    @Test
    fun batchTranslate_returns_success() = runTest {

        coEvery { api.batchTranslate(any()) } returns TranslateTestSamples.batchResponse

        val result = repository.batchTranslate(
            texts = TranslateTestSamples.batchInput,
            source = "en",
            target = "fr"
        )

        val success = result as? TranslateResult.Success
        assertNotNull(success)

        val data = success!!.data

        assertEquals("Bonjour le monde", data[0].translatedText)
        assertEquals("Comment vas-tu?", data[1].translatedText)
        assertEquals("Bonjour", data[2].translatedText)

        coVerify(exactly = 1) { api.batchTranslate(any()) }
    }

    @Test
    fun translateSubtitles_success() = runTest {

        coEvery { api.translateSubtitles(any()) } returns TranslateTestSamples.subtitleResponse

        val result = repository.translateSubtitles(
            TranslateTestSamples.subtitleInput
        )

        val success = result as? TranslateResult.Success
        assertNotNull(success)

        assertEquals(
            TranslateTestSamples.subtitleResponse.content,
            success!!.data.content
        )

        coVerify(exactly = 1) { api.translateSubtitles(any()) }
    }

    @Test
    fun translateEmail_success() = runTest {

        coEvery { api.translateEmail(any()) } returns TranslateTestSamples.emailResponse

        val result = repository.translateEmail(
            TranslateTestSamples.emailInput
        )

        val success = result as? TranslateResult.Success
        assertNotNull(success)

        assertEquals(
            TranslateTestSamples.emailResponse.subject,
            success!!.data.subject
        )

        coVerify(exactly = 1) { api.translateEmail(any()) }
    }


    @Test
    fun translateHtml_success() = runTest {

        coEvery { api.translateHtml(any()) } returns TranslateTestSamples.htmlResponse

        val result = repository.translateHtml(
            TranslateTestSamples.htmlInput
        )

        val success = result as? TranslateResult.Success
        assertNotNull(success)

        assertEquals(
            TranslateTestSamples.htmlResponse.html,
            success!!.data.html
        )

        coVerify(exactly = 1) { api.translateHtml(any()) }
    }
}
