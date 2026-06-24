plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.metalava)
    alias(libs.plugins.ktfmt)
    alias(libs.plugins.publish)
    alias(libs.plugins.dokka)
    alias(libs.plugins.paparazzi)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.kanake10.translate_ui"
    compileSdk { version = release(36) }

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures { compose = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(groupId = "io.github.kanake10", artifactId = "translate-ui", version = "1.2.0")

    pom {
        name.set("Translate UI")
        description.set("Compose UI for Translate SDK")
        url.set("https://github.com/kanake10/translate-android")

        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("kanake10")
                name.set("Ezra Kanake")
                url.set("https://github.com/kanake10")
            }
        }

        scm {
            url.set("https://github.com/kanake10/translate-android")
            connection.set("scm:git:git://github.com/kanake10/translate-android.git")
            developerConnection.set("scm:git:ssh://git@github.com/kanake10/translate-android.git")
        }
    }
}

metalava {
    filename = "api/$name-api.txt"
    apiCompatAnnotations = listOf("androidx.compose.runtime.Composable")
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    api(libs.androidx.lifecycle.viewmodel.compose)
    api(project(":translate"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.core.ktx)
    testImplementation(libs.google.testparameterinjector)

    debugImplementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
