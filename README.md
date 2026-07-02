# Translate Android SDK

A lightweight Android SDK for integrating powerful text translation,built on the [TranslatePlus API](https://docs.translateplus.io/). Supports single text, batch texts, subtitle, email and HTML translation with an optional Jetpack Compose UI module included out of the box.

---

## Features

-  Single text translation
-  Batch translation
-  Subtitle translation (SRT & VTT)
-  Email translation (subject + body)
-  HTML translation with tag preservation
-  Fetch all supported languages
-  Optional pre-built Compose UI module

---

## Installation

Add the dependencies to your module-level `build.gradle.kts`:

```kotlin
// Core SDK (required)
implementation("io.github.kanake10:translate:VERSION")

// Optional Compose UI module
implementation("io.github.kanake10:translate-ui:VERSION")
```

---

## Initialization

```kotlin
val translateConfiguration = TranslateConfiguration.Builder(
    apiKey = "YOUR_API_KEY"
)
    .build()

TranslateClient.initialize(translateConfiguration)
```

### Configuration Options

| Option | Required | Default | Description |
|---|---|---|---|
| `apiKey` | ✅ | — | Your TranslatePlus API key |
| `baseUrl` | ❌ | `https://api.translateplus.io/` | Override the base API URL |
| `timeoutSeconds` | ❌ | `30` | Network timeout in seconds |
| `okHttpClient` | ❌ | `null` | Provide a custom `OkHttpClient` (e.g. with logging or caching) |

---

## Core SDK Usage

###  Single Translation

```kotlin
val result = TranslateClient.translate(
    text = "Hello world",
    source = "en", // Optional, defaults to "auto"
    target = "fr"
)
```

###  Batch Translation

Translate multiple strings :

```kotlin
val result = TranslateClient.batchTranslate(
    texts = listOf(
        "Hello world",
        "How are you?",
        "Good morning"
    ),
    source = "en",
    target = "fr"
)
```

###  Subtitle Translation

Translate SRT or VTT subtitle

```kotlin
val srtContent = "1\n" +
                "00:00:01,000 --> 00:00:02,000\n" +
                "Hello world\n"

val result = TranslateClient.translateSubtitles(
    SubtitleRequest(
        format = "srt", // or "vtt"
        content = srtContent,
        source = "en",
        target = "es"
    )
)
```

###  Email Translation

Translate both the subject and HTML body of an email:

```kotlin
val result = TranslateClient.translateEmail(
    EmailRequest(
        subject = "Welcome to our service",
        email_body = "<p>Thank you for signing up!</p><p>We are happy to have you.</p>",
        source = "auto",
        target = "fr"
    )
)
```

###  HTML Translation

Translate HTML content
```kotlin
val html = """
            <p>Hello <b>world</b>!</p>
            <p>This is a <i>test</i>.</p>
        """.trimIndent()

val result = TranslateClient.translateHtml(
    HtmlRequest(
        html = html,
        source = "auto",
        target = "fr"
    )
)
```

###  Supported Languages

```kotlin
val result = TranslateClient.getSupportedLanguages()
```

---

## Optional UI Module

The `translate-ui` artifact provides ready-to-use Jetpack Compose components so you can drop a full translation experience into your app without writing any UI code yourself. Every component is also fully customizable via slot APIs.

### TranslationScreen

`TranslationScreen` is a complete, self-contained translation screen. It manages its own ViewModel, handles loading and error states internally, and wires everything together for you.

Out of the box it renders:
- A **language selector** — two dropdowns (From / To) populated with all supported languages
- A **text input field** for the text to translate
- A **translated output area** that appears once a result is ready
- A **Translate button** that shows a loading spinner while the request is in progress
- An **error message** area displayed when a request fails

You can optionally pre-select source and target languages:

```kotlin
TranslationScreen(
    translateFrom = "en",
    translateTo = "fr",
    translateLanguageSelector = null
)
```
Available slots:

| Slot | Description |
|---|---|
| `headerContent` | Optional composable rendered at the top of the screen |
| `translateLanguageSelector` | Language picker UI (receives language list + selection callbacks) |
| `translateInputField` | Text input area (receives current text + onChange callback) |
| `translationContent` | Displays the translated result |
| `translateButton` | The action button (receives `isLoading` + `onClick`) |
| `translateErrorContent` | Error display (receives error message string) |

---


## Contributing

Contributions are welcome! Whether it's a bug fix, a new feature or an improvement to the docs. Feel free to open an issue or submit a pull request.

---

## License

```
Copyright 2026 Ezra Kanake

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
