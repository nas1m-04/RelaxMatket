package tj.relax.ui.screens.auth.data.dto.request

import com.google.gson.annotations.SerializedName

data class RefreshRequest(
    @SerializedName("refresh_token") val refreshToken: String,
)
