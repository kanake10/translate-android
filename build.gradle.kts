// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ktfmt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.publish) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.paparazzi) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlin.android) apply false
}

subprojects {
    if (name != "build-logic") {

        apply(plugin = "com.diffplug.spotless")
        apply(plugin = "io.gitlab.arturbosch.detekt")

        extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
            kotlin {
                target("src/**/*.kt")
                licenseHeaderFile(
                    rootProject.file("spotless/copyright.kt"),
                    "^(package|object|import|interface)"
                )
            }
        }

        extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
            config.setFrom(rootProject.file("config/detekt.yml"))
            buildUponDefaultConfig = true
            allRules = false
            autoCorrect = true
        }
    }
}

dokka {
    moduleName.set("Translate")

    dokkaPublications.html {
        outputDirectory.set(rootDir.resolve("docs/kdoc"))

        failOnWarning.set(true)
        suppressInheritedMembers.set(true)
    }

    dokkaSourceSets.configureEach {
        documentedVisibilities(
            org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier.Public
        )
        includes.from("README.md")

        sourceLink {
            localDirectory.set(file("src/main/kotlin"))
            remoteUrl("https://github.com/kanake10/translate-android")
            remoteLineSuffix.set("#L")
        }

        externalDocumentationLinks.register("android") {
            url("https://developer.android.com/reference/")
        }

        externalDocumentationLinks.register("kotlin") {
            url("https://kotlinlang.org/api/latest/jvm/stdlib/")
        }
    }
}

dependencies {
    subprojects
        .filter {
            it.plugins.hasPlugin("com.android.library") ||
                    it.plugins.hasPlugin("org.jetbrains.kotlin.jvm")
        }
        .forEach { dokka(it) }
}