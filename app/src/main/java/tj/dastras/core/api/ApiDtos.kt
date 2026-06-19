package tj.dastras.core.api

import com.google.gson.annotations.SerializedName
import tj.dastras.data.Product
import tj.dastras.data.UserProfile

// ── Auth ────────────────────────────────────────────────────────────────────

data class RegisterRequest(
    val phone: String,
    val password: String,
    val name: String,
)

data class LoginRequest(
    val phone: String,
    val password: String,
)

data class RefreshRequest(
    @SerializedName("refresh_token") val refreshToken: String,
)

data class AuthResponse(
    @SerializedName("access_token")  val accessToken: String = "",
    @SerializedName("refresh_token") val refreshToken: String = "",
    val user: UserProfile? = null,
)

data class CartItemResponse(
    val id: Int = 0,
    val userUid: String = "",
    val productId: Int = 0,
    val quantity: Int = 1,
    val product: Product? = null,
)

data class OrderApiResponse(
    val id: String = "",
    val userUid: String = "",
    val subtotal: Double = 0.0,
    val total: Double = 0.0,
    val status: String = "pending",
    val address: String? = null,
    val createdAt: String = "",
    val items: List<OrderItemApiResponse> = emptyList(),
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

data class OrderItemApiResponse(
    val id: Int = 0,
    val orderId: String = "",
    val productId: Int = 0,
    val quantity: Int = 1,
    val price: Double = 0.0,
    val product: Product? = null,
)

// ── Loyalty ─────────────────────────────────────────────────────────────────

data class LoyaltyLevelResponse(
    val name: String = "",
    val minSpent: Double = 0.0,
    val maxSpent: Double? = null,
    val cashbackPercent: Double = 0.0,
    val color: Long = 0xFFC0C0C0L,
    val benefits: List<String> = emptyList(),
    val isCurrent: Boolean = false,
)

data class LoyaltySummaryResponse(
    val bonusBalance: Double = 0.0,
    val totalSpent: Double = 0.0,
    val cardNumber: String? = null,
    val memberSince: String = "",
    val level: LoyaltyLevelResponse = LoyaltyLevelResponse(),
    val nextLevel: LoyaltyLevelResponse? = null,
    val progressToNextLevel: Double = 0.0,
    val amountToNextLevel: Double = 0.0,
    val bonusToCurrencyRate: Double = 1.0,
    val maxBonusPaymentPercent: Double = 50.0,
)

data class BonusTransactionApiResponse(
    val id: Int = 0,
    val description: String = "",
    val amount: Double = 0.0,
    val isCredit: Boolean = true,
    val orderId: String? = null,
    val createdAt: String = "",
)

data class AchievementApiResponse(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val emoji: String = "🏆",
    val unlocked: Boolean = false,
    val unlockedAt: String? = null,
    val bonusReward: Int = 0,
)
