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
            .baseUrl("https://api.translateplus.io/")
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