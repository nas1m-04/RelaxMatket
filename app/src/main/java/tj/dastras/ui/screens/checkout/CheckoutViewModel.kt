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
import tj.dastras.data.CreateOrderRequest
import tj.dastras.data.LocalUserStore
import tj.dastras.data.Order
import tj.dastras.data.OrderItemRequest
import tj.dastras.data.OrderRepository
import tj.dastras.data.UserRepository
import tj.dastras.core.api.ErrorPresenter
import tj.dastras.core.api.friendlyErrorMessage
import javax.inject.Inject

private const val TAG = "CheckoutViewModel"

data class CheckoutUiState(
    val deliveryType: String = "delivery",
    val address: String = "",
    val timeSlot: String? = null,
    val paymentMethod: String = "card_on_delivery",
    val comment: String = "",
    val branches: List<Branch> = emptyList(),
    val selectedBranchId: Int? = null,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val order: Order? = null,
    val cashbackPercent: Float = 0f,
    val saveAddress: Boolean = false,
    val showAddressPrompt: Boolean = false,
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val branchRepository: BranchRepository,
    private val userRepository: UserRepository,
    private val localUserStore: LocalUserStore,
) : ViewModel() {

    var uiState by mutableStateOf(CheckoutUiState())
        private set

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                val branches     = branchRepository.getAll()
                val profile      = userRepository.getCachedLocal()
                val preferredId  = profile?.preferredBranchId
                val cashback     = profile?.level?.cashbackPercent ?: 0f
                val savedAddress = localUserStore.getDeliveryAddress()
                val defaultId    = uiState.selectedBranchId
                    ?: preferredId?.takeIf { id -> branches.any { it.id == id } }
                    ?: branches.firstOrNull()?.id
                uiState = uiState.copy(
                    branches         = branches,
                    selectedBranchId = defaultId,
                    cashbackPercent  = cashback,
                    address          = if (uiState.address.isBlank()) savedAddress else uiState.address,
                )
            } catch (e: Exception) {
                Log.e(TAG, "load: error", e)
            }
        }
    }

    fun setDeliveryType(type: String) { uiState = uiState.copy(deliveryType = type) }
    fun setAddress(address: String)   { uiState = uiState.copy(address = address) }
    fun setTimeSlot(slot: String?)    { uiState = uiState.copy(timeSlot = slot) }
    fun setPaymentMethod(method: String) { uiState = uiState.copy(paymentMethod = method) }
    fun setComment(comment: String)   { uiState = uiState.copy(comment = comment) }
    fun setBranch(branchId: Int)      { uiState = uiState.copy(selectedBranchId = branchId) }
    fun toggleSaveAddress()           { uiState = uiState.copy(saveAddress = !uiState.saveAddress) }
    fun clearError()                  { uiState = uiState.copy(error = null) }

    fun submitOrder(items: List<OrderItemRequest>, useBonuses: Boolean, promoCode: String?) {
        if (items.isEmpty() || uiState.isSubmitting) return
        if (uiState.deliveryType == "delivery" && uiState.address.isBlank()) {
            uiState = uiState.copy(showAddressPrompt = true)
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(isSubmitting = true, error = null)
            try {
                if (uiState.saveAddress && uiState.address.isNotBlank()) {
                    localUserStore.saveDeliveryAddress(uiState.address)
                }
                val order = orderRepository.createOrder(
                    CreateOrderRequest(
                        deliveryType  = uiState.deliveryType,
                        address       = if (uiState.deliveryType == "delivery") uiState.address.ifBlank { null } else null,
                        timeSlot      = uiState.timeSlot,
                        paymentMethod = uiState.paymentMethod,
                        comment       = uiState.comment.ifBlank { null },
                        promoCode     = promoCode,
                        useBonuses    = useBonuses,
                        branchId      = uiState.selectedBranchId,
                        items         = items,
                    )
                )
                uiState = uiState.copy(isSubmitting = false, order = order)
            } catch (e: Exception) {
                Log.e(TAG, "submitOrder: error", e)
                uiState = uiState.copy(isSubmitting = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }

    fun dismissAddressPrompt() { uiState = uiState.copy(showAddressPrompt = false) }

    fun confirmAddressAndSubmit(address: String, items: List<OrderItemRequest>, useBonuses: Boolean, promoCode: String?) {
        localUserStore.saveDeliveryAddress(address)
        uiState = uiState.copy(address = address, showAddressPrompt = false)
        submitOrder(items, useBonuses, promoCode)
    }

    fun reset() {
        uiState = uiState.copy(
            isSubmitting      = false,
            error             = null,
            order             = null,
            showAddressPrompt = false,
            comment           = "",
            timeSlot          = null,
        )
        load()
    }
}
