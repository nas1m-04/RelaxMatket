package tj.relax.ui.screens.product

import tj.relax.data.Category
import tj.relax.data.Product

data class ProductDetailUiState(
    val product: Product? = null,
    val category: Category? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
