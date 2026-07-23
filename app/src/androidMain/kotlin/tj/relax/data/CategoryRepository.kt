package tj.relax.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.dataOrThrow
import tj.relax.core.db.dao.CategoryDao
import tj.relax.core.db.entity.toDomain
import tj.relax.core.db.entity.toEntity
import tj.relax.core.di.ApplicationScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val api: RelaxApiService,
    private val dao: CategoryDao,
    @ApplicationScope private val appScope: CoroutineScope,
) {
    private val memCache = MemoryCache<List<Category>>(ttlMs = 10 * 60_000L)

    suspend fun getAll(): List<Category> {
        // L1: RAM
        memCache.get()?.let { return it }

        // L2: Room (instant from disk)
        val local = dao.getAll()
        if (local.isNotEmpty()) {
            val result = local.map { it.toDomain() }
            memCache.set(result)
            // Refresh from network in background without blocking
            appScope.launch { runCatching { fetchAndSave() } }
            return result
        }

        // L3: Network (first launch)
        return fetchAndSave()
    }

    private suspend fun fetchAndSave(): List<Category> {
        val remote = api.getCategories().dataOrThrow()
        dao.deleteAll()
        dao.insertAll(remote.map { it.toEntity() })
        return remote.also { memCache.set(it) }
    }

    fun invalidateCache() {
        memCache.invalidate()
        appScope.launch { dao.deleteAll() }
    }
}
