package tj.relax.data

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int = 0,
    val name: String = "",
    val brand: String? = null,
    val imageUrl: String? = null,
    val images: List<String> = emptyList(),
    val price: Double = 0.0,
    val oldPrice: Double? = null,
    /** Discounted price for card holders (every logged-in user). Null = no card discount. */
    val cardPrice: Double? = null,
    val unit: String? = null,
    val weight: String? = null,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val categoryId: Int = 0,
    val isNew: Boolean = false,
    val description: String? = null,
    val composition: String? = null,
    val inStock: Boolean = true,
    val isFavorite: Boolean = false,
) {
    /** Price to actually charge/display as the main price — card price when this product has one. */
    val effectivePrice: Double get() = cardPrice?.takeIf { it > 0 && it < price } ?: price
    val hasCardDiscount: Boolean get() = cardPrice != null && cardPrice > 0 && cardPrice < price
}
