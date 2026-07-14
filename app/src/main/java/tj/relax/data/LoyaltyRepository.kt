package tj.relax.data

import tj.relax.core.api.AchievementApiResponse
import tj.relax.core.api.BonusTransactionApiResponse
import tj.relax.core.api.LoyaltyLevelResponse
import tj.relax.core.api.LoyaltySummaryResponse
import tj.relax.core.api.PagedResponse
import tj.relax.core.api.QrTokenResponse
import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.dataOrThrow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoyaltyRepository @Inject constructor(
    private val api: RelaxApiService,
    private val localStore: LoyaltyLocalStore,
) {
    private val summaryCache      = MemoryCache<LoyaltySummaryResponse>(ttlMs = 2 * 60_000L)
    private val levelsCache       = MemoryCache<List<LoyaltyLevelResponse>>(ttlMs = 10 * 60_000L)
    private val achievementsCache = MemoryCache<List<AchievementApiResponse>>(ttlMs = 5 * 60_000L)

    // Returns cached summary from RAM or disk — instant, no network
    fun getCachedSummary(): LoyaltySummaryResponse? =
        summaryCache.get() ?: localStore.getSummary()

    // Returns cached levels from RAM or disk — instant, no network
    fun getCachedLevels(): List<LoyaltyLevelResponse>? =
        levelsCache.get() ?: localStore.getLevels()

    // Returns cached achievements from RAM or disk — instant, no network
    fun getCachedAchievements(): List<AchievementApiResponse>? =
        achievementsCache.get() ?: localStore.getAchievements()

    suspend fun getSummary(): LoyaltySummaryResponse =
        summaryCache.get() ?: api.getLoyaltySummary().dataOrThrow().also {
            summaryCache.set(it)
            localStore.saveSummary(it)
        }

    suspend fun getLevels(): List<LoyaltyLevelResponse> =
        levelsCache.get() ?: api.getLoyaltyLevels().dataOrThrow().also {
            levelsCache.set(it)
            localStore.saveLevels(it)
        }

    suspend fun getAchievements(): List<AchievementApiResponse> =
        achievementsCache.get() ?: api.getAchievements().dataOrThrow().also {
            achievementsCache.set(it)
            localStore.saveAchievements(it)
        }

    suspend fun getTransactions(page: Int = 1, pageSize: Int = 20): PagedResponse<BonusTransactionApiResponse> =
        api.getBonusTransactions(page, pageSize).dataOrThrow()

    suspend fun getQrToken(): QrTokenResponse =
        api.getQrToken().dataOrThrow()

    fun invalidateCache() {
        summaryCache.invalidate()
        levelsCache.invalidate()
        achievementsCache.invalidate()
    }
}
