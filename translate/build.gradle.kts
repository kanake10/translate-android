plugins {
    alias(libs.plugins.android.library)
    id("me.tylerbwong.gradle.metalava") version "0.5.0"
}

android {
    namespace = "com.kanake10.translate"
    compileSdk {
        version = release(36)
    }

}

metalava {
    filename = "api/$name-api.txt"
}

dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp.core)
}