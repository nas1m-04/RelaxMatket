package tj.relax.core.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

// Bridges the FCM service (no ViewModel access) to a live LoyaltyViewModel: the backend fires a
// bonus_earned/bonus_spent push right after a cashier consumes the customer's QR, so the app can
// generate a fresh one immediately instead of waiting out its 5-minute expiry.
object LoyaltyPushEvents {
    private val _qrConsumed = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val qrConsumed: SharedFlow<Unit> = _qrConsumed.asSharedFlow()

    fun notifyQrConsumed() {
        _qrConsumed.tryEmit(Unit)
    }
}
