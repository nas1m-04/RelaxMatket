package tj.relax.data

import kotlinx.serialization.Serializable

@Serializable
data class Banner(
    val id: Int = 0,
    val title: String = "",
    val subtitle: String? = null,
    val imageUrl: String? = null,
    val backgroundColor: String? = null,
    val badgeText: String? = null,
)
