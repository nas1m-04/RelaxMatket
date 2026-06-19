package tj.dastras.data

import tj.dastras.core.api.OrderApiResponse
import tj.dastras.core.api.RelaxApiService
import tj.dastras.core.api.dataOrThrow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    suspend fun getOrders(): List<Order> =
        api.getOrders().dataOrThrow().map { it.toOrder() }

    suspend fun createOrder(request: CreateOrderRequest): Order =
        api.createOrder(request).dataOrThrow().toOrder()

    suspend fun getById(id: String): Order =
        api.getOrder(id).dataOrThrow().toOrder()
}

private fun OrderApiResponse.toOrder() = Order(
    id            = id,
    userUid       = userUid,
    subtotal      = if (subtotal > 0) subtotal else total,
    total         = total,
    status        = when (status.lowercase()) {
        "pending"    -> OrderStatus.PENDING
        "confirmed"  -> OrderStatus.CONFIRMED
        "preparing"  -> OrderStatus.PREPARING
        "delivering" -> OrderStatus.DELIVERING
        "delivered"  -> OrderStatus.DELIVERED
        "cancelled"  -> OrderStatus.CANCELLED
        else         -> OrderStatus.PENDING
    },
    address       = address,
    date          = createdAt,
    items         = items.mapNotNull { item ->
        item.product?.let { CartItem(product = it, quantity = item.quantity) }
    },
    discount      = discount,
    bonusesUsed   = bonusesUsed,
    bonusEarned   = bonusEarned,
    bonusBalance  = bonusBalance,
    bonusSettled  = bonusSettled,
    deliveryType  = deliveryType,
    timeSlot      = timeSlot,
    paymentMethod = paymentMethod,
    comment       = comment,
    promoCode     = promoCode,
)
