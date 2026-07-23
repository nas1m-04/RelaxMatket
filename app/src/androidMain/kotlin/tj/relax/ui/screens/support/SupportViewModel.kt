package tj.relax.ui.screens.support

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tj.relax.core.api.ErrorPresenter
import tj.relax.core.api.friendlyErrorMessage
import tj.relax.data.SupportRepository

private const val TAG = "SupportViewModel"
private const val PAGE_SIZE = 20

class SupportViewModel(
    private val supportRepository: SupportRepository,
) : ViewModel() {

    var uiState by mutableStateOf(SupportUiState())
        private set

    private var job: Job? = null

    init { load() }

    fun load() {
        job?.cancel()
        job = viewModelScope.launch {
            uiState = uiState.copy(isLoading = uiState.tickets.isEmpty(), error = null)
            try {
                val result = supportRepository.getTickets(page = 1, pageSize = PAGE_SIZE)
                uiState = uiState.copy(tickets = result.items, page = 1, totalPages = result.totalPages, isLoading = false)
            } catch (e: Exception) {
                Log.e(TAG, "load: error", e)
                uiState = uiState.copy(isLoading = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }

    suspend fun refresh() {
        job?.cancel()
        uiState = uiState.copy(isRefreshing = true, error = null)
        try {
            val result = supportRepository.getTickets(page = 1, pageSize = PAGE_SIZE)
            uiState = uiState.copy(tickets = result.items, page = 1, totalPages = result.totalPages, isRefreshing = false)
        } catch (e: Exception) {
            Log.e(TAG, "refresh: error", e)
            uiState = uiState.copy(isRefreshing = false)
            ErrorPresenter.report(e)
        }
    }

    fun loadMore() {
        if (uiState.isLoadingMore || !uiState.hasMore) return
        if (uiState.isLoading || uiState.isRefreshing) return

        job = viewModelScope.launch {
            uiState = uiState.copy(isLoadingMore = true)
            try {
                val next = uiState.page + 1
                val result = supportRepository.getTickets(page = next, pageSize = PAGE_SIZE)
                uiState = uiState.copy(
                    tickets = uiState.tickets + result.items,
                    page = next,
                    totalPages = result.totalPages,
                    isLoadingMore = false,
                )
            } catch (e: Exception) {
                Log.e(TAG, "loadMore: error", e)
                uiState = uiState.copy(isLoadingMore = false)
                ErrorPresenter.report(e)
            }
        }
    }

    fun submitTicket(message: String, onSuccess: () -> Unit) {
        if (message.isBlank() || uiState.isSubmitting) return
        viewModelScope.launch {
            uiState = uiState.copy(isSubmitting = true)
            try {
                supportRepository.createTicket(message.trim())
                uiState = uiState.copy(isSubmitting = false)
                onSuccess()
                load()
            } catch (e: Exception) {
                Log.e(TAG, "submitTicket: error", e)
                uiState = uiState.copy(isSubmitting = false)
                ErrorPresenter.report(e)
            }
        }
    }
}
