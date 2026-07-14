package tj.relax.ui.screens.favorites

import tj.relax.data.Product

data class FavoritesUiState(
    val favorites: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)
