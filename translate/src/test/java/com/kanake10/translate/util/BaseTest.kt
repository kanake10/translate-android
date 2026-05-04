package com.kanake10.translate.util

import io.mockk.clearAllMocks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

/**
 * this will go to test-utils late & [TTS]
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseTest {

    protected val dispatcher = StandardTestDispatcher()

    @Before
    fun baseSetUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun baseTearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }
}