package tj.dastras.data

import tj.dastras.core.api.PagedResponse
import tj.dastras.core.api.RelaxApiService
import tj.dastras.core.api.dataOrThrow
import tj.dastras.ui.screens.catalog.data.dto.request.CatalogRequest
import javax.inject.Inject
import javax.inject.Singleton

private const val PRODUCT_TTL_MS = 5 * 60 * 1000L

@Singleton
class ProductRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    @Volatile private var cachedNew: List<Product>? = null
    @Volatile private var newFetchedAt: Long = 0L

    @Volatile private var cachedSale: List<Product>? = null
    @Volatile private var saleFetchedAt: Long = 0L

    suspend fun getAll(request: CatalogRequest): PagedResponse<Product> {

        return api.getProducts(
            page = request.page,
            pageSize =request.pageSize,
            categoryId = request.categoryId,
            search = request.search,
            sort = request.sort
        ).dataOrThrow()
            .let { response ->
                PagedResponse(
                    items        = response.items,
                    totalCount   = response.totalCount,
                    page         = response.page,
                    pageSize     = response.pageSize,
                    totalPages   = response.totalPages,
                    hasNextPage  = response.hasNextPage,
                    hasPreviousPage = response.hasPreviousPage,
                )
            }
    }


    suspend fun getByCategory(categoryId: Int): List<Product> =
        api.getProducts(categoryId = categoryId).dataOrThrow().items

    suspend fun search(query: String, sort: String? = null): List<Product> =
        api.getProducts(search = query, sort = sort).dataOrThrow().items

    suspend fun getById(id: Int): Product =
        api.getProduct(id).dataOrThrow()

    suspend fun getNew(): List<Product> {
        val now = System.currentTimeMillis()
        if (cachedNew != null && now - newFetchedAt < PRODUCT_TTL_MS) return cachedNew!!
        return api.getNewProducts().dataOrThrow().also {
            cachedNew = it
            newFetchedAt = System.currentTimeMillis()
        }
    }
    suspend fun getPopular(): List<Product> {
        return api.getPopularProducts().dataOrThrow()
    }

    suspend fun getSale(): List<Product> {
        val now = System.currentTimeMillis()
        if (cachedSale != null && now - saleFetchedAt < PRODUCT_TTL_MS) return cachedSale!!
        return api.getSaleProducts().dataOrThrow().also {
            cachedSale = it
            saleFetchedAt = System.currentTimeMillis()
        }
    }
}
