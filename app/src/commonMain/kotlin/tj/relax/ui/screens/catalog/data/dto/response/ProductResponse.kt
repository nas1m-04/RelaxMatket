package tj.relax.ui.screens.catalog.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val id: Int,
    val name: String,
    val price: Double,
    val oldPrice: Double?,
    val imageUrl: String,
    val categoryId: Int,
    val rating: Float,
    val isNew: Boolean,
    val isOnSale: Boolean,
)