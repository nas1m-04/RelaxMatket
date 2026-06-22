package tj.dastras.ui.screens.catalog.presentation.data

import tj.dastras.data.Category
import tj.dastras.data.Product

data class CatalogUiState(
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),

    val page: Int = 1,
    val hasNextPage: Boolean = true,

    val selectedCategoryId: Int = 0,
    val searchQuery: String = "",
    val sortBy: String? = null,
    val showNewOnly: Boolean = false,
    val showFilter: Boolean = false,

    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val priceFrom: Int? = null,
    val priceTo: Int? = null,
    val showSortSheet: Boolean = false,
    val showFilterSheet : Boolean = false,
)
