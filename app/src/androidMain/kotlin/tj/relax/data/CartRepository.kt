package tj.relax.data

import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.dataOrThrow
import tj.relax.core.api.toApiException

class CartRepository(
    private val api: RelaxApiService,
) {
    // The backend can end up with two cart rows for the same product (e.g. two rapid
    // add-to-cart calls racing before the first insert lands) — merge those here so the
    // cart list can never contain a duplicate product id, which would crash LazyColumn's
    // key(= product.id) requirement.
    suspend fun getItems(): List<CartItem> =
        api.getCart().dataOrThrow()
            .mapNotNull { row -> row.product?.let { CartItem(product = it, quantity = row.quantity) } }
            .groupBy { it.product.id }
            .map { (_, rows) -> rows.first().copy(quantity = rows.sumOf { it.quantity }) }

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
