package tj.relax.ui.screens.checkout

import tj.relax.data.Branch
import tj.relax.data.Order

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
