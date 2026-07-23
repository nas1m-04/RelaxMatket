package tj.relax.data

import kotlinx.serialization.Serializable

@Serializable
data class LoyaltyLevel(
    val name: String,
    val minPoints: Int,
    val maxPoints: Int,
    val cashbackPercent: Float,
    val color: Long,
    val benefits: List<String>,
)
