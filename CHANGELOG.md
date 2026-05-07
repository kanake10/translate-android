# Changelog

---

## [1.2.0] - 2026-05-07

### Added
- Java interoperability improvements (`@JvmOverloads` on translate and batch translate calls)
- Dokka documentation setup
- KDoc documentation across public APIs
- Translate tests with mock library
- Scrollable translate content

### Changed
- Translate component tweaks (drop-in ready)
- Show language code in the chat sample app

### Fixed
- Dropdown menu now correctly launches on click
- Dokka warnings resolved

### Internal
- Detekt and Spotless setup for code quality

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
implementation("io.github.kanake10:translate:1.2.0")
implementation("io.github.kanake10:translate-ui:1.2.0")
```