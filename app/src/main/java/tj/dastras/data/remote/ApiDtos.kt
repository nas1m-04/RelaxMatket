package tj.dastras.data.remote

import com.google.gson.annotations.SerializedName
import tj.dastras.data.Product
import tj.dastras.data.UserProfile

// ── Auth ────────────────────────────────────────────────────────────────────

data class RegisterRequest(
    val phone: String,
    val password: String,
    val name: String,
)

data class LoginRequest(
    val phone: String,
    val password: String,
)

data class RefreshRequest(
    @SerializedName("refresh_token") val refreshToken: String,
)

data class AuthResponse(
    @SerializedName("access_token")  val accessToken: String = "",
    @SerializedName("refresh_token") val refreshToken: String = "",
    val user: UserProfile? = null,
)

data class CartItemResponse(
    val id: Int = 0,
    val userUid: String = "",
    val productId: Int = 0,
    val quantity: Int = 1,
    val product: Product? = null,
)

data class OrderApiResponse(
    val id: String = "",
    val userUid: String = "",
    val total: Double = 0.0,
    val status: String = "PROCESSING",
    val address: String? = null,
    val createdAt: String = "",
    val items: List<OrderItemApiResponse> = emptyList(),
)

data class OrderItemApiResponse(
    val id: Int = 0,
    val orderId: String = "",
    val productId: Int = 0,
    val quantity: Int = 1,
    val price: Double = 0.0,
    val product: Product? = null,
)
