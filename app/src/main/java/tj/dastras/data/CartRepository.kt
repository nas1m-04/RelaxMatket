package tj.dastras.data

import tj.dastras.data.remote.RelaxApiService
import tj.dastras.data.remote.dataOrThrow
import tj.dastras.data.remote.toApiException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    suspend fun getItems(): List<CartItem> =
        api.getCart().dataOrThrow()
            .mapNotNull { row -> row.product?.let { CartItem(product = it, quantity = row.quantity) } }

    suspend fun upsert(productId: Int, quantity: Int) {
        val response = api.addToCart(AddToCartRequest(productId, quantity))
        if (!response.isSuccessful) throw response.toApiException()
    }

    suspend fun remove(productId: Int) {
        val response = api.removeFromCart(productId)
        if (!response.isSuccessful) throw response.toApiException()
    }

    suspend fun clear() {
        val response = api.clearCart()
        if (!response.isSuccessful) throw response.toApiException()
    }
}
