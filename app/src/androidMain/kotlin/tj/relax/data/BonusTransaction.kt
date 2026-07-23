package tj.relax.data

data class BonusTransaction(
    val id: Int = 0,
    val description: String = "",
    val amount: Int = 0,
    val isCredit: Boolean = true,
    val date: String = "",
    val orderId: String = "",
)
