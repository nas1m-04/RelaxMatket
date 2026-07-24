package tj.relax.core.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val englishMonthsNominative = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
)
private val russianMonthsNominative = listOf(
    "январь", "февраль", "март", "апрель", "май", "июнь",
    "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь",
)
private val russianMonthsGenitive = listOf(
    "января", "февраля", "марта", "апреля", "мая", "июня",
    "июля", "августа", "сентября", "октября", "ноября", "декабря",
)

internal fun russianMonthGenitive(monthNumber: Int): String = russianMonthsGenitive[monthNumber - 1]
internal fun russianMonthNominative(monthNumber: Int): String = russianMonthsNominative[monthNumber - 1]
internal fun Int.twoDigits(): String = if (this < 10) "0$this" else "$this"

/** Parses an ISO-8601 instant (with a numeric offset or "Z") and converts it to the device's local time. */
internal fun parseToLocalDateTime(raw: String): LocalDateTime =
    Instant.parse(raw).toLocalDateTime(TimeZone.currentSystemDefault())

/** "February 2024" (backend format, English nominative month) -> "С нами с февраль 2024".
 * Currently unused by any screen (superseded by the ISO-datetime-based formatMemberSince in
 * LoyaltyViewModel.kt) but kept working in case something starts calling it again. */
fun formatMemberSince(raw: String): String {
    if (raw.isBlank()) return ""
    val parts = raw.trim().split(" ")
    if (parts.size != 2) return raw
    val monthIndex = englishMonthsNominative.indexOfFirst { it.equals(parts[0], ignoreCase = true) }
    if (monthIndex == -1) return raw
    return "С нами с ${russianMonthsNominative[monthIndex]} ${parts[1]}"
}
