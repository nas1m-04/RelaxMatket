package tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tj.relax.core.api.ErrorPresenter
import tj.relax.core.events.LoyaltyPushEvents
import tj.relax.data.LocalUserStore
import tj.relax.data.LoyaltyRepository
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// Credits and debits share one paginated feed and are split into tabs client-side, so a small
// page size can make one tab look empty while the other hogs the whole page — keep this generous.
private const val TX_PAGE_SIZE = 15

class LoyaltyViewModel(
    private val loyaltyRepository: LoyaltyRepository,
    private val localUserStore: LocalUserStore,
) : ViewModel() {

    val userName: String get() = localUserStore.get()?.name ?: "Гость"

    var uiState by mutableStateOf(LoyaltyCardState())
        private set

    // fetchFromNetwork() (initial load/refresh, replaces the tx list) and
    // loadMoreTransactions() (appends a page) both mutate uiState.transactions — only one
    // may be in flight at a time, otherwise a stale loadMore response can land after a
    // refresh already replaced the list, appending overlapping transaction ids and
    // crashing the list with a duplicate key.
    private var txJob: Job? = null

    init {
        // Show cached data instantly — no spinner at all if we have local data
        val cachedSummary = loyaltyRepository.getCachedSummary()
        if (cachedSummary != null) {
            uiState = uiState.copy(
                summary      = cachedSummary,
                levels       = loyaltyRepository.getCachedLevels() ?: emptyList(),
                achievements = loyaltyRepository.getCachedAchievements() ?: emptyList(),
                isLoading    = false,
            )
        }
        // Refresh from network in background (silently if we already have data)
        viewModelScope.launch {
            try {
                fetchFromNetwork(showSpinner = cachedSummary == null)
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false)
                ErrorPresenter.report(e)
            }
        }

        // A push arrives once the cashier has used the currently-displayed QR — only worth acting
        // on if the QR screen is actually open right now (qrJob active); otherwise the next time it
        // opens will fetch a fresh one anyway.
        viewModelScope.launch {
            LoyaltyPushEvents.qrConsumed.collect {
                if (qrJob?.isActive == true) startQrRefresh()
            }
        }
    }

    private suspend fun fetchFromNetwork(showSpinner: Boolean = false) {
        txJob?.cancel()
        if (showSpinner) uiState = uiState.copy(isLoading = true)
        coroutineScope {
            val summaryD = async { loyaltyRepository.getSummary() }
            val levelsD  = async { loyaltyRepository.getLevels() }
            val achievD  = async { loyaltyRepository.getAchievements() }
            val txD      = async { loyaltyRepository.getTransactions(1, TX_PAGE_SIZE) }

            val txPage = txD.await()
            uiState = uiState.copy(
                summary      = summaryD.await(),
                levels       = levelsD.await(),
                achievements = achievD.await(),
                transactions = txPage.items,
                txPage       = 1,
                txTotal      = txPage.totalCount,
                txTotalPages = txPage.totalPages,
                isLoading    = false,
                isRefreshing = false,
                lastUpdated  = System.currentTimeMillis(),
            )
        }
    }

    suspend fun refresh() {
        uiState = uiState.copy(isRefreshing = true)
        try {
            fetchFromNetwork(showSpinner = false)
        } catch (e: Exception) {
            uiState = uiState.copy(isRefreshing = false)
            ErrorPresenter.report(e)
        }
    }

    fun loadMoreTransactions() {
        if (uiState.isLoadingMoreTx || !uiState.hasMoreTx) return
        if (uiState.isLoading || uiState.isRefreshing) return
        txJob = viewModelScope.launch {
            uiState = uiState.copy(isLoadingMoreTx = true)
            try {
                val next = uiState.txPage + 1
                val page = loyaltyRepository.getTransactions(next, TX_PAGE_SIZE)
                uiState = uiState.copy(
                    transactions    = uiState.transactions + page.items,
                    txPage          = next,
                    txTotal         = page.totalCount,
                    txTotalPages    = page.totalPages,
                    isLoadingMoreTx = false,
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isLoadingMoreTx = false)
                ErrorPresenter.report(e)
            }
        }
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

fun formatTransactionDate(raw: String): String = try {
    OffsetDateTime.parse(raw)
        .atZoneSameInstant(localZone)
        .format(DateTimeFormatter.ofPattern("d MMMM, HH:mm", Locale("ru")))
} catch (e: Exception) {
    raw
}

// Day + time only, no month name — for lists already grouped under a month header, where
// repeating the month on every row reads as the same date shown twice.
fun formatDayTime(raw: String): String = try {
    OffsetDateTime.parse(raw)
        .atZoneSameInstant(localZone)
        .format(DateTimeFormatter.ofPattern("d, HH:mm", Locale("ru")))
} catch (e: Exception) {
    raw
}

// Time only — for rows already grouped under a full-date header, where the date itself
// would just repeat what the header already says.
fun formatTimeOnly(raw: String): String = try {
    OffsetDateTime.parse(raw)
        .atZoneSameInstant(localZone)
        .format(DateTimeFormatter.ofPattern("HH:mm", Locale("ru")))
} catch (e: Exception) {
    raw
}

// Full date — "14 февраля 2026" — used as a group-section header.
fun formatFullDate(raw: String): String = try {
    OffsetDateTime.parse(raw)
        .atZoneSameInstant(localZone)
        .format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru")))
} catch (e: Exception) {
    raw
}

fun formatMemberSince(raw: String): String = try {
    OffsetDateTime.parse(raw)
        .atZoneSameInstant(localZone)
        .format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru")))
        .replaceFirstChar { it.uppercase() }
} catch (e: Exception) {
    raw
}
