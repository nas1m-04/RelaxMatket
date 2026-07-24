package tj.relax.ui.screens.home.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.dataOrThrow
import tj.relax.core.db.dao.BannerDao
import tj.relax.core.db.entity.toDomain
import tj.relax.core.db.entity.toEntity
import tj.relax.data.Banner
import tj.relax.data.MemoryCache

class BannerRepository(
    private val api: RelaxApiService,
    private val dao: BannerDao,
    private val appScope: CoroutineScope,
) {
    private val memCache = MemoryCache<List<Banner>>(ttlMs = 10 * 60_000L)

    suspend fun getAll(): List<Banner> {
        // L1: RAM
        memCache.get()?.let { return it }

        // L2: Room
        val local = dao.getAll()
        if (local.isNotEmpty()) {
            val result = local.map { it.toDomain() }
            memCache.set(result)
            appScope.launch { runCatching { fetchAndSave() } }
            return result
        }

        // L3: Network
        return fetchAndSave()
    }

    private suspend fun fetchAndSave(): List<Banner> {
        val remote = api.getBanners().dataOrThrow()
        dao.deleteAll()
        dao.insertAll(remote.map { it.toEntity() })
        return remote.also { memCache.set(it) }
    }

    fun invalidateCache() {
        memCache.invalidate()
        appScope.launch { dao.deleteAll() }
    }
}
