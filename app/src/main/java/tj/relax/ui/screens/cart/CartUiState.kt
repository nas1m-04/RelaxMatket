package tj.relax.ui.screens.cart

import tj.relax.data.CartItem

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
