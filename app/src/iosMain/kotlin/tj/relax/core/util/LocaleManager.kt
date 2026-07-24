package tj.relax.core.util

actual object LocaleManager {
    actual val RUSSIAN = "ru"
    actual val ENGLISH = "en"
    actual val TAJIK   = "tg"

    actual fun setLanguage(languageCode: String) {
        TODO("iOS: NSUserDefaults \"AppleLanguages\" override + restart (Phase 9)")
    }

    actual fun getCurrentLanguage(): String = RUSSIAN
}
