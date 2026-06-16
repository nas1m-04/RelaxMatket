package tj.dastras.data

import android.util.Log
import tj.dastras.data.remote.RelaxApiService
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "BranchRepository"

@Singleton
class BranchRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    suspend fun getAll(): List<Branch> =
        api.getBranches().let { response ->
            if (!response.isSuccessful) Log.w(TAG, "getAll: failed code=${response.code()}")
            response.body()?.data?.filter { it.isActive } ?: emptyList()
        }
}
