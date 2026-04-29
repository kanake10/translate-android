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
            toolVersion = "1.23.6"
            config.setFrom(rootProject.file("config/detekt.yml"))
            buildUponDefaultConfig = true
            allRules = false
            autoCorrect = true
        }
    }
}