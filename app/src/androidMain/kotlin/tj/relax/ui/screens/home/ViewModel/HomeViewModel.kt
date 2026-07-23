package tj.relax.ui.screens.home.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

private const val TAG = "HomeViewModel"

class HomeViewModel(
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
        viewModelScope.launch {
            runCatching { notificationsRepository.markRead(id) }
            refreshUnreadCount()
        }
    }

    /** Re-checks whether the bell icon's unread dot should show — called on init/pull-to-refresh
     * and whenever the Home screen resumes, since marking notifications read happens on a
     * separate screen this ViewModel has no other way to hear about. */
    fun refreshUnreadCount() {
        viewModelScope.launch {
            runCatching { notificationsRepository.getUnreadCount() }
                .onSuccess { count -> uiState = uiState.copy(hasUnread = count > 0) }
        }
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
                    val unreadD   = async { runCatching { notificationsRepository.getUnreadCount() }.getOrNull() }

                    val products     = popularD.await()
                    val newProducts  = newD.await().ifEmpty { products.filter { it.isNew } }
                    val saleProducts = saleD.await().ifEmpty { products.filter { it.oldPrice != null } }
                    val categories   = catsD.await()
                    val banners      = bannersD.await().ifEmpty { MockData.banners }
                    val inAppModal   = modalD.await()
                    val unreadCount  = unreadD.await()

                    uiState = uiState.copy(
                        products     = products,
                        newProducts  = newProducts,
                        saleProducts = saleProducts,
                        categories   = categories,
                        banners      = banners,
                        inAppModal   = inAppModal,
                        hasUnread    = unreadCount?.let { it > 0 } ?: uiState.hasUnread,
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
