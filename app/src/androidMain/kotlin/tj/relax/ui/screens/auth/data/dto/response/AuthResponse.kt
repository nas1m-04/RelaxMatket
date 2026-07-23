package tj.relax.ui.screens.auth.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tj.relax.data.UserProfile

@Serializable
data class AuthResponse(
    @SerialName("access_token")  val accessToken: String = "",
    @SerialName("refresh_token") val refreshToken: String = "",
    val user: UserProfile? = null,
)
