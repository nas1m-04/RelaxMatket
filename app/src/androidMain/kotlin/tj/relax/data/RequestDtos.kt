package tj.relax.data

data class AddToCartRequest(
    val productId: Int,
    val quantity: Int,
)

data class CreateOrderRequest(
    val deliveryType: String = "delivery",
    val address: String? = null,
    val timeSlot: String? = null,
    val paymentMethod: String = "cash",
    val comment: String? = null,
    val promoCode: String? = null,
    val useBonuses: Boolean = false,
    val branchId: Int? = null,
    val items: List<OrderItemRequest>,
)

data class OrderItemRequest(
    val productId: Int,
    val quantity: Int,
)

data class UpdateProfileRequest(
    val name: String? = null,
    val email: String? = null,
    val avatarUrl: String? = null,
    val preferredBranchId: Int? = null,
    val pushEnabled: Boolean? = null,
)
