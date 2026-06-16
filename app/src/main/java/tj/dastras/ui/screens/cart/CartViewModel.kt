package tj.dastras.ui.screens.cart

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.dastras.data.AuthRepository
import tj.dastras.data.CartItem
import tj.dastras.data.CartRepository
import tj.dastras.data.LoyaltyRepository
import tj.dastras.data.Product
import tj.dastras.data.UserRepository
import tj.dastras.data.remote.ErrorPresenter
import tj.dastras.data.remote.friendlyErrorMessage
import javax.inject.Inject

private const val TAG = "CartViewModel"

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val bonusBalance: Double = 0.0,
    val bonusToCurrencyRate: Double = 1.0,
    val maxBonusPaymentPercent: Double = 50.0,
    val promoCode: String = "",
    val promoApplied: Boolean = false,
    val useBonuses: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val loyaltyRepository: LoyaltyRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    var uiState by mutableStateOf(CartUiState())
        private set

    init { load() }

    fun load() {
        if (!authRepository.isLoggedIn) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val items   = cartRepository.getItems()
                val profile = userRepository.getOrCreate()
                val loyalty = loyaltyRepository.getSummary()
                uiState = uiState.copy(
                    items                  = items,
                    bonusBalance           = profile.bonusBalance,
                    bonusToCurrencyRate    = loyalty.bonusToCurrencyRate,
                    maxBonusPaymentPercent = loyalty.maxBonusPaymentPercent,
                    isLoading              = false,
                )
            } catch (e: Exception) {
                Log.e(TAG, "load: error", e)
                uiState = uiState.copy(isLoading = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }

    fun quantityOf(productId: Int): Int =
        uiState.items.find { it.product.id == productId }?.quantity ?: 0

    fun add(product: Product) {
        if (!authRepository.isLoggedIn) {
            Log.d(TAG, "add: skipped, user is not logged in")
            return
        }
        val existing = uiState.items.find { it.product.id == product.id }
        if (existing != null) {
            increase(product.id)
            return
        }
        uiState = uiState.copy(items = uiState.items + CartItem(product = product, quantity = 1))
        viewModelScope.launch {
            try {
                cartRepository.upsert(product.id, 1)
            } catch (e: Exception) {
                Log.e(TAG, "add: error productId=${product.id}", e)
                ErrorPresenter.report(e)
            }
        }
    }

    fun increase(productId: Int) {
        val item   = uiState.items.find { it.product.id == productId } ?: return
        val newQty = item.quantity + 1
        uiState    = uiState.copy(items = uiState.items.map { if (it.product.id == productId) it.copy(quantity = newQty) else it })
        viewModelScope.launch {
            try {
                cartRepository.upsert(productId, newQty)
            } catch (e: Exception) {
                Log.e(TAG, "increase: error productId=$productId", e)
                ErrorPresenter.report(e)
            }
        }
    }

    fun decrease(productId: Int) {
        val item = uiState.items.find { it.product.id == productId } ?: return
        if (item.quantity <= 1) { remove(productId); return }
        val newQty = item.quantity - 1
        uiState    = uiState.copy(items = uiState.items.map { if (it.product.id == productId) it.copy(quantity = newQty) else it })
        viewModelScope.launch {
            try {
                cartRepository.upsert(productId, newQty)
            } catch (e: Exception) {
                Log.e(TAG, "decrease: error productId=$productId", e)
                ErrorPresenter.report(e)
            }
        }
    }

    fun remove(productId: Int) {
        uiState = uiState.copy(items = uiState.items.filter { it.product.id != productId })
        viewModelScope.launch {
            try {
                cartRepository.remove(productId)
            } catch (e: Exception) {
                Log.e(TAG, "remove: error productId=$productId", e)
                ErrorPresenter.report(e)
            }
        }
    }

    fun clear() {
        uiState = uiState.copy(items = emptyList())
        viewModelScope.launch {
            try {
                cartRepository.clear()
            } catch (e: Exception) {
                Log.e(TAG, "clear: error", e)
                ErrorPresenter.report(e)
            }
        }
    }

    fun setPromoCode(code: String) { uiState = uiState.copy(promoCode = code) }

    fun togglePromo() {
        if (uiState.promoCode.isNotEmpty()) uiState = uiState.copy(promoApplied = !uiState.promoApplied)
    }

    fun toggleBonuses(value: Boolean) { uiState = uiState.copy(useBonuses = value) }
}
