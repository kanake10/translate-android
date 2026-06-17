# ─── Public API entry points ───────────────────────────────────────────────
-keep class com.kanake10.translate.TranslateClient { *; }
-keep class com.kanake10.translate.TranslateConfiguration { *; }
-keep class com.kanake10.translate.TranslateConfiguration$Builder { *; }

# ─── Sealed classes + all subclasses ($ covers nested classes in R8) ────────
-keep class com.kanake10.translate.util.TranslateResult$** { *; }
-keep class com.kanake10.translate.util.TranslateError$** { *; }

# ─── Request models ──────────────────────────────────────────────────────────
-keep class com.kanake10.translate.domain.models.**Request { *; }

# ─── Moshi ───────────────────────────────────────────────────────────────────
-keepattributes Signature, *Annotation*
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }
-keepclassmembers class ** {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}

# ─── Retrofit ────────────────────────────────────────────────────────────────
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions

# ─── OkHttp ──────────────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okio.**