plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.ksp)
  alias(libs.plugins.ktfmt)
    alias(libs.plugins.kotlin.android)
}

android {
  namespace = "com.kanake10.translate_db"
  compileSdk { version = release(36) }

  defaultConfig {
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
}

dependencies {
  implementation(libs.room.runtime)
    implementation(libs.core.ktx)
    ksp(libs.room.compiler)
  implementation(libs.room.ktx)
  implementation(libs.coroutines.android)
}
