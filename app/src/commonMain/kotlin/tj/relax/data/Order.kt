package tj.relax.data

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String = "",
    val userUid: String = "",
    val subtotal: Double = 0.0,
    val total: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val address: String? = null,
    val date: String = "",
    val items: List<CartItem> = emptyList(),
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
enum class OrderStatus(val color: Long) {
    PENDING    (0xFF6B7280L),
    CONFIRMED  (0xFF3B82F6L),
    PREPARING  (0xFF8B5CF6L),
    DELIVERING (0xFFF59E0BL),
    DELIVERED  (0xFF22C55EL),
    CANCELLED  (0xFFEF4444L),
}
