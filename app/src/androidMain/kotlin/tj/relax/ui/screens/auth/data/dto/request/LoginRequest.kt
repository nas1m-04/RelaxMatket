package tj.relax.ui.screens.auth.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val phone: String, val password: String)
