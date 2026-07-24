package tj.relax.ui.screens.orders.data.dto.response

import kotlinx.serialization.Serializable
import tj.relax.data.Product

@Serializable
data class OrderResponse(
    val id: String = "",
    val userUid: String = "",
    val subtotal: Double = 0.0,
    val total: Double = 0.0,
    val status: String = "pending",
    val address: String? = null,
    val createdAt: String = "",
    val items: List<OrderItemResponse> = emptyList(),
    val discount: Double = 0.0,
    val bonusesUsed: Double = 0.0,
    val bonusEarned: Double = 0.0,
    val bonusBalance: Double = 0.0,
    val bonusSettled: Boolean = false,
    val deliveryType: String = "delivery",
    val timeSlot: String? = null,
    val paymentMethod: String = "cash",
    val comment: String? = null,
    val promoCode: String? = null,
    val source: String = "app",
)

@Serializable
data class OrderItemResponse(
    val id: Int = 0,
    val orderId: String = "",
    val productId: Int? = null,
    val quantity: Int = 1,
    val price: Double = 0.0,
    val product: Product? = null,
    /** Set instead of [product] for POS-sourced items with no matching catalog product — Frontol
     * only knows the item's name, not our internal product id. */
    val name: String? = null,
)
