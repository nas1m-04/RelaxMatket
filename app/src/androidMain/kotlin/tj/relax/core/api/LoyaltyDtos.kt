package tj.relax.core.api

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

data class QrTokenResponse(
    val qrToken: String,
    val expiresAt: String,
)
