package tj.dastras.data

import tj.dastras.data.remote.RelaxApiService
import tj.dastras.data.remote.dataOrThrow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    suspend fun getAll(): List<Product> =
        api.getProducts().dataOrThrow()

    suspend fun getByCategory(categoryId: Int): List<Product> =
        api.getProducts(categoryId = categoryId).dataOrThrow()

    suspend fun search(query: String, sort: String? = null): List<Product> =
        api.getProducts(search = query, sort = sort).dataOrThrow()

    suspend fun getById(id: Int): Product =
        api.getProduct(id).dataOrThrow()

    suspend fun getNew(): List<Product> =
        api.getNewProducts().dataOrThrow()

    suspend fun getSale(): List<Product> =
        api.getSaleProducts().dataOrThrow()
}
