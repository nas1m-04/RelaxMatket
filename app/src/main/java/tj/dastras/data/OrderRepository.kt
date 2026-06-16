package tj.dastras.data

import android.util.Log
import tj.dastras.data.remote.OrderApiResponse
import tj.dastras.data.remote.RelaxApiService
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "OrderRepository"

@Singleton
class OrderRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    suspend fun getOrders(): List<Order> =
        api.getOrders().let { response ->
            if (!response.isSuccessful) Log.w(TAG, "getOrders: failed code=${response.code()}")
            response.body()?.data?.map { it.toOrder() } ?: emptyList()
        }

    suspend fun createOrder(address: String, items: List<OrderItemRequest>, branchId: Int? = null): Order? =
        api.createOrder(CreateOrderRequest(address, items, branchId)).let { response ->
            if (!response.isSuccessful) Log.w(TAG, "createOrder: failed code=${response.code()}")
            response.body()?.data?.toOrder()
        }

    suspend fun getById(id: String): Order? =
        api.getOrder(id).let { response ->
            if (!response.isSuccessful) Log.w(TAG, "getById: failed code=${response.code()} id=$id")
            response.body()?.data?.toOrder()
        }
}

private fun OrderApiResponse.toOrder() = Order(
    id      = id,
    userUid = userUid,
    total   = total,
    status  = runCatching { OrderStatus.valueOf(status) }.getOrDefault(OrderStatus.PROCESSING),
    address = address,
    date    = createdAt,
    items   = items.mapNotNull { item ->
        item.product?.let { CartItem(product = it, quantity = item.quantity) }
    },
)
