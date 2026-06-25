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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.kanake10.translate_ui.ui.theme.TranslateTheme
import org.junit.Rule

/**
 * This base class allows us to write Paparazzi tests that validate composable content in both light and dark theme
 * using a parameterized test. Just extend this base class and call [snapshot] with your composable content.
 */
abstract class BasePaparazziTest {
    @get:Rule
    @Suppress("ktlint:standard:backing-property-naming", "VariableNaming")
    val _paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.NEXUS_5,
    )

    fun snapshot(
        screenPaddingDp: Int = 16,
        content: @Composable () -> Unit,
    ) {
        _paparazzi.snapshot {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    listOf(false, true).forEach { isDark ->
                        TranslateTheme(darkTheme = isDark) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .padding(screenPaddingDp.dp),
                                ) {
                                    content()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}