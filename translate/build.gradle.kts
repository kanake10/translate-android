plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.metalava)
  alias(libs.plugins.ktfmt)
    id("com.vanniktech.maven.publish")
}

android {
  namespace = "com.kanake10.translate"
  compileSdk { version = release(36) }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(
        groupId = "io.github.kanake10",
        artifactId = "translate",
        version = "1.0.0"
    )

    pom {
        name.set("Translate SDK")
        description.set("Core translation SDK")
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

metalava { filename = "api/$name-api.txt" }

dependencies {
  implementation(libs.retrofit.core)
  implementation(libs.retrofit.moshi)
  implementation(libs.okhttp.core)
}
