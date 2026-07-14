package tj.relax.ui.screens.auth

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isRegistered: Boolean = false,
    val registeredName: String = "",
    val error: String? = null,
)
