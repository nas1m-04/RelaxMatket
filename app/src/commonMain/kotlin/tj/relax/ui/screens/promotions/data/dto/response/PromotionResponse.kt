package tj.relax.ui.screens.promotions.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PromotionResponse(
    val id: Int = 0,
    val title: String = "",
    val subtitle: String? = null,
    val discount: String? = null,
    val imageUrl: String? = null,
    val endDate: String? = null,
    val backgroundColor: String? = null,
    val isActive: Boolean = true,
    val sortOrder: Int = 0,
)
