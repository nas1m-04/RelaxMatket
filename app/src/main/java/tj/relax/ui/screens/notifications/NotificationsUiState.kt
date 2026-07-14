package tj.relax.ui.screens.notifications

import tj.relax.data.Notification

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)
