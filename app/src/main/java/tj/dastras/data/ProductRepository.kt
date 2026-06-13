package tj.dastras.data

import android.util.Log
import tj.dastras.data.remote.RelaxApiService
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ProductRepository"

@Singleton
class ProductRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    suspend fun getAll(): List<Product> =
        api.getProducts().let { response ->
            if (!response.isSuccessful) Log.w(TAG, "getAll: failed code=${response.code()}")
            response.body()?.data ?: emptyList()
        }

    suspend fun getByCategory(categoryId: Int): List<Product> =
        api.getProducts(categoryId = categoryId).let { response ->
            if (!response.isSuccessful) Log.w(TAG, "getByCategory: failed code=${response.code()}")
            response.body()?.data ?: emptyList()
        }

    suspend fun search(query: String, sort: String? = null): List<Product> =
        api.getProducts(search = query, sort = sort).let { response ->
            if (!response.isSuccessful) Log.w(TAG, "search: failed code=${response.code()}")
            response.body()?.data ?: emptyList()
        }

    suspend fun getById(id: Int): Product? =
        api.getProduct(id).let { response ->
            if (!response.isSuccessful) Log.w(TAG, "getById: failed code=${response.code()}")
            response.body()?.data
        }

    suspend fun getNew(): List<Product> =
        api.getNewProducts().let { response ->
            if (!response.isSuccessful) Log.w(TAG, "getNew: failed code=${response.code()}")
            response.body()?.data ?: emptyList()
        }

    suspend fun getSale(): List<Product> =
        api.getSaleProducts().let { response ->
            if (!response.isSuccessful) Log.w(TAG, "getSale: failed code=${response.code()}")
            response.body()?.data ?: emptyList()
        }
}
