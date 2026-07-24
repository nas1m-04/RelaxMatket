package tj.relax.ui.screens.notifications.data

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import tj.relax.core.util.parseToLocalDateTime
import tj.relax.core.util.russianMonthGenitive
import tj.relax.core.util.twoDigits
import tj.relax.data.Notification
import tj.relax.data.NotificationType
import tj.relax.ui.screens.notifications.data.dto.response.NotificationResponse

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

// The backend sends a naive "yyyy-MM-dd'T'HH:mm:ss" timestamp with no zone marker, which is
// always UTC — appending "Z" makes it a valid ISO instant, then parseToLocalDateTime converts
// it to the device's local time for display, matching what the old SimpleDateFormat-based
// version did (parse as UTC, format in the device's default zone).
private fun formatNotificationTime(isoString: String): String {
    if (isoString.isBlank()) return ""
    return try {
        val dateTime = parseToLocalDateTime("${isoString.take(19)}Z")
        val nowDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val timePart = "${dateTime.hour.twoDigits()}:${dateTime.minute.twoDigits()}"
        when (dateTime.date) {
            nowDateTime.date -> "Сегодня, $timePart"
            nowDateTime.date.minus(1, DateTimeUnit.DAY) -> "Вчера, $timePart"
            else -> "${dateTime.dayOfMonth} ${russianMonthGenitive(dateTime.monthNumber)}"
        }
    } catch (e: Exception) {
        isoString
    }
}
