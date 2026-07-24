package tj.relax.ui.screens.promotions.data

import tj.relax.data.Promotion
import tj.relax.ui.screens.promotions.data.dto.response.PromotionResponse

fun PromotionResponse.toDomain() = Promotion(
    id = id,
    title = title,
    subtitle = subtitle ?: "",
    discount = discount ?: "",
    imageUrl = imageUrl,
    endDate = endDate ?: "",
    backgroundColor = parseHexColor(backgroundColor),
)

private fun parseHexColor(hex: String?): Long {
    if (hex == null) return 0xFF028AFCL
    val clean = hex.removePrefix("#")
    return try {
        when (clean.length) {
            6 -> 0xFF000000L or clean.toLong(16)
            8 -> clean.toLong(16)
            else -> 0xFF028AFCL
        }
    } catch (_: NumberFormatException) {
        0xFF028AFCL
    }
}
