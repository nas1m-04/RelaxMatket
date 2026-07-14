package tj.relax.ui.screens.promotions

import tj.relax.data.Product
import tj.relax.data.Promotion

data class PromotionsUiState(
    val promotions: List<Promotion> = emptyList(),
    val saleProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)
