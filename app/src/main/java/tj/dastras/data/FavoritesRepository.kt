package tj.dastras.data

import android.util.Log
import tj.dastras.data.remote.RelaxApiService
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FavoritesRepository"

@Singleton
class FavoritesRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    suspend fun getFavorites(): List<Product> =
        api.getFavorites().let { response ->
            if (!response.isSuccessful) Log.w(TAG, "getFavorites: failed code=${response.code()}")
            response.body()?.data ?: emptyList()
        }

    suspend fun add(productId: Int) {
        val response = api.addFavorite(productId)
        if (!response.isSuccessful) Log.w(TAG, "add: failed code=${response.code()} productId=$productId")
    }

    suspend fun remove(productId: Int) {
        val response = api.removeFavorite(productId)
        if (!response.isSuccessful) Log.w(TAG, "remove: failed code=${response.code()} productId=$productId")
    }
}
