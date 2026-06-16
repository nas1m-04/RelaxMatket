package tj.dastras.data

import tj.dastras.data.remote.RelaxApiService
import tj.dastras.data.remote.dataOrThrow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class BranchRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    @Volatile private var cached: List<Branch>? = null

    suspend fun getAll(forceRefresh: Boolean = false): List<Branch> {
        cached?.let { if (!forceRefresh) return it }
        val branches = api.getBranches().dataOrThrow()
        cached = branches
        return branches
    }
}
