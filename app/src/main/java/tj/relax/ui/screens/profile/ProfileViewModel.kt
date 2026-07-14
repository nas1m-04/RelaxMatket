package tj.relax.ui.screens.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.relax.data.AuthRepository
import tj.relax.data.BranchRepository
import tj.relax.data.MockData
import tj.relax.data.UserProfile
import tj.relax.data.UserRepository
import tj.relax.core.api.ErrorPresenter
import tj.relax.core.api.friendlyErrorMessage
import tj.relax.data.UpdateProfileRequest
import javax.inject.Inject

private const val TAG = "ProfileViewModel"

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val branchRepository: BranchRepository,
) : ViewModel() {

    var uiState by mutableStateOf(
        ProfileUiState(profile = if (authRepository.isLoggedIn) userRepository.getCachedLocal() else null)
    )
        private set

    init {
        load()
        loadBranchName()
    }

    private fun loadBranchName() {
        viewModelScope.launch {
            try {
                val branchId = uiState.profile?.preferredBranchId ?: return@launch
                val branch = branchRepository.getAll().firstOrNull { it.id == branchId }
                uiState = uiState.copy(branchName = branch?.name)
            } catch (e: Exception) {
                Log.e(TAG, "loadBranchName: error", e)
            }
        }
    }

    fun load(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = uiState.profile == null, error = null)
            try {
                val profile = if (authRepository.isLoggedIn) userRepository.getOrCreate()
                              else MockData.currentUser
                uiState = uiState.copy(profile = profile, isLoading = false)
                loadBranchName()
            } catch (e: Exception) {
                Log.e(TAG, "load: error", e)
                uiState = uiState.copy(isLoading = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }

    fun logout() {
        authRepository.logout()
        uiState = uiState.copy(loggedOut = true)
    }

    fun uploadAvatar(bytes: ByteArray, mimeType: String = "image/jpeg") {
        if (!authRepository.isLoggedIn) return
        viewModelScope.launch {
            uiState = uiState.copy(isUploadingAvatar = true, error = null)
            try {
                val profile = userRepository.uploadAvatar(bytes, mimeType)
                uiState = uiState.copy(profile = profile, isUploadingAvatar = false)
            } catch (e: Exception) {
                Log.e(TAG, "uploadAvatar: error", e)
                uiState = uiState.copy(isUploadingAvatar = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val updated = userRepository.updateProfile(UpdateProfileRequest(name = name, email = email))
                uiState = uiState.copy(profile = updated, isLoading = false)
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }
    fun removeAvatar() {
        if (!authRepository.isLoggedIn) return
        viewModelScope.launch {
            uiState = uiState.copy(isUploadingAvatar = true, error = null)
            try {
                val profile = userRepository.removeAvatar()
                uiState = uiState.copy(profile = profile, isUploadingAvatar = false)
            } catch (e: Exception) {
                Log.e(TAG, "removeAvatar: error", e)
                uiState = uiState.copy(isUploadingAvatar = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }

    fun consumeError() {
        uiState = uiState.copy(error = null)
    }
}
