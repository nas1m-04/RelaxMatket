package tj.relax.data

import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.dataOrThrow

class BranchRepository(
    private val api: RelaxApiService,
) {
    suspend fun getAll(): List<Branch> =
        api.getBranches().dataOrThrow()
}
