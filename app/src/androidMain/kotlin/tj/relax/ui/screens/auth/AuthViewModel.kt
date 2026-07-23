package tj.relax.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tj.relax.data.AuthRepository
import tj.relax.data.toE164PhoneNumber

class AuthViewModel(
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

    fun register(
        name: String,
        phone: String,
        password: String,
        confirmPassword: String,
        secretQuestion: String = "",
        secretAnswer: String = "",
    ) {
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
        // Optional pair — but if one is filled in, both need to be, otherwise support staff would
        // have no way to verify the caller against a question with no matching answer.
        if (secretQuestion.isBlank() != secretAnswer.isBlank()) {
            uiState = uiState.copy(error = "Заполните и секретный вопрос, и ответ, либо оставьте оба поля пустыми")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = authRepository.register(
                toE164PhoneNumber(phone),
                password,
                name,
                secretQuestion.trim().takeIf { it.isNotBlank() },
                secretAnswer.trim().takeIf { it.isNotBlank() },
            )
            uiState = result.fold(
                onSuccess = { uiState.copy(isLoading = false, isRegistered = true) },
                onFailure = { uiState.copy(isLoading = false, error = it.message) },
            )
        }
    }
    fun onCongratulationsDone() {
        uiState = uiState.copy(isRegistered = false, isSuccess = true)
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }
}
