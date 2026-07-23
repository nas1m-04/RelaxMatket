package tj.relax.data

data class Notification(
    val id: Int = 0,
    val title: String = "",
    val body: String = "",
    val time: String = "",
    val type: NotificationType = NotificationType.SYSTEM,
    val isRead: Boolean = false,
    val imageUrl: String? = null,
    val displayMode: String = "INBOX",
)

enum class NotificationType { PROMO, BONUS, ORDER, SYSTEM, NEWS }
