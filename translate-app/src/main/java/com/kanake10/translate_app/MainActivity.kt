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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kanake10.translate.TranslateClient
import com.kanake10.translate.TranslateConfiguration
import com.kanake10.translate_ui.ui.Translate
import com.kanake10.translate_ui.ui.TranslationScreen

private const val TIMEOUT_SECONDS = 30L
private const val API_KEY = ""

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val translateConfiguration = TranslateConfiguration.Builder(
            apiKey = API_KEY
        )
            .timeoutSeconds(TIMEOUT_SECONDS)
            .build()
        TranslateClient.initialize(translateConfiguration)
        setContent {
            MaterialTheme {
                TranslationScreen()
            }
        }
    }
}

data class Post(
    val id: String,
    val body: String,
    val author: String,
)

/**
 *   call FeedScreen in setContent {}
 */
@Composable
fun FeedScreen() {
    val posts = remember {
        listOf(
            Post(id = "1", author = "Alice",   body = "Bonjour, comment ça va aujourd'hui?"),
            Post(id = "2", author = "Bob",     body = "Hola, ¿cómo estás?"),
            Post(id = "3", author = "Charlie", body = "Ciao, come stai?"),
            Post(id = "4", author = "Diana",   body = "Guten Tag, wie geht es Ihnen?"),
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(posts, key = { it.id }) { post ->
            PostCard(post)
        }
    }
}

@Composable
private fun PostCard(post: Post) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = post.author,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Translate(
                text = post.body,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}