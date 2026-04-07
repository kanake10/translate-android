plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.kanake10.translate"
    compileSdk {
        version = release(36)
    }

}

dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp.core)
}