package tj.dastras.core.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/** Supported interface languages and helpers to switch between them at runtime. */
object LocaleManager {
    const val RUSSIAN = "ru"
    const val ENGLISH = "en"
    const val TAJIK   = "tg"

    /** Applies the given language code app-wide (persists across restarts). */
    fun setLanguage(languageCode: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    }

    /** Returns the currently applied language code, defaulting to Russian. */
    fun getCurrentLanguage(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        if (locales.isEmpty) return RUSSIAN
        return when (locales[0]?.language) {
            ENGLISH -> ENGLISH
            TAJIK   -> TAJIK
            else    -> RUSSIAN
        }
    }
}
