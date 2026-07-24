package tj.relax.ui.screens.orders

import io.github.aakira.napier.Napier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tj.relax.data.AuthRepository
import tj.relax.data.MockData
import tj.relax.data.OrderRepository
import tj.relax.core.api.ErrorPresenter
import tj.relax.core.api.friendlyErrorMessage

private const val TAG = "OrdersViewModel"
private const val PAGE_SIZE = 20

class OrdersViewModel(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    var uiState by mutableStateOf(OrdersUiState())
        private set

    // load() (initial/refresh, replaces the list) and loadMore() (appends a page) both mutate
    // uiState.orders — only one may be in flight at a time, otherwise a stale loadMore response
    // can land after a refresh already replaced the list, appending overlapping order ids and
    // crashing the list with a duplicate key (same reasoning as LoyaltyViewModel's txJob).
    private var job: Job? = null

    init { load() }

    fun load(forceRefresh: Boolean = false) {
        job?.cancel()
        job = viewModelScope.launch {
            uiState = uiState.copy(isLoading = uiState.orders.isEmpty() && !forceRefresh, error = null)
            try {
                if (authRepository.isLoggedIn) {
                    val result = orderRepository.getOrders(page = 1, pageSize = PAGE_SIZE)
                    uiState = uiState.copy(
                        orders = result.items,
                        page = 1,
                        totalPages = result.totalPages,
                        totalCount = result.totalCount,
                        isLoading = false,
                    )
                } else {
                    uiState = uiState.copy(orders = MockData.orders, page = 1, totalPages = 1, totalCount = MockData.orders.size, isLoading = false)
                }
            } catch (e: Exception) {
                Napier.e("load: error", e, tag = TAG)
                uiState = uiState.copy(isLoading = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }

    suspend fun refresh() {
        job?.cancel()
        uiState = uiState.copy(isRefreshing = true, error = null)
        try {
            if (authRepository.isLoggedIn) {
                val result = orderRepository.getOrders(page = 1, pageSize = PAGE_SIZE)
                uiState = uiState.copy(orders = result.items, page = 1, totalPages = result.totalPages, totalCount = result.totalCount, isRefreshing = false)
            } else {
                uiState = uiState.copy(orders = MockData.orders, page = 1, totalPages = 1, totalCount = MockData.orders.size, isRefreshing = false)
            }
        } catch (e: Exception) {
            Napier.e("refresh: error", e, tag = TAG)
            uiState = uiState.copy(isRefreshing = false)
            ErrorPresenter.report(e)
        }
    }

    fun loadMore() {
        if (!authRepository.isLoggedIn) return
        if (uiState.isLoadingMore || !uiState.hasMore) return
        if (uiState.isLoading || uiState.isRefreshing) return

        job = viewModelScope.launch {
            uiState = uiState.copy(isLoadingMore = true)
            try {
                val next = uiState.page + 1
                val result = orderRepository.getOrders(page = next, pageSize = PAGE_SIZE)
                uiState = uiState.copy(
                    orders = uiState.orders + result.items,
                    page = next,
                    totalPages = result.totalPages,
                    totalCount = result.totalCount,
                    isLoadingMore = false,
                )
            } catch (e: Exception) {
                Napier.e("loadMore: error", e, tag = TAG)
                uiState = uiState.copy(isLoadingMore = false)
                ErrorPresenter.report(e)
            }
        }
    }
}
