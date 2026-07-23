package tj.relax.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tj.relax.core.api.PagedResponse
import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.dataOrThrow
import tj.relax.core.db.dao.CachedProductDao
import tj.relax.core.db.entity.toDomain
import tj.relax.core.db.entity.toEntity
import tj.relax.core.di.ApplicationScope
import tj.relax.ui.screens.catalog.data.dto.request.CatalogRequest
import javax.inject.Inject
import javax.inject.Singleton

private const val LIST_POPULAR = "popular"
private const val LIST_NEW     = "new"
private const val LIST_SALE    = "sale"

@Singleton
class ProductRepository @Inject constructor(
    private val api: RelaxApiService,
    private val dao: CachedProductDao,
    @ApplicationScope private val appScope: CoroutineScope,
) {
    // L1: RAM caches per list type
    private val popularMem = MemoryCache<List<Product>>(ttlMs = 3 * 60_000L)
    private val newMem     = MemoryCache<List<Product>>(ttlMs = 3 * 60_000L)
    private val saleMem    = MemoryCache<List<Product>>(ttlMs = 3 * 60_000L)

    // Catalog & search — always fresh from network, not cached locally
    suspend fun getAll(request: CatalogRequest): PagedResponse<Product> =
        api.getProducts(
            page       = request.page,
            pageSize   = request.pageSize,
            categoryId = request.categoryId,
            search     = request.search,
            sort       = request.sort,
        ).dataOrThrow().let { r ->
            PagedResponse(r.items, r.totalCount, r.page, r.pageSize, r.totalPages, r.hasNextPage, r.hasPreviousPage)
        }

    suspend fun getByCategory(categoryId: Int): List<Product> =
        api.getProducts(categoryId = categoryId).dataOrThrow().items

    suspend fun search(query: String, sort: String? = null): List<Product> =
        api.getProducts(search = query, sort = sort).dataOrThrow().items

    suspend fun getById(id: Int): Product =
        api.getProduct(id).dataOrThrow()

    // ── Home screen lists — L1 RAM → L2 Room → L3 Network ────────────────────

    suspend fun getPopular(): List<Product> = getCached(LIST_POPULAR, popularMem) {
        api.getPopularProducts().dataOrThrow()
    }

    suspend fun getNew(): List<Product> = getCached(LIST_NEW, newMem) {
        api.getNewProducts().dataOrThrow()
    }

    suspend fun getSale(): List<Product> = getCached(LIST_SALE, saleMem) {
        api.getSaleProducts().dataOrThrow()
    }

    private suspend fun getCached(
        type: String,
        mem: MemoryCache<List<Product>>,
        fetch: suspend () -> List<Product>,
    ): List<Product> {
        // L1
        mem.get()?.let { return it }
        // L2
        val local = dao.getByType(type)
        if (local.isNotEmpty()) {
            val result = local.map { it.toDomain() }
            mem.set(result)
            appScope.launch { runCatching { fetchAndSave(type, mem, fetch) } }
            return result
        }
        // L3
        return fetchAndSave(type, mem, fetch)
    }

    private suspend fun fetchAndSave(
        type: String,
        mem: MemoryCache<List<Product>>,
        fetch: suspend () -> List<Product>,
    ): List<Product> {
        val remote = fetch()
        dao.deleteByType(type)
        dao.insertAll(remote.map { it.toEntity(type) })
        mem.set(remote)
        return remote
    }

    fun invalidateHomeCache() {
        popularMem.invalidate()
        newMem.invalidate()
        saleMem.invalidate()
        appScope.launch {
            dao.deleteByType(LIST_POPULAR)
            dao.deleteByType(LIST_NEW)
            dao.deleteByType(LIST_SALE)
        }
    }
}
