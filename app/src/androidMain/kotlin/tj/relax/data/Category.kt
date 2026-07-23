package tj.relax.data

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int = 0,
    val name: String = "",
    val icon: String? = null,
    val color: String? = null,
    val productCount: Int = 0,
)
