package tj.relax.data

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
