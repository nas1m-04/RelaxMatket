package tj.dastras.ui.screens.catalog.ViewModel

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
import tj.dastras.core.api.ErrorPresenter
import tj.dastras.core.api.friendlyErrorMessage
import tj.dastras.ui.screens.catalog.data.dto.request.CatalogRequest
import tj.dastras.ui.screens.catalog.presentation.data.CatalogUiState
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
                val response = productRepository.getAll(
                    CatalogRequest(
                        page       = 1,
                        categoryId = uiState.selectedCategoryId.takeIf { it != 0 },
                        search     = uiState.searchQuery.ifBlank { null },
                        sort       = uiState.sortBy
                    )
                )
                val categories = categoryRepository.getAll()
                uiState = uiState.copy(
                    products = response.items,
                    categories = categories,
                    page = response.page,
                    hasNextPage = response.hasNextPage,
                    isLoading = false
                )
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
                        page       = uiState.page + 1,
                        categoryId = uiState.selectedCategoryId.takeIf { it != 0 },
                        search     = uiState.searchQuery.ifBlank { null },
                        sort       = uiState.sortBy
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

    fun applyQuickFilter(categoryId: Int = 0, newOnly: Boolean = false, sortBy: String = "popular") {
        uiState = uiState.copy(
            selectedCategoryId = categoryId,
            searchQuery = "",
            showNewOnly = newOnly,
            sortBy = sortBy,
        )
        applyFilters()
    }

    // Фильтры отправляются на бэк, не локально
    private fun applyFilters() {
        load()
    }
}