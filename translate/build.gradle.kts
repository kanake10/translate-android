plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.metalava)
  alias(libs.plugins.ktfmt)
}

android {
  namespace = "com.kanake10.translate"
  compileSdk { version = release(36) }
}

metalava { filename = "api/$name-api.txt" }

dependencies {
  implementation(libs.retrofit.core)
  implementation(libs.retrofit.moshi)
  implementation(libs.okhttp.core)
}
