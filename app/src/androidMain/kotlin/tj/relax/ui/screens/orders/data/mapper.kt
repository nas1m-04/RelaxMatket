package tj.relax.ui.screens.orders.data

import tj.relax.data.CartItem
import tj.relax.data.Order
import tj.relax.data.OrderStatus
import tj.relax.data.Product
import tj.relax.ui.screens.orders.data.dto.response.OrderItemResponse
import tj.relax.ui.screens.orders.data.dto.response.OrderResponse

// POS purchases (Frontol) aren't matched to a catalog product — Frontol only reports a name,
// so we show it as a name-only line instead of dropping it from the receipt.
private fun OrderItemResponse.toCartItem(): CartItem? {
    val matched = product
    // `price` here is the amount actually charged per unit at order time (snapshotted server-side —
    // already resolved from CardPrice if one applied then). The nested `product` is today's live
    // catalog data, whose price/cardPrice may have changed since — overriding with the snapshot and
    // clearing cardPrice keeps a receipt showing what was really paid, not today's price.
    if (matched != null) return CartItem(product = matched.copy(price = price, cardPrice = null), quantity = quantity)
    val fallbackName = name?.takeIf { it.isNotBlank() } ?: return null
    return CartItem(product = Product(name = fallbackName, price = price, inStock = true), quantity = quantity)
}

fun OrderResponse.toDomain() = Order(
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
    items         = items.mapNotNull { it.toCartItem() },
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
    source        = source,
)
