package tj.relax.ui.screens.cart.data.dto.response

import tj.relax.data.Product

data class CartItemResponse(
    val id: Int = 0,
    val userUid: String = "",
    val productId: Int = 0,
    val quantity: Int = 1,
    val product: Product? = null,
)
