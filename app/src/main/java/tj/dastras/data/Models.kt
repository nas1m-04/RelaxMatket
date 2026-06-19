package tj.dastras.data

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int = 0,
    val name: String = "",
    val brand: String? = null,
    val imageUrl: String? = null,
    val images: List<String> = emptyList(),
    val price: Double = 0.0,
    val oldPrice: Double? = null,
    val unit: String? = null,
    val weight: String? = null,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val categoryId: Int = 0,
    val isNew: Boolean = false,
    val description: String? = null,
    val composition: String? = null,
    val inStock: Boolean = true,
    val isFavorite: Boolean  = false,
)

data class Category(
    val id: Int = 0,
    val name: String = "",
    val icon: String? = null,
    val color: String? = null,
    val productCount: Int = 0,
)

data class Banner(
    val id: Int = 0,
    val title: String = "",
    val subtitle: String? = null,
    val imageUrl: String? = null,
    val backgroundColor: String? = null,
    val badgeText: String? = null,
)

data class CartItem(
    val product: Product,
    val quantity: Int,
)

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
)

enum class OrderStatus(val color: Long) {
    PENDING    (0xFF6B7280L),
    CONFIRMED  (0xFF3B82F6L),
    PREPARING  (0xFF8B5CF6L),
    DELIVERING (0xFFF59E0BL),
    DELIVERED  (0xFF22C55EL),
    CANCELLED  (0xFFEF4444L),
}

data class Promotion(
    val id: Int = 0,
    val title: String = "",
    val subtitle: String = "",
    val discount: String = "",
    @SerializedName("image_url")        val imageUrl: String? = null,
    @SerializedName("end_date")         val endDate: String = "",
    @SerializedName("background_color") val backgroundColor: Long = 0L,
)

data class BonusTransaction(
    val id: Int = 0,
    val description: String = "",
    val amount: Int = 0,
    @SerializedName("is_credit") val isCredit: Boolean = true,
    val date: String = "",
    @SerializedName("order_id") val orderId: String = "",
)

data class LoyaltyLevel(
    val name: String,
    val minPoints: Int,
    val maxPoints: Int,
    val cashbackPercent: Float,
    val color: Long,
    val benefits: List<String>,
)

data class Notification(
    val id: Int = 0,
    val title: String = "",
    val body: String = "",
    val time: String = "",
    val type: NotificationType = NotificationType.SYSTEM,
    @SerializedName("is_read") val isRead: Boolean = false,
)

enum class NotificationType { PROMO, BONUS, ORDER, SYSTEM }

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val cardNumber: String = "",
    val bonusBalance: Double = 0.0,
    val totalSpent: Double = 0.0,
    val memberSince: String = "",
    val favoriteIds: List<Int> = emptyList(),
    val preferredBranchId: Int? = null,
    @kotlin.jvm.Transient val level: LoyaltyLevel = LoyaltyLevel(
        "Старт", 0, 4999, 1f, 0xFFC0C0C0L, listOf("1% кэшбэк бонусами")
    ),
)

data class Branch(
    val id: Int = 0,
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val phone: String? = null,
    val isActive: Boolean = true,
)

// ── Request DTOs ──────────────────────────────────────────────────────────────

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
)
