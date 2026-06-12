package tj.dastras.data

import android.util.Log
import tj.dastras.data.remote.RelaxApiService
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "CategoryRepository"

@Singleton
class CategoryRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    suspend fun getAll(): List<Category> =
        api.getCategories().let { response ->
            if (!response.isSuccessful) Log.w(TAG, "getAll: failed code=${response.code()}")
            response.body()?.data ?: emptyList()
        }
}
