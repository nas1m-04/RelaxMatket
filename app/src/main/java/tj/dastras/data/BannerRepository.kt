package tj.dastras.data

import android.util.Log
import tj.dastras.data.remote.RelaxApiService
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "BannerRepository"

@Singleton
class BannerRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    suspend fun getAll(): List<Banner> =
        api.getBanners().let { response ->
            if (!response.isSuccessful) Log.w(TAG, "getAll: failed code=${response.code()}")
            response.body()?.data ?: emptyList()
        }
}
