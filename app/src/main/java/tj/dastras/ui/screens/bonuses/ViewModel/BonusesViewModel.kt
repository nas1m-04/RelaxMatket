package tj.dastras.ui.screens.bonuses.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.dastras.core.api.ErrorPresenter
import tj.dastras.core.api.friendlyErrorMessage
import tj.dastras.data.AuthRepository
import tj.dastras.data.LoyaltyRepository
import javax.inject.Inject

private const val PAGE_SIZE = 4

@HiltViewModel
class BonusesViewModel @Inject constructor(
    private val loyaltyRepository: LoyaltyRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    var uiState by mutableStateOf(BonusesState())
        private set

    init {
        loadMoreData()
        loadTransactions()
    }

    fun loadMoreData() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val summary = loyaltyRepository.getSummary()
                val levels = loyaltyRepository.getLevels()
                val achievements = loyaltyRepository.getAchievements()
                uiState = uiState.copy(
                    summary = summary,
                    levels = levels,
                    achievements = achievements,
                    isLoading = false,
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }

    fun loadTransactions() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            val page = loyaltyRepository.getTransactions(
                page = 1,
                pageSize = PAGE_SIZE
            )

            uiState = uiState.copy(
                transactions = page.items,
                total = page.totalCount,
                totalPages = page.totalPages,
                page = 1,
                isLoading = false
            )
        }
    }

    fun refresh() {
        loadMoreData()
        loadTransactions()
    }

    fun loadMore() {
        if (uiState.isLoadingMore || !uiState.hasMore) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoadingMore = true)

            val next = uiState.page + 1

            val page = loyaltyRepository.getTransactions(
                page = next,
                pageSize = PAGE_SIZE
            )

            uiState = uiState.copy(
                transactions = uiState.transactions + page.items,
                total = page.totalCount,
                totalPages = page.totalPages,
                page = next,
                isLoadingMore = false
            )
        }
    }
}