plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.metalava)
  alias(libs.plugins.ktfmt)
    id("com.vanniktech.maven.publish")
}

android {
  namespace = "com.kanake10.translate_ui"
  compileSdk { version = release(36) }

  defaultConfig {
    minSdk = 24

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }

  buildFeatures { compose = true }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(
        groupId = "io.github.kanake10",
        artifactId = "translate-ui",
        version = "1.0.0"
    )

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
            url.set("https://github.com/kanake10/translate")
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
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  api(project(":translate"))
}
