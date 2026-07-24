package tj.relax.ui.screens.checkout

import io.github.aakira.napier.Napier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tj.relax.data.Branch
import tj.relax.data.BranchRepository
import tj.relax.data.CreateOrderRequest
import tj.relax.data.LocalUserStore
import tj.relax.data.Order
import tj.relax.data.OrderItemRequest
import tj.relax.data.LoyaltyRepository
import tj.relax.data.OrderRepository
import tj.relax.data.UserRepository
import tj.relax.core.api.ErrorPresenter
import tj.relax.core.api.friendlyErrorMessage

private const val TAG = "CheckoutViewModel"

class CheckoutViewModel(
    private val orderRepository: OrderRepository,
    private val branchRepository: BranchRepository,
    private val userRepository: UserRepository,
    private val localUserStore: LocalUserStore,
    private val loyaltyRepository: LoyaltyRepository,
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
                Napier.e("load: error", e, tag = TAG)
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
                loyaltyRepository.invalidateCache() // balance changed after purchase
                uiState = uiState.copy(isSubmitting = false, order = order)
            } catch (e: Exception) {
                Napier.e("submitOrder: error", e, tag = TAG)
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
