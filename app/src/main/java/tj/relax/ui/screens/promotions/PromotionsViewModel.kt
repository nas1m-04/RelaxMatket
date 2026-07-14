package tj.relax.ui.screens.promotions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import tj.relax.core.api.friendlyErrorMessage
import tj.relax.data.ProductRepository
import tj.relax.data.PromotionsRepository
import javax.inject.Inject

@HiltViewModel
class PromotionsViewModel @Inject constructor(
    private val repository: PromotionsRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {

    var uiState by mutableStateOf(PromotionsUiState())
        private set

    init { load() }

    fun load() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val promotionsDeferred   = async { repository.getPromotions() }
                val saleProductsDeferred = async { productRepository.getSale() }

                uiState = uiState.copy(
                    promotions   = promotionsDeferred.await(),
                    saleProducts = saleProductsDeferred.await(),
                    isLoading    = false,
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = friendlyErrorMessage(e))
            }
        }
    }
}
