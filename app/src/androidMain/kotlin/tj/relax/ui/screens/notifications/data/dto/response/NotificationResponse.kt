package tj.relax.ui.screens.notifications.data.dto.response

data class NotificationResponse(
    val id: Int = 0,
    val title: String = "",
    val body: String = "",
    val type: String = "SYSTEM",
    val isRead: Boolean = false,
    val createdAt: String = "",
    val imageUrl: String? = null,
    val displayMode: String = "INBOX",
)

data class UnreadCountResponse(
    val count: Int = 0,
)
