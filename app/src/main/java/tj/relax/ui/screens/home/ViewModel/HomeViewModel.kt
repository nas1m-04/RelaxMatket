package tj.relax.ui.screens.home.ViewModel

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
import tj.relax.ui.screens.home.data.repository.BannerRepository
import tj.relax.data.CategoryRepository
import tj.relax.data.LocalUserStore
import tj.relax.data.MockData
import tj.relax.data.NotificationsRepository
import tj.relax.data.ProductRepository
import tj.relax.core.api.ErrorPresenter
import tj.relax.core.api.friendlyErrorMessage
import javax.inject.Inject

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val bannerRepository: BannerRepository,
    private val localUserStore: LocalUserStore,
    private val notificationsRepository: NotificationsRepository,
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState(deliveryAddress = localUserStore.getDeliveryAddress()))
        private set

    fun setDeliveryAddress(address: String) {
        localUserStore.saveDeliveryAddress(address)
        uiState = uiState.copy(deliveryAddress = address)
    }

    fun dismissInAppModal() {
        val id = uiState.inAppModal?.id ?: return
        uiState = uiState.copy(inAppModal = null)
        viewModelScope.launch { runCatching { notificationsRepository.markRead(id) } }
    }

    init { load() }

    fun load(forceRefresh: Boolean = false) {
        if (forceRefresh) {
            productRepository.invalidateHomeCache()
            categoryRepository.invalidateCache()
            bannerRepository.invalidateCache()
        }
        val firstLoad = uiState.isEmpty
        uiState = uiState.copy(
            isLoading    = firstLoad,
            isRefreshing = !firstLoad,
            error        = null,
        )
        viewModelScope.launch {
            try {
                coroutineScope {
                    val popularD  = async { productRepository.getPopular() }
                    val newD      = async { productRepository.getNew() }
                    val saleD     = async { productRepository.getSale() }
                    val catsD     = async { categoryRepository.getAll() }
                    val bannersD  = async { bannerRepository.getAll() }
                    val modalD    = async { runCatching { notificationsRepository.getInAppModal() }.getOrNull() }

                    val products     = popularD.await()
                    val newProducts  = newD.await().ifEmpty { products.filter { it.isNew } }
                    val saleProducts = saleD.await().ifEmpty { products.filter { it.oldPrice != null } }
                    val categories   = catsD.await()
                    val banners      = bannersD.await().ifEmpty { MockData.banners }
                    val inAppModal   = modalD.await()

                    uiState = uiState.copy(
                        products     = products,
                        newProducts  = newProducts,
                        saleProducts = saleProducts,
                        categories   = categories,
                        banners      = banners,
                        inAppModal   = inAppModal,
                        isLoading    = false,
                        isRefreshing = false,
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "load: error", e)
                uiState = uiState.copy(isLoading = false, isRefreshing = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }
}
