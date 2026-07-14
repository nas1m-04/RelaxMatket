package tj.relax.data

data class Promotion(
    val id: Int = 0,
    val title: String = "",
    val subtitle: String = "",
    val discount: String = "",
    val imageUrl: String? = null,
    val endDate: String = "",
    val backgroundColor: Long = 0L,
)
