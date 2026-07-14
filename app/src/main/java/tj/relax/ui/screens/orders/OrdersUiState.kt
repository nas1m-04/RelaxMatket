package tj.relax.ui.screens.orders

import tj.relax.data.Order

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)
