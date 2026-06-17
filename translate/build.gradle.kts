plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.metalava)
    alias(libs.plugins.ktfmt)
    alias(libs.plugins.publish)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.kanake10.translate"
    compileSdk { version = release(36) }

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

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

    coordinates(groupId = "io.github.kanake10", artifactId = "translate", version = "1.2.0")

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
    api(libs.okhttp.core)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.moshi)
    implementation(libs.core.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
}
