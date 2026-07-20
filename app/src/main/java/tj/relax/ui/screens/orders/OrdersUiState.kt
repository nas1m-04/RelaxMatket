package tj.relax.ui.screens.orders

import tj.relax.data.Order

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val page: Int = 1,
    val totalPages: Int = 0,
    val totalCount: Int = 0,
    val error: String? = null,
) {
    val hasMore: Boolean get() = orders.isNotEmpty() && page < totalPages
}
