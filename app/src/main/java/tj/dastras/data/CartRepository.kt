package tj.dastras.data

import android.util.Log
import tj.dastras.data.remote.RelaxApiService
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "CartRepository"

@Singleton
class CartRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    suspend fun getItems(): List<CartItem> =
        api.getCart().let { response ->
            if (!response.isSuccessful) Log.w(TAG, "getItems: failed code=${response.code()}")
            response.body()?.data
                ?.mapNotNull { row -> row.product?.let { CartItem(product = it, quantity = row.quantity) } }
                ?: emptyList()
        }

    suspend fun upsert(productId: Int, quantity: Int): Boolean {
        val response = api.addToCart(AddToCartRequest(productId, quantity))
        if (!response.isSuccessful) Log.w(TAG, "upsert: failed code=${response.code()} productId=$productId")
        return response.isSuccessful
    }

    suspend fun remove(productId: Int) {
        val response = api.removeFromCart(productId)
        if (!response.isSuccessful) Log.w(TAG, "remove: failed code=${response.code()} productId=$productId")
    }

    suspend fun clear() {
        val response = api.clearCart()
        if (!response.isSuccessful) Log.w(TAG, "clear: failed code=${response.code()}")
    }
}
