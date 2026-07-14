package tj.relax.data

import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.dataOrThrow
import tj.relax.core.api.toApiException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    suspend fun getFavorites(): List<Product> =
        api.getFavorites().dataOrThrow()

    suspend fun add(productId: Int) {
        val response = api.addFavorite(productId)
        if (!response.isSuccessful) throw response.toApiException()
    }

    suspend fun remove(productId: Int) {
        val response = api.removeFavorite(productId)
        if (!response.isSuccessful) throw response.toApiException()
    }
}
