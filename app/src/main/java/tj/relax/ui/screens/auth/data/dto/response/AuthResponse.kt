package tj.relax.ui.screens.auth.data.dto.response

import com.google.gson.annotations.SerializedName
import tj.relax.data.UserProfile

data class AuthResponse(
    @SerializedName("access_token")  val accessToken: String = "",
    @SerializedName("refresh_token") val refreshToken: String = "",
    val user: UserProfile? = null,
)
