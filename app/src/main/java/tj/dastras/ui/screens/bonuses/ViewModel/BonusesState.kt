package tj.dastras.ui.screens.bonuses.ViewModel

import tj.dastras.core.api.AchievementApiResponse
import tj.dastras.core.api.BonusTransactionApiResponse
import tj.dastras.core.api.LoyaltyLevelResponse
import tj.dastras.core.api.LoyaltySummaryResponse

data class BonusesState(
    val transactions: List<BonusTransactionApiResponse> = emptyList(),
    val page: Int = 1,
    val total: Int = 0,
    val totalPages: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val summary: LoyaltySummaryResponse? = null,
    val levels: List<LoyaltyLevelResponse> = emptyList(),
    val achievements: List<AchievementApiResponse> = emptyList(),
    val error: String? = null,

) {
    val hasMore: Boolean get() = transactions.isNotEmpty() && page < totalPages
}