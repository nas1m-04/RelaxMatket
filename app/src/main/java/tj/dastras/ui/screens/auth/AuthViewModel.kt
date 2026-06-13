package tj.dastras.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.dastras.data.AuthRepository
import tj.dastras.data.toE164PhoneNumber
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun login(phone: String, password: String) {
        if (uiState.isLoading) return

        if (phone.isBlank() || password.isBlank()) {
            uiState = uiState.copy(error = "Введите номер телефона и пароль")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = authRepository.login(toE164PhoneNumber(phone), password)
            uiState = result.fold(
                onSuccess = { uiState.copy(isLoading = false, isSuccess = true) },
                onFailure = { uiState.copy(isLoading = false, error = it.message) },
            )
        }
    }

    fun register(name: String, phone: String, password: String, confirmPassword: String) {
        if (uiState.isLoading) return

        if (name.isBlank() || phone.isBlank() || password.isBlank()) {
            uiState = uiState.copy(error = "Заполните все поля")
            return
        }
        if (password.length < 6) {
            uiState = uiState.copy(error = "Пароль должен содержать не менее 6 символов")
            return
        }
        if (password != confirmPassword) {
            uiState = uiState.copy(error = "Пароли не совпадают")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = authRepository.register(toE164PhoneNumber(phone), password, name)
            uiState = result.fold(
                onSuccess = { uiState.copy(isLoading = false, isSuccess = true) },
                onFailure = { uiState.copy(isLoading = false, error = it.message) },
            )
        }
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }
}
