package tj.relax.data

import com.russhwolf.settings.Settings
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tj.relax.core.api.AchievementApiResponse
import tj.relax.core.api.LoyaltyLevelResponse
import tj.relax.core.api.LoyaltySummaryResponse

/** Persists loyalty data locally so it's available instantly on next app open, without a network call. */
class LoyaltyLocalStore(
    private val settings: Settings,
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun saveSummary(summary: LoyaltySummaryResponse) =
        settings.putString(KEY_SUMMARY, json.encodeToString<LoyaltySummaryResponse>(summary))

    fun getSummary(): LoyaltySummaryResponse? =
        settings.getStringOrNull(KEY_SUMMARY)?.let {
            try { json.decodeFromString<LoyaltySummaryResponse>(it) } catch (e: Exception) { null }
        }

    fun saveLevels(levels: List<LoyaltyLevelResponse>) =
        settings.putString(KEY_LEVELS, json.encodeToString<List<LoyaltyLevelResponse>>(levels))

    fun getLevels(): List<LoyaltyLevelResponse>? =
        settings.getStringOrNull(KEY_LEVELS)?.let {
            try { json.decodeFromString<List<LoyaltyLevelResponse>>(it) } catch (e: Exception) { null }
        }

    fun saveAchievements(achievements: List<AchievementApiResponse>) =
        settings.putString(KEY_ACHIEVEMENTS, json.encodeToString<List<AchievementApiResponse>>(achievements))

    fun getAchievements(): List<AchievementApiResponse>? =
        settings.getStringOrNull(KEY_ACHIEVEMENTS)?.let {
            try { json.decodeFromString<List<AchievementApiResponse>>(it) } catch (e: Exception) { null }
        }

    fun clear() {
        settings.remove(KEY_SUMMARY)
        settings.remove(KEY_LEVELS)
        settings.remove(KEY_ACHIEVEMENTS)
    }

    companion object {
        private const val KEY_SUMMARY      = "summary"
        private const val KEY_LEVELS       = "levels"
        private const val KEY_ACHIEVEMENTS = "achievements"
    }
}
