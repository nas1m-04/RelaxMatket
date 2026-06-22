package tj.dastras.core.util

import java.text.SimpleDateFormat
import java.util.Locale

fun formatMemberSince(raw: String): String {
    if (raw.isBlank()) return ""
    return try {
        val input  = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
        val date   = input.parse(raw) ?: return raw
        val month  = SimpleDateFormat("LLLL", Locale("ru")).format(date)
            .replaceFirstChar { it.lowercase() }
        val year   = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
        "С нами с $month $year"
    } catch (e: Exception) {
        raw
    }
}