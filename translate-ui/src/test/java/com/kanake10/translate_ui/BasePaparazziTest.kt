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
package com.kanake10.translate_ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.NightMode
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.runner.RunWith

/**
 * This base class allows us to write Paparazzi tests that validate composable content in both light and dark theme
 * using a parameterized test. Just extend this base class and call [snapshot] with your composable content.
 */
@RunWith(TestParameterInjector::class)
abstract class BasePaparazziTest {
    @get:Rule
    @Suppress("ktlint:standard:backing-property-naming", "VariableNaming")
    val _paparazzi = Paparazzi()

    @TestParameter
    val testInput: TestInput = TestInput.LIGHT_PHONE

    /**
     * Validates the supplied [content] in both light and dark theme.
     */
    fun snapshot(
        screenPaddingDp: Int = 16,
        content: @Composable () -> Unit,
    ) {
        _paparazzi.unsafeUpdateConfig(testInput.deviceConfig)

        _paparazzi.snapshot {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
            ) {
                MaterialTheme {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(screenPaddingDp.dp),
                        ) {
                            content()
                        }
                    }
                }
            }
        }
    }

    enum class TestInput(
        val deviceConfig: DeviceConfig,
    ) {
        LIGHT_PHONE(
            deviceConfig = DeviceConfig.NEXUS_5.copy(
                nightMode = NightMode.NOTNIGHT,
            ),
        ),
        DARK_PHONE(
            deviceConfig = DeviceConfig.NEXUS_5.copy(
                nightMode = NightMode.NIGHT,
            ),
        ),
//        LANDSCAPE_PHONE(
//            deviceConfig = DeviceConfig.NEXUS_5.copy(
//                orientation = ScreenOrientation.LANDSCAPE,
//            ),
//        ),
//        TABLET(
//            deviceConfig = DeviceConfig.PIXEL_C.copy(
//                orientation = ScreenOrientation.LANDSCAPE,
//            ),
//        ),
    }
}