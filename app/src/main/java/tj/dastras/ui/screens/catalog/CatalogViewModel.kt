package tj.dastras.ui.screens.catalog

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.dastras.data.Category
import tj.dastras.data.CategoryRepository
import tj.dastras.data.Product
import tj.dastras.data.ProductRepository
import tj.dastras.data.remote.ErrorPresenter
import tj.dastras.data.remote.friendlyErrorMessage
import javax.inject.Inject

private const val TAG = "CatalogViewModel"

data class CatalogUiState(
    val allProducts: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Int = 0,
    val searchQuery: String = "",
    val sortBy: String = "popular",
    val showNewOnly: Boolean = false,
    val showFilter: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

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
                val products   = productRepository.getAll()
                val categories = categoryRepository.getAll()
                uiState = uiState.copy(
                    allProducts      = products,
                    filteredProducts = products,
                    categories       = categories,
                    isLoading        = false,
                )
            } catch (e: Exception) {
                Log.e(TAG, "load: error", e)
                uiState = uiState.copy(isLoading = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
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

    fun sort(sortBy: String) {
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

    /** Resets the catalog filters and applies the given quick filter — used when jumping in from the Home screen. */
    fun applyQuickFilter(categoryId: Int = 0, newOnly: Boolean = false, sortBy: String = "popular") {
        uiState = uiState.copy(
            selectedCategoryId = categoryId,
            searchQuery        = "",
            showNewOnly        = newOnly,
            sortBy             = sortBy,
        )
        applyFilters()
    }

    private fun applyFilters() {
        val filtered = uiState.allProducts
            .filter { p ->
                (uiState.selectedCategoryId == 0 || p.categoryId == uiState.selectedCategoryId) &&
                (uiState.searchQuery.isEmpty() || p.name.contains(uiState.searchQuery, ignoreCase = true)) &&
                (!uiState.showNewOnly || p.isNew)
            }
            .let { list ->
                when (uiState.sortBy) {
                    "price_asc"  -> list.sortedBy { it.price }
                    "price_desc" -> list.sortedByDescending { it.price }
                    "rating"     -> list.sortedByDescending { it.rating }
                    else         -> list
                }
            }
        uiState = uiState.copy(filteredProducts = filtered)
    }
}
