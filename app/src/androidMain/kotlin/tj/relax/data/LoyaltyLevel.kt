package tj.relax.data

data class LoyaltyLevel(
    val name: String,
    val minPoints: Int,
    val maxPoints: Int,
    val cashbackPercent: Float,
    val color: Long,
    val benefits: List<String>,
)
