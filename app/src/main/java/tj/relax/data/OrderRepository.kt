package tj.relax.data

import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.dataOrThrow
import tj.relax.ui.screens.orders.data.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    suspend fun getOrders(): List<Order> =
        api.getOrders().dataOrThrow().map { it.toDomain() }

    suspend fun createOrder(request: CreateOrderRequest): Order =
        api.createOrder(request).dataOrThrow().toDomain()

    suspend fun getById(id: String): Order =
        api.getOrder(id).dataOrThrow().toDomain()
}
