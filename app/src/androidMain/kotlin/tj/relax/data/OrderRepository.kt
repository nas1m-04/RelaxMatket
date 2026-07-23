package tj.relax.data

import tj.relax.core.api.PagedResponse
import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.dataOrThrow
import tj.relax.ui.screens.orders.data.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    suspend fun getOrders(page: Int = 1, pageSize: Int = 20): PagedResponse<Order> {
        val response = api.getOrders(page, pageSize).dataOrThrow()
        return PagedResponse(
            items = response.items.map { it.toDomain() },
            totalCount = response.totalCount,
            page = response.page,
            pageSize = response.pageSize,
            totalPages = response.totalPages,
            hasPreviousPage = response.hasPreviousPage,
            hasNextPage = response.hasNextPage,
        )
    }

    suspend fun createOrder(request: CreateOrderRequest): Order =
        api.createOrder(request).dataOrThrow().toDomain()

    suspend fun getById(id: String): Order =
        api.getOrder(id).dataOrThrow().toDomain()
}
