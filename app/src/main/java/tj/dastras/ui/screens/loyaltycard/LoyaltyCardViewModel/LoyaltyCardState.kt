package tj.dastras.ui.screens.loyaltycard.LoyaltyCardViewModel

import tj.dastras.core.api.AchievementApiResponse
import tj.dastras.core.api.LoyaltyLevelResponse
import tj.dastras.core.api.LoyaltySummaryResponse
import tj.dastras.data.UserProfile

data class LoyaltyCardState(
    val summary: LoyaltySummaryResponse? = null,
    val levels: List<LoyaltyLevelResponse> = emptyList(),
    val achievements: List<AchievementApiResponse> = emptyList(),
    val isLoading: Boolean = true,
    val lastUpdated: Long = 0L,
    val isRefreshing: Boolean = false,
)

sealed class QrState {
    object Loading : QrState()
    data class Ready(
        val token: String,
        val expiresAt: java.time.Instant,
    ) : QrState()
    object Error : QrState()
}