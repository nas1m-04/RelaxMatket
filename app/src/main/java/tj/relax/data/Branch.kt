package tj.relax.data

data class Branch(
    val id: Int = 0,
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val phone: String? = null,
    val isActive: Boolean = true,
)
