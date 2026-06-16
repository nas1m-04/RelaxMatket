package tj.dastras.data

import tj.dastras.data.remote.BonusTransactionApiResponse
import tj.dastras.data.remote.LoyaltyLevelResponse
import tj.dastras.data.remote.LoyaltySummaryResponse
import tj.dastras.data.remote.PagedResponse
import tj.dastras.data.remote.RelaxApiService
import tj.dastras.data.remote.dataOrThrow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoyaltyRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    suspend fun getSummary(): LoyaltySummaryResponse =
        api.getLoyaltySummary().dataOrThrow()

    suspend fun getLevels(): List<LoyaltyLevelResponse> =
        api.getLoyaltyLevels().dataOrThrow()

    suspend fun getTransactions(page: Int = 1, pageSize: Int = 20): PagedResponse<BonusTransactionApiResponse> =
        api.getBonusTransactions(page, pageSize).dataOrThrow()
}
