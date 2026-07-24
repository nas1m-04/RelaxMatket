package tj.relax.core.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

actual object LocaleManager {
    actual val RUSSIAN = "ru"
    actual val ENGLISH = "en"
    actual val TAJIK   = "tg"

    actual fun setLanguage(languageCode: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    }

    actual fun getCurrentLanguage(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        if (locales.isEmpty) return RUSSIAN
        return when (locales[0]?.language) {
            ENGLISH -> ENGLISH
            TAJIK   -> TAJIK
            else    -> RUSSIAN
        }
    }
}
