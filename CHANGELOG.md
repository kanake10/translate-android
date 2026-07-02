# Changelog

## [v1.3.0] -> 2026-07-02

### Added
- Copy-paste functionality for translated text (#27)
- Target language persistence during screen configuration changes (#34)
- Paparazzi snapshot testing setup and content copy snapshots (#23, #28)
- Consumer ProGuard rules for library optimization (#25)
- Continuous Integration (CI/CD) build pipeline (#30)

### Changed
- State management refactored to expose states safely (#22)
- UI components updated to respect device safe areas (#31)
- Theme architecture refactored to move parent theme to `translate-ui` module (#33)

### Fixed
- Build failures in both T-app and T-chat applications (#35)
- Detekt code quality violations (#29)

---

## [v1.2.0] -> 2026-05-07

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

## [v1.1.0] -> 2026-04-28

### Added
- Subtitle translation support (SRT / VTT format)
- Email translation API (subject + HTML body)
- HTML translation support (preserves tags & structure)
- Direct delegation API via `TranslateClient`
- sorted deps

---

## [v1.0.0] -> 2026-04-28

### Initial Release

First public release of the Translate SDK.

### Added
- Core translation SDK (`translate`)
- Jetpack Compose UI module (`translate-ui`)
- Retrofit-based networking layer

---

### 📦 Installation

```kotlin
implementation("io.github.kanake10:translate:VERSION")
implementation("io.github.kanake10:translate-ui:VERSION")
```
