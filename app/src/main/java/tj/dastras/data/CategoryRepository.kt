package tj.dastras.data

import tj.dastras.core.api.RelaxApiService
import tj.dastras.core.api.dataOrThrow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    @Volatile private var cached: List<Category>? = null

    suspend fun getAll(): List<Category> {
        cached?.let { return it }
        return api.getCategories().dataOrThrow().also { cached = it }
    }
}
