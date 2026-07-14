package tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel

import tj.relax.core.api.AchievementApiResponse
import tj.relax.core.api.BonusTransactionApiResponse
import tj.relax.core.api.LoyaltyLevelResponse
import tj.relax.core.api.LoyaltySummaryResponse
import tj.relax.data.UserProfile

data class LoyaltyCardState(
    val summary: LoyaltySummaryResponse? = null,
    val levels: List<LoyaltyLevelResponse> = emptyList(),
    val achievements: List<AchievementApiResponse> = emptyList(),
    val transactions: List<BonusTransactionApiResponse> = emptyList(),
    val txPage: Int = 1,
    val txTotal: Int = 0,
    val txTotalPages: Int = 0,
    val isLoadingMoreTx: Boolean = false,
    val isLoading: Boolean = true,
    val lastUpdated: Long = 0L,
    val isRefreshing: Boolean = false,
) {
    val hasMoreTx: Boolean get() = transactions.isNotEmpty() && txPage < txTotalPages
}

sealed class QrState {
    object Loading : QrState()
    data class Ready(
        val token: String,
        val expiresAt: java.time.Instant,
    ) : QrState()
    object Error : QrState()
}