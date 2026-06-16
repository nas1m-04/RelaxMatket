package tj.dastras.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.dastras.data.Banner
import tj.dastras.data.BannerRepository
import tj.dastras.data.Category
import tj.dastras.data.CategoryRepository
import tj.dastras.data.MockData
import tj.dastras.data.Product
import tj.dastras.data.ProductRepository
import tj.dastras.data.remote.ErrorPresenter
import tj.dastras.data.remote.friendlyErrorMessage
import javax.inject.Inject

private const val TAG = "HomeViewModel"

data class HomeUiState(
    val products: List<Product> = emptyList(),
    val newProducts: List<Product> = emptyList(),
    val saleProducts: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val banners: List<Banner> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val bannerRepository: BannerRepository,
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    init { load() }

    fun load() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val products     = productRepository.getAll()
                val newProducts  = productRepository.getNew().ifEmpty { products.filter { it.isNew } }
                val saleProducts = productRepository.getSale().ifEmpty { products.filter { it.oldPrice != null } }
                val categories   = categoryRepository.getAll()
                val banners      = bannerRepository.getAll().ifEmpty { MockData.banners }
                uiState = uiState.copy(
                    products     = products,
                    newProducts  = newProducts,
                    saleProducts = saleProducts,
                    categories   = categories,
                    banners      = banners,
                    isLoading    = false,
                )
            } catch (e: Exception) {
                Log.e(TAG, "load: error", e)
                uiState = uiState.copy(isLoading = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }
}
