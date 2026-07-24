package tj.relax.core.util

/** Supported interface languages and helpers to switch between them at runtime. */
expect object LocaleManager {
    val RUSSIAN: String
    val ENGLISH: String
    val TAJIK: String

    /** Applies the given language code app-wide (persists across restarts). */
    fun setLanguage(languageCode: String)

    /** Returns the currently applied language code, defaulting to Russian. */
    fun getCurrentLanguage(): String
}
