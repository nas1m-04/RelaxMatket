package tj.relax.ui.screens.notifications.data

import tj.relax.data.Notification
import tj.relax.data.NotificationType
import tj.relax.ui.screens.notifications.data.dto.response.NotificationResponse
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

fun NotificationResponse.toDomain() = Notification(
    id          = id,
    title       = title,
    body        = body,
    time        = formatNotificationTime(createdAt),
    type        = runCatching { NotificationType.valueOf(type) }.getOrDefault(NotificationType.SYSTEM),
    isRead      = isRead,
    imageUrl    = imageUrl,
    displayMode = displayMode,
)

private fun formatNotificationTime(isoString: String): String {
    if (isoString.isBlank()) return ""
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val date = sdf.parse(isoString.take(19)) ?: return isoString
        val now = Calendar.getInstance()
        val cal = Calendar.getInstance().apply { time = date }
        val isToday = now.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)
        val isYesterday = run {
            val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            yesterday.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                    yesterday.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)
        }
        val timePart = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
        when {
            isToday -> "Сегодня, $timePart"
            isYesterday -> "Вчера, $timePart"
            else -> SimpleDateFormat("d MMMM", Locale("ru")).format(date)
        }
    } catch (e: Exception) {
        isoString
    }
}
