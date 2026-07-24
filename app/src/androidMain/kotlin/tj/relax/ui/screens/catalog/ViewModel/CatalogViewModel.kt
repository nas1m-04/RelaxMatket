package tj.relax.ui.screens.catalog.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import tj.relax.data.Category
import tj.relax.data.CategoryRepository
import tj.relax.data.Product
import tj.relax.data.ProductRepository
import tj.relax.core.api.ErrorPresenter
import tj.relax.core.api.friendlyErrorMessage
import tj.relax.ui.screens.catalog.data.dto.request.CatalogRequest
import tj.relax.ui.screens.catalog.presentation.data.CatalogUiState

class CatalogViewModel(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    var uiState by mutableStateOf(CatalogUiState())
        private set

    // Fetching the whole catalog is expensive — this ViewModel is shared (sharedViewModel()) and
    // grabbed from the Home screen too (just to pre-set a quick filter before navigating here), so
    // it must NOT fetch on construction. The Catalog screen triggers the first load itself via
    // loadIfNeeded() when it actually appears.
    private var hasLoaded = false

    // fetch() (full reload/filter change) and loadNextPage() (append) both mutate
    // uiState.products, so only one may be in flight at a time — otherwise a next-page
    // response for a stale filter can land after a fetch() already replaced the list,
    // appending overlapping product ids and crashing the grid with a duplicate key.
    private var fetchJob: Job? = null

    fun loadIfNeeded() {
        if (hasLoaded) return
        fetch()
    }

    fun refresh() = fetch(isRefresh = true)

    private fun fetch(isRefresh: Boolean = false) {
        hasLoaded = true
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            uiState = uiState.copy(isLoading = !isRefresh, isRefreshing = isRefresh, error = null)
            try {
                coroutineScope {
                    val productsD   = async {
                        productRepository.getAll(
                            CatalogRequest(
                                page       = 1,
                                categoryId = uiState.selectedCategoryId.takeIf { it != 0 },
                                search     = uiState.searchQuery.ifBlank { null },
                                sort       = if (uiState.showNewOnly) "new" else uiState.sortBy,
                                priceFrom  = uiState.priceFrom,
                                priceTo    = uiState.priceTo,
                            )
                        )
                    }
                    val categoriesD = async { categoryRepository.getAll() }

                    val response   = productsD.await()
                    val categories = categoriesD.await()

                    uiState = uiState.copy(
                        products     = response.items,
                        categories   = categories,
                        page         = response.page,
                        hasNextPage  = response.hasNextPage,
                        isLoading    = false,
                        isRefreshing = false,
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, isRefreshing = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }

    fun loadNextPage() {
        if (uiState.isLoadingMore) return
        if (!uiState.hasNextPage) return

        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            uiState = uiState.copy(isLoadingMore = true)
            try {
                val response = productRepository.getAll(
                    CatalogRequest(
                        page       = uiState.page + 1,
                        categoryId = uiState.selectedCategoryId.takeIf { it != 0 },
                        search     = uiState.searchQuery.ifBlank { null },
                        sort       = if (uiState.showNewOnly) "new" else uiState.sortBy,
                        priceFrom  = uiState.priceFrom,
                        priceTo    = uiState.priceTo,
                    )
                )

                uiState = uiState.copy(
                    products = uiState.products + response.items,
                    page = response.page,
                    hasNextPage = response.hasNextPage,
                    isLoadingMore = false
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isLoadingMore = false, error = friendlyErrorMessage(e))
            }
        }
    }

    fun selectCategory(id: Int) {
        uiState = uiState.copy(selectedCategoryId = id)
        applyFilters()
    }

    fun search(query: String) {
        uiState = uiState.copy(searchQuery = query)
        applyFilters()
    }

    fun sort(sortBy: String?) {
        uiState = uiState.copy(sortBy = sortBy)
        applyFilters()
    }

    fun toggleFilter() {
        uiState = uiState.copy(showFilter = !uiState.showFilter)
    }

    fun setNewOnly(value: Boolean) {
        uiState = uiState.copy(showNewOnly = value)
        applyFilters()
    }

    fun applyQuickFilter(categoryId: Int = 0, newOnly: Boolean = false, sortBy: String = "popular") {
        uiState = uiState.copy(
            selectedCategoryId = categoryId,
            searchQuery = "",
            showNewOnly = newOnly,
            sortBy = sortBy,
        )
        applyFilters()
    }

    fun applyPriceFilter(from: Int?, to: Int?) {
        uiState = uiState.copy(priceFrom = from, priceTo = to)
        applyFilters()
    }
    private fun applyFilters() {
        fetch()
    }
}