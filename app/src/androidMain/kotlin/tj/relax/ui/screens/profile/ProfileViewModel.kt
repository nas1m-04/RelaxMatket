package tj.relax.ui.screens.profile

import io.github.aakira.napier.Napier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tj.relax.data.AuthRepository
import tj.relax.data.BranchRepository
import tj.relax.data.MockData
import tj.relax.data.UserProfile
import tj.relax.data.UserRepository
import tj.relax.core.api.ErrorPresenter
import tj.relax.core.api.friendlyErrorMessage
import tj.relax.data.UpdateProfileRequest

private const val TAG = "ProfileViewModel"

class ProfileViewModel(
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
                Napier.e("loadBranchName: error", e, tag = TAG)
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
                Napier.e("load: error", e, tag = TAG)
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
                Napier.e("uploadAvatar: error", e, tag = TAG)
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
                Napier.e("removeAvatar: error", e, tag = TAG)
                uiState = uiState.copy(isUploadingAvatar = false, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }

    fun setPushEnabled(enabled: Boolean) {
        val current = uiState.profile ?: return
        // Optimistic — a settings toggle should feel instant, not wait on a round trip.
        uiState = uiState.copy(profile = current.copy(pushEnabled = enabled))
        viewModelScope.launch {
            try {
                userRepository.updateProfile(UpdateProfileRequest(pushEnabled = enabled))
            } catch (e: Exception) {
                Napier.e("setPushEnabled: error", e, tag = TAG)
                uiState = uiState.copy(profile = current, error = friendlyErrorMessage(e))
                ErrorPresenter.report(e)
            }
        }
    }

    fun consumeError() {
        uiState = uiState.copy(error = null)
    }

    var changePasswordState by mutableStateOf(ChangePasswordUiState())
        private set

    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        if (changePasswordState.isLoading) return

        if (currentPassword.isBlank() || newPassword.isBlank()) {
            changePasswordState = changePasswordState.copy(error = "Заполните все поля")
            return
        }
        if (newPassword.length < 6) {
            changePasswordState = changePasswordState.copy(error = "Новый пароль должен содержать не менее 6 символов")
            return
        }
        if (newPassword != confirmPassword) {
            changePasswordState = changePasswordState.copy(error = "Пароли не совпадают")
            return
        }

        changePasswordState = changePasswordState.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = authRepository.changePassword(currentPassword, newPassword)
            changePasswordState = result.fold(
                onSuccess = { changePasswordState.copy(isLoading = false, success = true) },
                onFailure = { changePasswordState.copy(isLoading = false, error = it.message) },
            )
        }
    }

    fun resetChangePasswordState() {
        changePasswordState = ChangePasswordUiState()
    }
}

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
)
