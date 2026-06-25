# ─── Public Composable entry point ─────────────────────────────────────────
-keep class com.kanake10.translate_ui.screen.TranslationScreenKt { *; }

# ─── ViewModel reflection (Compose runtime instantiates via reflection) ─────
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# ─── Compose runtime ────────────────────────────────────────────────────────
-keepattributes RuntimeVisibleAnnotations
-dontwarn androidx.compose.**