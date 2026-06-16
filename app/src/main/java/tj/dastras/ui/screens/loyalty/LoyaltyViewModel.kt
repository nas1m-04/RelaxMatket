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
import tj.dastras.data.remote.BonusTransactionApiResponse
import tj.dastras.data.remote.ErrorPresenter
import tj.dastras.data.remote.LoyaltyLevelResponse
import tj.dastras.data.remote.LoyaltySummaryResponse
import tj.dastras.data.remote.friendlyErrorMessage
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

private const val TAG = "LoyaltyViewModel"

data class LoyaltyUiState(
    val profile: UserProfile? = null,
    val summary: LoyaltySummaryResponse? = null,
    val levels: List<LoyaltyLevelResponse> = emptyList(),
    val transactions: List<BonusTransactionApiResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class LoyaltyViewModel @Inject constructor(
    private val loyaltyRepository: LoyaltyRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    var uiState by mutableStateOf(LoyaltyUiState())
        private set

    init { load() }

    fun load() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = uiState.summary == null, error = null)
            try {
                if (authRepository.isLoggedIn) {
                    val profile = userRepository.getOrCreate()
                    val summary = loyaltyRepository.getSummary()
                    val levels = loyaltyRepository.getLevels()
                    val transactions = loyaltyRepository.getTransactions(pageSize = 50).items
                    uiState = uiState.copy(
                        profile = profile,
                        summary = summary,
                        levels = levels,
                        transactions = transactions,
                        isLoading = false,
                    )
                } else {
                    val user = MockData.currentUser
                    uiState = uiState.copy(
                        profile = user,
                        summary = mockSummary(user),
                        levels = MockData.loyaltyLevels.map { it.toResponse(isCurrent = it.name == user.level.name) },
                        transactions = MockData.bonusTransactions.map { it.toApiResponse() },
                        isLoading = false,
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

/** Formats an ISO-8601 timestamp into a human-friendly "1 июня, 14:23" string. Falls back to the raw value if it isn't an ISO date. */
fun formatTransactionDate(raw: String): String = try {
    OffsetDateTime.parse(raw).format(DateTimeFormatter.ofPattern("d MMMM, HH:mm", Locale("ru")))
} catch (e: Exception) {
    raw
}

/** Formats an ISO-8601 timestamp into a "Март 2023"-style month/year string. Falls back to the raw value if it isn't an ISO date. */
fun formatMemberSince(raw: String): String = try {
    OffsetDateTime.parse(raw).format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru")))
        .replaceFirstChar { it.uppercase() }
} catch (e: Exception) {
    raw
}
