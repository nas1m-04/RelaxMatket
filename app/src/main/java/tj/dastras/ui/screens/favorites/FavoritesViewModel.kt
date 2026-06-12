package tj.dastras.ui.screens.favorites

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.dastras.data.AuthRepository
import tj.dastras.data.FavoritesRepository
import tj.dastras.data.Product
import tj.dastras.data.remote.friendlyErrorMessage
import javax.inject.Inject

private const val TAG = "FavoritesViewModel"

data class FavoritesUiState(
    val favorites: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    var uiState by mutableStateOf(FavoritesUiState())
        private set

    init { load() }

    fun load() {
        if (!authRepository.isLoggedIn) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val favorites = favoritesRepository.getFavorites()
                uiState = uiState.copy(favorites = favorites, isLoading = false)
            } catch (e: Exception) {
                Log.e(TAG, "load: error", e)
                uiState = uiState.copy(isLoading = false, error = friendlyErrorMessage(e))
            }
        }
    }

    fun isFavorite(productId: Int): Boolean =
        uiState.favorites.any { it.id == productId }

    fun toggle(product: Product) {
        if (!authRepository.isLoggedIn) return
        val isFav = uiState.favorites.any { it.id == product.id }
        if (isFav) {
            uiState = uiState.copy(favorites = uiState.favorites.filter { it.id != product.id })
            viewModelScope.launch { favoritesRepository.remove(product.id) }
        } else {
            uiState = uiState.copy(favorites = uiState.favorites + product)
            viewModelScope.launch { favoritesRepository.add(product.id) }
        }
    }
}
