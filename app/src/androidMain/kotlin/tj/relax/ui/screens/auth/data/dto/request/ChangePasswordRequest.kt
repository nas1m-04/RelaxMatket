package tj.relax.ui.screens.auth.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(val currentPassword: String, val newPassword: String)
