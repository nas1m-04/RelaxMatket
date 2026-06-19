package tj.dastras.data

import tj.dastras.core.api.RelaxApiService
import tj.dastras.core.api.dataOrThrow
import tj.dastras.core.api.toApiException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    @Volatile private var cached: List<Product>? = null

    suspend fun getFavorites(forceRefresh: Boolean = false): List<Product> {
        cached?.let { if (!forceRefresh) return it }
        return api.getFavorites().dataOrThrow().also { cached = it }
    }

    suspend fun add(productId: Int) {
        val response = api.addFavorite(productId)
        if (!response.isSuccessful) throw response.toApiException()
        // Invalidate so next getFavorites() fetches fresh list with full product details
        cached = null
    }

    suspend fun remove(productId: Int) {
        val response = api.removeFavorite(productId)
        if (!response.isSuccessful) throw response.toApiException()
        cached = cached?.filter { it.id != productId }
    }

    fun invalidate() { cached = null }
}
