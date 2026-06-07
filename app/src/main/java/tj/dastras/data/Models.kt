package tj.dastras.data

data class Product(
    val id: Int,
    val name: String,
    val brand: String = "",
    val imageUrl: String,
    val price: Double,
    val oldPrice: Double? = null,
    val unit: String = "шт",
    val weight: String = "",
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val categoryId: Int,
    val isNew: Boolean = false,
    val isFavorite: Boolean = false,
    val description: String = "",
    val composition: String = "",
    val inStock: Boolean = true,
    val cartCount: Int = 0,
)

data class Category(
    val id: Int,
    val name: String,
    val icon: String,
    val color: Long,
    val productCount: Int = 0,
)

data class Banner(
    val id: Int,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val backgroundColor: Long,
    val badgeText: String = "",
)

data class CartItem(
    val product: Product,
    val quantity: Int,
)

data class Order(
    val id: String,
    val date: String,
    val items: List<CartItem>,
    val total: Double,
    val status: OrderStatus,
    val address: String = "",
)

enum class OrderStatus(val label: String, val color: Long) {
    DELIVERED("Доставлен", 0xFF22C55E),
    IN_PROGRESS("В пути", 0xFFF59E0B),
    PROCESSING("Обрабатывается", 0xFF3B82F6),
    CANCELLED("Отменён", 0xFFEF4444),
}

data class Promotion(
    val id: Int,
    val title: String,
    val subtitle: String,
    val discount: String,
    val imageUrl: String,
    val endDate: String,
    val backgroundColor: Long,
)

data class BonusTransaction(
    val id: Int,
    val description: String,
    val amount: Int,
    val isCredit: Boolean,
    val date: String,
    val orderId: String = "",
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
    val id: Int,
    val title: String,
    val body: String,
    val time: String,
    val type: NotificationType,
    val isRead: Boolean = false,
)

enum class NotificationType { PROMO, BONUS, ORDER, SYSTEM }

data class UserProfile(
    val name: String,
    val phone: String,
    val email: String,
    val avatarUrl: String,
    val cardNumber: String,
    val bonusBalance: Int,
    val totalSpent: Double,
    val level: LoyaltyLevel,
    val memberSince: String,
)
