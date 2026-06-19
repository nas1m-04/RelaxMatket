package tj.dastras.ui.screens.loyalty

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.dastras.data.AuthRepository
import tj.dastras.data.BonusTransaction
import tj.dastras.data.LoyaltyLevel
import tj.dastras.data.LoyaltyRepository
import tj.dastras.data.MockData
import tj.dastras.data.UserProfile
import tj.dastras.data.UserRepository
import tj.dastras.core.api.AchievementApiResponse
import tj.dastras.core.api.BonusTransactionApiResponse
import tj.dastras.core.api.ErrorPresenter
import tj.dastras.core.api.LoyaltyLevelResponse
import tj.dastras.core.api.LoyaltySummaryResponse
import tj.dastras.core.api.friendlyErrorMessage
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

private const val TAG = "LoyaltyViewModel"
private const val PAGE_SIZE = 20

data class LoyaltyUiState(
    val profile: UserProfile? = null,
    val summary: LoyaltySummaryResponse? = null,
    val levels: List<LoyaltyLevelResponse> = emptyList(),
    val transactions: List<BonusTransactionApiResponse> = emptyList(),
    val transactionPage: Int = 1,
    val transactionTotal: Int = 0,
    val isLoadingMore: Boolean = false,
    val achievements: List<AchievementApiResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false,
) {
    val hasMoreTransactions: Boolean get() = transactions.size < transactionTotal
}

@HiltViewModel
class LoyaltyViewModel @Inject constructor(
    private val loyaltyRepository: LoyaltyRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    var uiState by mutableStateOf(LoyaltyUiState())
        private set

    init { load() }

    fun loadMoreTransactions() {
        if (!uiState.hasMoreTransactions || uiState.isLoadingMore || !authRepository.isLoggedIn) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingMore = true)
            try {
                val nextPage = uiState.transactionPage + 1
                val paged    = loyaltyRepository.getTransactions(page = nextPage, pageSize = PAGE_SIZE)
                uiState = uiState.copy(
                    transactions     = uiState.transactions + paged.items,
                    transactionPage  = nextPage,
                    transactionTotal = paged.totalCount,
                    isLoadingMore    = false,
                )
            } catch (e: Exception) {
                Log.e(TAG, "loadMoreTransactions: error", e)
                uiState = uiState.copy(isLoadingMore = false)
            }
        }
    }
    fun onScreenVisible() {
        load()
    }

    fun refreshTransactions() {
        if (uiState.isRefreshing || !authRepository.isLoggedIn) return

        viewModelScope.launch {
            uiState = uiState.copy(isRefreshing = true)

            try {
                val txPaged = loyaltyRepository.getTransactions(
                    page = 1,
                    pageSize = PAGE_SIZE
                )

                uiState = uiState.copy(
                    transactions = txPaged.items,
                    transactionPage = 1,
                    transactionTotal = txPaged.totalCount,
                    isRefreshing = false
                )
            } catch (e: Exception) {
                Log.e(TAG, "refreshTransactions", e)

                uiState = uiState.copy(
                    isRefreshing = false,
                    error = friendlyErrorMessage(e)
                )
            }
        }
    }
    fun load() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = uiState.summary == null, error = null)
            try {
                if (authRepository.isLoggedIn) {
                    val profile = userRepository.getOrCreate()
                    val summary = loyaltyRepository.getSummary()
                    val levels = loyaltyRepository.getLevels()
                    val txPaged = loyaltyRepository.getTransactions(page = 1, pageSize = PAGE_SIZE)
                    val achievements = try {
                        loyaltyRepository.getAchievements()
                    } catch (e: Exception) {
                        Log.w(TAG, "achievements endpoint not available, using mock", e)
                        mockAchievements()
                    }
                    uiState = uiState.copy(
                        profile          = profile,
                        summary          = summary,
                        levels           = levels,
                        transactions     = txPaged.items,
                        transactionPage  = 1,
                        transactionTotal = txPaged.totalCount,
                        achievements     = achievements,
                        isLoading        = false,
                    )
                } else {
                    val user = MockData.currentUser
                    uiState = uiState.copy(
                        profile      = user,
                        summary      = mockSummary(user),
                        levels       = MockData.loyaltyLevels.map { it.toResponse(isCurrent = it.name == user.level.name) },
                        transactions = MockData.bonusTransactions.map { it.toApiResponse() },
                        achievements = mockAchievements(),
                        isLoading    = false,
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "load: error", e)
                uiState = uiState.copy(isLoading = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }
}

private fun mockSummary(user: UserProfile): LoyaltySummaryResponse {
    val levels = MockData.loyaltyLevels
    val level = user.level
    val next = levels.getOrNull(levels.indexOf(level) + 1)
    val progress = if (next != null) {
        ((user.totalSpent - level.minPoints) / (next.minPoints - level.minPoints)).coerceIn(0.0, 1.0)
    } else 1.0
    val amountToNext = next?.let { (it.minPoints - user.totalSpent).coerceAtLeast(0.0) } ?: 0.0

    return LoyaltySummaryResponse(
        bonusBalance = user.bonusBalance,
        totalSpent = user.totalSpent,
        cardNumber = user.cardNumber,
        memberSince = user.memberSince,
        level = level.toResponse(isCurrent = true),
        nextLevel = next?.toResponse(isCurrent = false),
        progressToNextLevel = progress,
        amountToNextLevel = amountToNext,
    )
}

private fun LoyaltyLevel.toResponse(isCurrent: Boolean): LoyaltyLevelResponse = LoyaltyLevelResponse(
    name = name,
    minSpent = minPoints.toDouble(),
    maxSpent = if (maxPoints == Int.MAX_VALUE) null else maxPoints.toDouble(),
    cashbackPercent = cashbackPercent.toDouble(),
    color = color,
    benefits = benefits,
    isCurrent = isCurrent,
)

private fun BonusTransaction.toApiResponse(): BonusTransactionApiResponse = BonusTransactionApiResponse(
    id = id,
    description = description,
    amount = amount.toDouble(),
    isCredit = isCredit,
    orderId = orderId.ifEmpty { null },
    createdAt = date,
)

private fun mockAchievements(): List<AchievementApiResponse> = listOf(
    AchievementApiResponse(id = "first_purchase",  title = "Первая покупка",    description = "Совершите первый заказ",    emoji = "🛒", unlocked = false, bonusReward = 50),
    AchievementApiResponse(id = "ten_orders",      title = "10 покупок",        description = "Совершите 10 заказов",      emoji = "🔥", unlocked = false, bonusReward = 100),
    AchievementApiResponse(id = "vip_level",       title = "VIP-покупатель",    description = "Достигните уровня VIP",     emoji = "👑", unlocked = false, bonusReward = 200),
    AchievementApiResponse(id = "big_order",       title = "Большой заказ",     description = "Сделайте заказ на 500 TJS", emoji = "🎯", unlocked = false, bonusReward = 75),
)

private val localZone: ZoneId = ZoneId.systemDefault()

/** Formats an ISO-8601 UTC timestamp into a human-friendly "1 июня, 14:23" string in the device's local timezone. */
fun formatTransactionDate(raw: String): String = try {
    OffsetDateTime.parse(raw)
        .atZoneSameInstant(localZone)
        .format(DateTimeFormatter.ofPattern("d MMMM, HH:mm", Locale("ru")))
} catch (e: Exception) {
    raw
}

/** Formats an ISO-8601 UTC timestamp into a "Март 2023"-style month/year string in the device's local timezone. */
fun formatMemberSince(raw: String): String = try {
    OffsetDateTime.parse(raw)
        .atZoneSameInstant(localZone)
        .format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru")))
        .replaceFirstChar { it.uppercase() }
} catch (e: Exception) {
    raw
}
