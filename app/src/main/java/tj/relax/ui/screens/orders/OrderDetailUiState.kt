package tj.relax.ui.screens.orders

import tj.relax.data.Order

data class OrderDetailUiState(
    val order: Order? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)
