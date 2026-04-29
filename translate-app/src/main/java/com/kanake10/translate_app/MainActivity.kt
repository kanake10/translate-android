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
package com.kanake10.translate_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kanake10.translate.TranslateClient
import com.kanake10.translate.TranslateConfiguration
import com.kanake10.translate_ui.ui.Translate
import com.kanake10.translate_ui.ui.TranslationScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val translateConfiguration = TranslateConfiguration.Builder(
            apiKey = ""
        )
            .timeoutSeconds(30)
            .build()

        TranslateClient.initialize(translateConfiguration)

        setContent {
            MaterialTheme {
                TranslationScreen()
            }
        }
    }
}

/**
 *   call FeedScreen in setContent {}
 */
data class Post(
    val id: String,
    val post: String,
    val author: String,
)

@Composable
fun FeedScreen() {
    val posts = remember {
        listOf(
            Post(id = "3", post = "Bonjour, comment ça va?", author = "Charlie"),
        )
    }

    val translatedTexts = remember { mutableStateMapOf<String, String>() }

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(posts, key = { it.id }) { post ->

            val textToShow = translatedTexts[post.id] ?: post.post

            Card {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(post.author)

                    Text(textToShow)

                    Translate(
                        postText = post.post,
                        modifier = Modifier.padding(top = 8.dp),
                        onTranslated = { translated ->
                            translatedTexts[post.id] = translated
                        }
                    )
                }
            }
        }
    }
}