package tj.relax.ui.screens.auth.data.dto.request

data class RegisterRequest(
    val phone: String,
    val password: String,
    val name: String,
    val secretQuestion: String? = null,
    val secretAnswer: String? = null,
)
