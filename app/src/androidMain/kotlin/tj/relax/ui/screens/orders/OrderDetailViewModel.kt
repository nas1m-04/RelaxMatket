package tj.relax.ui.screens.orders

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.relax.core.api.ErrorPresenter
import tj.relax.core.api.friendlyErrorMessage
import tj.relax.data.AuthRepository
import tj.relax.data.MockData
import tj.relax.data.OrderRepository
import javax.inject.Inject

private const val TAG = "OrderDetailViewModel"

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val orderId: String = checkNotNull(savedStateHandle["id"])

    var uiState by mutableStateOf(OrderDetailUiState())
        private set

    init { load() }

    fun load() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val order = if (authRepository.isLoggedIn) orderRepository.getById(orderId)
                            else MockData.orders.firstOrNull { it.id == orderId }
                uiState = uiState.copy(order = order, isLoading = false)
            } catch (e: Exception) {
                Log.e(TAG, "load: error", e)
                uiState = uiState.copy(isLoading = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }
}
