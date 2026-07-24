package tj.relax.ui.screens.support

import tj.relax.data.SupportTicket

data class SupportUiState(
    val tickets: List<SupportTicket> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isSubmitting: Boolean = false,
    val page: Int = 1,
    val totalPages: Int = 0,
    val error: String? = null,
) {
    val hasMore: Boolean get() = tickets.isNotEmpty() && page < totalPages
}
