package tj.dastras.data

import tj.dastras.data.remote.OrderApiResponse
import tj.dastras.data.remote.RelaxApiService
import tj.dastras.data.remote.dataOrThrow
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
    total         = total,
    status        = runCatching { OrderStatus.valueOf(status) }.getOrDefault(OrderStatus.PROCESSING),
    address       = address,
    date          = createdAt,
    items         = items.mapNotNull { item ->
        item.product?.let { CartItem(product = it, quantity = item.quantity) }
    },
    discount      = discount,
    bonusesUsed   = bonusesUsed,
    bonusEarned   = bonusEarned,
    bonusBalance  = bonusBalance,
    deliveryType  = deliveryType,
    timeSlot      = timeSlot,
    paymentMethod = paymentMethod,
    comment       = comment,
    promoCode     = promoCode,
)
