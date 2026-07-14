# ── Retrofit ──────────────────────────────────────────────────────────────────
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembernames interface * {
    @retrofit2.http.* <methods>;
}
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# ── Gson (JSON serialization) ──────────────────────────────────────────────────
# Keep all data/DTO classes so Gson can deserialize them by field name
-keep class tj.relax.data.** { *; }
-keep class tj.relax.core.api.** { *; }
-keep class tj.relax.ui.screens.**.data.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ── OkHttp ─────────────────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**

# ── Coil (image loading) ───────────────────────────────────────────────────────
-dontwarn coil.**

# ── Hilt / Dagger ─────────────────────────────────────────────────────────────
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-dontwarn dagger.**

# ── ZXing (QR codes) ──────────────────────────────────────────────────────────
-keep class com.google.zxing.** { *; }

# ── Kotlin coroutines ─────────────────────────────────────────────────────────
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# ── Room database ─────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface *
-keepclassmembers @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.**

# ── Stack traces (keep line numbers in crash reports) ─────────────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
