package tj.dastras.ui.screens.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tj.dastras.data.Product
import tj.dastras.data.ProductRepository
import tj.dastras.core.api.ErrorPresenter
import tj.dastras.core.api.friendlyErrorMessage
import javax.inject.Inject

private const val TAG = "SearchViewModel"
private const val SEARCH_DEBOUNCE_MS = 400L

data class SearchUiState(
    val query: String = "",
    val results: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val hasSearched: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {

    var uiState by mutableStateOf(SearchUiState())
        private set

    private var searchJob: Job? = null

    fun setQuery(query: String) {
        uiState = uiState.copy(query = query)
        searchJob?.cancel()

        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            uiState = uiState.copy(results = emptyList(), isLoading = false, hasSearched = false, error = null)
            return
        }

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val results = productRepository.search(trimmed)
                uiState = uiState.copy(results = results, isLoading = false, hasSearched = true)
            } catch (e: Exception) {
                Log.e(TAG, "setQuery: search error", e)
                uiState = uiState.copy(isLoading = false, hasSearched = true, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }
}
