package tj.relax.data

import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.dataOrThrow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BranchRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    suspend fun getAll(): List<Branch> =
        api.getBranches().dataOrThrow()
}
