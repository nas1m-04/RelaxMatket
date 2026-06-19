package tj.dastras.ui.screens.home.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.dastras.data.Banner
import tj.dastras.ui.screens.home.data.repository.BannerRepository
import tj.dastras.data.Category
import tj.dastras.data.CategoryRepository
import tj.dastras.data.LocalUserStore
import tj.dastras.data.MockData
import tj.dastras.data.Product
import tj.dastras.data.ProductRepository
import tj.dastras.core.api.ErrorPresenter
import tj.dastras.core.api.friendlyErrorMessage
import javax.inject.Inject

private const val TAG = "HomeViewModel"

data class HomeUiState(
    val products: List<Product> = emptyList(),
    val newProducts: List<Product> = emptyList(),
    val saleProducts: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val banners: List<Banner> = emptyList(),
    val deliveryAddress: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val bannerRepository: BannerRepository,
    private val localUserStore: LocalUserStore,
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState(deliveryAddress = localUserStore.getDeliveryAddress()))
        private set

    fun setDeliveryAddress(address: String) {
        localUserStore.saveDeliveryAddress(address)
        uiState = uiState.copy(deliveryAddress = address)
    }

    init { load() }

    fun load() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val products     = productRepository.getPopular()
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
