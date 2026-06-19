package tj.dastras.ui.screens.product

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.dastras.data.Category
import tj.dastras.data.CategoryRepository
import tj.dastras.data.Product
import tj.dastras.data.ProductRepository
import tj.dastras.core.api.ErrorPresenter
import tj.dastras.core.api.friendlyErrorMessage
import javax.inject.Inject

private const val TAG = "ProductDetailViewModel"

data class ProductDetailUiState(
    val product: Product? = null,
    val category: Category? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

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
