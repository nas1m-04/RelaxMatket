package tj.relax.ui.screens.favorites

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tj.relax.data.AuthRepository
import tj.relax.data.FavoritesRepository
import tj.relax.data.Product
import tj.relax.core.api.ErrorPresenter
import tj.relax.core.api.friendlyErrorMessage

private const val TAG = "FavoritesViewModel"

class FavoritesViewModel(
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
                ErrorPresenter.report(e)
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
            viewModelScope.launch {
                try {
                    favoritesRepository.remove(product.id)
                } catch (e: Exception) {
                    Log.e(TAG, "toggle: remove error", e)
                    ErrorPresenter.report(e)
                }
            }
        } else {
            uiState = uiState.copy(favorites = uiState.favorites + product)
            viewModelScope.launch {
                try {
                    favoritesRepository.add(product.id)
                } catch (e: Exception) {
                    Log.e(TAG, "toggle: add error", e)
                    ErrorPresenter.report(e)
                }
            }
        }
    }
}
