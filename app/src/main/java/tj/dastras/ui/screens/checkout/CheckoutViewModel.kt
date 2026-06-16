package tj.dastras.ui.screens.checkout

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.dastras.data.Branch
import tj.dastras.data.BranchRepository
import tj.dastras.data.CartItem
import tj.dastras.data.CartRepository
import tj.dastras.data.OrderItemRequest
import tj.dastras.data.OrderRepository
import tj.dastras.data.remote.friendlyErrorMessage
import javax.inject.Inject

private const val TAG = "CheckoutViewModel"

data class CheckoutUiState(
    val items: List<CartItem> = emptyList(),
    val branches: List<Branch> = emptyList(),
    val selectedBranchId: Int? = null,
    val isLoading: Boolean = false,
    val isPlacingOrder: Boolean = false,
    val orderPlaced: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val branchRepository: BranchRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
) : ViewModel() {

    var uiState by mutableStateOf(CheckoutUiState())
        private set

    init { load() }

    fun load() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val branches = branchRepository.getAll()
                val items    = cartRepository.getItems()
                uiState = uiState.copy(
                    items            = items,
                    branches         = branches,
                    selectedBranchId = uiState.selectedBranchId ?: branches.firstOrNull()?.id,
                    isLoading        = false,
                )
            } catch (e: Exception) {
                Log.e(TAG, "load: error", e)
                uiState = uiState.copy(isLoading = false, error = friendlyErrorMessage(e))
            }
        }
    }

    fun selectBranch(branchId: Int) {
        uiState = uiState.copy(selectedBranchId = branchId)
    }

    fun placeOrder(address: String) {
        if (uiState.isPlacingOrder) return
        viewModelScope.launch {
            uiState = uiState.copy(isPlacingOrder = true, error = null)
            try {
                val items = uiState.items.map { item ->
                    OrderItemRequest(productId = item.product.id, quantity = item.quantity, price = item.product.price)
                }
                val order = orderRepository.createOrder(address, items, uiState.selectedBranchId)
                if (order != null) {
                    cartRepository.clear()
                    uiState = uiState.copy(isPlacingOrder = false, orderPlaced = true)
                } else {
                    uiState = uiState.copy(isPlacingOrder = false, error = "Не удалось оформить заказ")
                }
            } catch (e: Exception) {
                Log.e(TAG, "placeOrder: error", e)
                uiState = uiState.copy(isPlacingOrder = false, error = friendlyErrorMessage(e))
            }
        }
    }
}
