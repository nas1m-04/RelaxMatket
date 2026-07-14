package tj.relax.ui.screens.home.ViewModel

import tj.relax.data.Banner
import tj.relax.data.Category
import tj.relax.data.Notification
import tj.relax.data.Product

data class HomeUiState(
    val products: List<Product> = emptyList(),
    val newProducts: List<Product> = emptyList(),
    val saleProducts: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val banners: List<Banner> = emptyList(),
    val deliveryAddress: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val inAppModal: Notification? = null,
) {
    val isEmpty get() = products.isEmpty() && categories.isEmpty() && banners.isEmpty()
}
