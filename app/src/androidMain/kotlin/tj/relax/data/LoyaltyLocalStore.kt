package tj.relax.data

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tj.relax.core.api.AchievementApiResponse
import tj.relax.core.api.LoyaltyLevelResponse
import tj.relax.core.api.LoyaltySummaryResponse

/** Persists loyalty data locally so it's available instantly on next app open, without a network call. */
class LoyaltyLocalStore(
    context: Context,
) {
    private val prefs = context.getSharedPreferences("loyalty_prefs", Context.MODE_PRIVATE)
    private val gson  = Gson()

    fun saveSummary(summary: LoyaltySummaryResponse) =
        prefs.edit { putString(KEY_SUMMARY, gson.toJson(summary)) }

    fun getSummary(): LoyaltySummaryResponse? =
        prefs.getString(KEY_SUMMARY, null)?.let {
            try { gson.fromJson(it, LoyaltySummaryResponse::class.java) } catch (_: Exception) { null }
        }

    fun saveLevels(levels: List<LoyaltyLevelResponse>) =
        prefs.edit { putString(KEY_LEVELS, gson.toJson(levels)) }

    fun getLevels(): List<LoyaltyLevelResponse>? =
        prefs.getString(KEY_LEVELS, null)?.let {
            try {
                val type = object : TypeToken<List<LoyaltyLevelResponse>>() {}.type
                gson.fromJson(it, type)
            } catch (_: Exception) { null }
        }

    fun saveAchievements(achievements: List<AchievementApiResponse>) =
        prefs.edit { putString(KEY_ACHIEVEMENTS, gson.toJson(achievements)) }

    fun getAchievements(): List<AchievementApiResponse>? =
        prefs.getString(KEY_ACHIEVEMENTS, null)?.let {
            try {
                val type = object : TypeToken<List<AchievementApiResponse>>() {}.type
                gson.fromJson(it, type)
            } catch (_: Exception) { null }
        }

    fun clear() = prefs.edit { clear() }

    companion object {
        private const val KEY_SUMMARY      = "summary"
        private const val KEY_LEVELS       = "levels"
        private const val KEY_ACHIEVEMENTS = "achievements"
    }
}
