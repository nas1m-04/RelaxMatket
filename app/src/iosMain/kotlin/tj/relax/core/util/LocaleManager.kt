package tj.relax.core.util

import platform.Foundation.NSUserDefaults

/**
 * iOS has no in-place locale switch like [androidx.appcompat.app.AppCompatDelegate] on Android —
 * overriding "AppleLanguages" only takes effect after the app process restarts. The caller is
 * responsible for prompting the user to relaunch after [setLanguage].
 */
actual object LocaleManager {
    actual val RUSSIAN = "ru"
    actual val ENGLISH = "en"
    actual val TAJIK   = "tg"

    private const val LanguagesKey = "AppleLanguages"

    actual fun setLanguage(languageCode: String) {
        NSUserDefaults.standardUserDefaults.setObject(listOf(languageCode), forKey = LanguagesKey)
        NSUserDefaults.standardUserDefaults.synchronize()
    }

    actual fun getCurrentLanguage(): String {
        val languages = NSUserDefaults.standardUserDefaults.arrayForKey(LanguagesKey)
        val first = (languages?.firstOrNull() as? String)?.substringBefore("-") ?: return RUSSIAN
        return when (first) {
            ENGLISH -> ENGLISH
            TAJIK   -> TAJIK
            else    -> RUSSIAN
        }
    }
}
