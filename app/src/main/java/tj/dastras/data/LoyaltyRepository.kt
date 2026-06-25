package tj.dastras.data

import tj.dastras.core.api.AchievementApiResponse
import tj.dastras.core.api.BonusTransactionApiResponse
import tj.dastras.core.api.LoyaltyLevelResponse
import tj.dastras.core.api.LoyaltySummaryResponse
import tj.dastras.core.api.PagedResponse
import tj.dastras.core.api.QrTokenResponse
import tj.dastras.core.api.RelaxApiService
import tj.dastras.core.api.dataOrThrow
import javax.inject.Inject
import javax.inject.Singleton

private const val TTL_MS = 5 * 60 * 1000L // 5 minutes

@Singleton
class LoyaltyRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    @Volatile private var cachedSummary: LoyaltySummaryResponse? = null
    @Volatile private var summaryFetchedAt: Long = 0L

    @Volatile private var cachedLevels: List<LoyaltyLevelResponse>? = null
    @Volatile private var levelsFetchedAt: Long = 0L

    @Volatile private var cachedAchievements: List<AchievementApiResponse>? = null
    @Volatile private var achievementsFetchedAt: Long = 0L

    suspend fun getSummary(forceRefresh: Boolean = false): LoyaltySummaryResponse {
        val now = System.currentTimeMillis()
        if (!forceRefresh && cachedSummary != null && now - summaryFetchedAt < TTL_MS) {
            return cachedSummary!!
        }
        return api.getLoyaltySummary().dataOrThrow().also {
            cachedSummary = it
            summaryFetchedAt = System.currentTimeMillis()
        }
    }

    suspend fun getLevels(forceRefresh: Boolean = false): List<LoyaltyLevelResponse> {
        val now = System.currentTimeMillis()
        if (!forceRefresh && cachedLevels != null && now - levelsFetchedAt < TTL_MS) {
            return cachedLevels!!
        }
        return api.getLoyaltyLevels().dataOrThrow().also {
            cachedLevels = it
            levelsFetchedAt = System.currentTimeMillis()
        }
    }

    suspend fun getTransactions(page: Int = 1, pageSize: Int = 20): PagedResponse<BonusTransactionApiResponse> =
        api.getBonusTransactions(page, pageSize).dataOrThrow()

    suspend fun getAchievements(forceRefresh: Boolean = false): List<AchievementApiResponse> {
        val now = System.currentTimeMillis()
        if (!forceRefresh && cachedAchievements != null && now - achievementsFetchedAt < TTL_MS) {
            return cachedAchievements!!
        }
        return api.getAchievements().dataOrThrow().also {
            cachedAchievements = it
            achievementsFetchedAt = System.currentTimeMillis()
        }
    }

    fun invalidateSummary() {
        cachedSummary = null
        summaryFetchedAt = 0L
    }

    suspend fun getQrToken(): QrTokenResponse =
        api.getQrToken().dataOrThrow()
}
