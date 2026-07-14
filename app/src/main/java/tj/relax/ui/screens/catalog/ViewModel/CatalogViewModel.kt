package tj.relax.ui.screens.catalog.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject
@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    var uiState by mutableStateOf(CatalogUiState())
        private set

    init { load() }

    private fun load() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
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
                        products    = response.items,
                        categories  = categories,
                        page        = response.page,
                        hasNextPage = response.hasNextPage,
                        isLoading   = false,
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }

    fun loadNextPage() {
        if (uiState.isLoadingMore) return
        if (!uiState.hasNextPage) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoadingMore = true)
            try {
                val response = productRepository.getAll(
                    CatalogRequest(
                        page       = 1,
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
        load()
    }
}