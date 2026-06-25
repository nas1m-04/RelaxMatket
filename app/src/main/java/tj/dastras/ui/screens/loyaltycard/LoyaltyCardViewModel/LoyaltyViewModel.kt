package tj.dastras.ui.screens.loyaltycard.LoyaltyCardViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tj.dastras.core.api.ErrorPresenter
import tj.dastras.data.LocalUserStore
import tj.dastras.data.LoyaltyRepository
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LoyaltyViewModel @Inject constructor(
    private val loyaltyRepository: LoyaltyRepository,
    private val localUserStore: LocalUserStore,
) : ViewModel() {

    val userName: String get() = localUserStore.get()?.name ?: "Гость"

    var uiState by mutableStateOf(LoyaltyCardState())
        private set

    init {
        viewModelScope.launch {
            try {
                load()
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false)
                ErrorPresenter.report(e)
            }
        }
    }

    private suspend fun fetchLoyaltyData(forceRefresh: Boolean) {
        val summary = loyaltyRepository.getSummary(forceRefresh)
        val levels = loyaltyRepository.getLevels(forceRefresh)
        val achievements = loyaltyRepository.getAchievements(forceRefresh)

        uiState = uiState.copy(
            summary      = summary,
            levels       = levels,
            achievements = achievements,
            isLoading    = false,
            lastUpdated  = System.currentTimeMillis(),
        )
    }

    private suspend fun load() {
        if (uiState.summary == null) {
            uiState = uiState.copy(isLoading = true)
        }
        fetchLoyaltyData(forceRefresh = false)
    }

    suspend fun refresh() {
        fetchLoyaltyData(forceRefresh = true)
    }

    var qrState by mutableStateOf<QrState>(QrState.Loading)
        private set

    private var qrJob: Job? = null

    fun startQrRefresh() {
        qrJob?.cancel()
        qrJob = viewModelScope.launch {
            while (true) {
                try {
                    qrState = QrState.Loading
                    val response  = loyaltyRepository.getQrToken()
                    val expiresAt = java.time.Instant.parse(response.expiresAt)
                    qrState       = QrState.Ready(response.qrToken, expiresAt)

                    // Ждём до истечения минус 5 сек запас
                    val refreshIn = java.time.Instant.now()
                        .until(expiresAt, java.time.temporal.ChronoUnit.MILLIS) - 5_000L
                    if (refreshIn > 0) delay(refreshIn)

                } catch (e: Exception) {
                    qrState = QrState.Error
                    ErrorPresenter.report(e)
                    delay(15_000L)
                }
            }
        }
    }

    fun stopQrRefresh() {
        qrJob?.cancel()
    }
}

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