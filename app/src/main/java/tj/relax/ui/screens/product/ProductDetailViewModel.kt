package tj.relax.ui.screens.product

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.relax.data.Category
import tj.relax.data.CategoryRepository
import tj.relax.data.Product
import tj.relax.data.ProductRepository
import tj.relax.core.api.ErrorPresenter
import tj.relax.core.api.friendlyErrorMessage
import javax.inject.Inject

private const val TAG = "ProductDetailViewModel"

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val productId: Int = checkNotNull(savedStateHandle["id"])

    var uiState by mutableStateOf(ProductDetailUiState())
        private set

    init { load() }

    private fun load() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val product    = productRepository.getById(productId)
                val categories = categoryRepository.getAll()
                val category   = categories.find { it.id == product.categoryId }
                uiState = uiState.copy(product = product, category = category, isLoading = false)
            } catch (e: Exception) {
                Log.e(TAG, "load: error", e)
                uiState = uiState.copy(isLoading = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }
}
