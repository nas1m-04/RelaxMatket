package tj.dastras.ui.screens.branch

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.dastras.data.Branch
import tj.dastras.data.BranchRepository
import tj.dastras.data.UpdateProfileRequest
import tj.dastras.data.UserRepository
import tj.dastras.data.remote.ErrorPresenter
import tj.dastras.data.remote.friendlyErrorMessage
import javax.inject.Inject

private const val TAG = "SelectBranchViewModel"

data class SelectBranchUiState(
    val isLoading: Boolean = true,
    val branches: List<Branch> = emptyList(),
    val selectedBranchId: Int? = null,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
)

@HiltViewModel
class SelectBranchViewModel @Inject constructor(
    private val branchRepository: BranchRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    var uiState by mutableStateOf(SelectBranchUiState())
        private set

    init {
        viewModelScope.launch {
            try {
                val branches = branchRepository.getAll()
                val preferredId = userRepository.getCachedLocal()?.preferredBranchId
                    ?: userRepository.getOrCreate().preferredBranchId
                val selected = preferredId?.takeIf { id -> branches.any { it.id == id } }
                    ?: branches.firstOrNull()?.id
                uiState = uiState.copy(isLoading = false, branches = branches, selectedBranchId = selected)
            } catch (e: Exception) {
                Log.e(TAG, "load: error", e)
                uiState = uiState.copy(isLoading = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }

    fun selectBranch(id: Int) {
        uiState = uiState.copy(selectedBranchId = id)
    }

    fun confirm() {
        val branchId = uiState.selectedBranchId ?: return
        if (uiState.isSaving) return
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null)
            try {
                userRepository.updateProfile(UpdateProfileRequest(preferredBranchId = branchId))
                uiState = uiState.copy(isSaving = false, saved = true)
            } catch (e: Exception) {
                Log.e(TAG, "confirm: error", e)
                uiState = uiState.copy(isSaving = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }
}
