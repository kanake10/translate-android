# Changelog

---

## [1.1.0] - 2026-04-28

### Added
- Subtitle translation support (SRT / VTT format)
- Email translation API (subject + HTML body)
- HTML translation support (preserves tags & structure)
- Direct delegation API via `TranslateClient`
- sorted deps

---

## [1.0.0] - 2026-04-28

### Initial Release

First public release of the Translate SDK.

### Added
- Core translation SDK (`translate`)
- Jetpack Compose UI module (`translate-ui`)
- Retrofit-based networking layer

---

### 📦 Installation

```kotlin
implementation("io.github.kanake10:translate:1.1.0")
implementation("io.github.kanake10:translate-ui:1.1.0")