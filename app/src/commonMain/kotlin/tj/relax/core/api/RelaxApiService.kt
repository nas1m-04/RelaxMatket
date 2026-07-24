package tj.relax.core.api

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import tj.relax.data.AddToCartRequest
import tj.relax.data.Banner
import tj.relax.data.Branch
import tj.relax.data.Category
import tj.relax.data.CrashReportRequest
import tj.relax.data.CreateOrderRequest
import tj.relax.data.CreateSupportTicketRequest
import tj.relax.data.SupportTicket
import tj.relax.data.Product
import tj.relax.data.UpdateProfileRequest
import tj.relax.data.UserProfile
import tj.relax.ui.screens.auth.data.dto.request.ChangePasswordRequest
import tj.relax.ui.screens.auth.data.dto.request.LoginRequest
import tj.relax.ui.screens.auth.data.dto.request.RefreshRequest
import tj.relax.ui.screens.auth.data.dto.request.RegisterRequest
import tj.relax.ui.screens.auth.data.dto.response.AuthResponse
import tj.relax.ui.screens.cart.data.dto.response.CartItemResponse
import tj.relax.ui.screens.notifications.data.dto.response.NotificationResponse
import tj.relax.ui.screens.notifications.data.dto.response.UnreadCountResponse
import tj.relax.ui.screens.orders.data.dto.response.OrderResponse
import tj.relax.ui.screens.promotions.data.dto.response.PromotionResponse

private val json = Json { ignoreUnknownKeys = true }

class RelaxApiService(private val client: HttpClient) {

    @Serializable
    data class UpdateFcmTokenRequest(val fcmToken: String)

    private suspend inline fun <reified T> call(
        method: HttpMethod,
        path: String,
        query: Map<String, Any?> = emptyMap(),
        requestBody: Any? = null,
    ): ApiHttpResponse<ApiResponse<T>> {
        val response = client.request(ApiConfig.BASE_URL + path) {
            this.method = method
            query.forEach { (key, value) -> if (value != null) parameter(key, value) }
            if (requestBody != null) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        }
        return response.toApiHttpResponse()
    }

    private suspend inline fun <reified T> HttpResponse.toApiHttpResponse(): ApiHttpResponse<ApiResponse<T>> {
        val text = bodyAsText()
        val parsed = if (text.isNotBlank()) {
            try { json.decodeFromString<ApiResponse<T>>(text) } catch (e: Exception) { null }
        } else null
        return ApiHttpResponse(
            isSuccessful = status.isSuccess(),
            code = status.value,
            body = parsed,
            errorBody = if (!status.isSuccess()) text else null,
        )
    }

    // ── Auth (public) ────────────────────────────────────────────────────
    suspend fun register(request: RegisterRequest): ApiHttpResponse<ApiResponse<AuthResponse>> =
        call(HttpMethod.Post, "auth/register", requestBody = request)

    suspend fun login(request: LoginRequest): ApiHttpResponse<ApiResponse<AuthResponse>> =
        call(HttpMethod.Post, "auth/login", requestBody = request)

    suspend fun refresh(request: RefreshRequest): ApiHttpResponse<ApiResponse<AuthResponse>> =
        call(HttpMethod.Post, "auth/refresh", requestBody = request)

    // ── Products (public) ─────────────────────────────────────────────────
    suspend fun getProducts(
        categoryId: Int? = null,
        search: String? = null,
        sort: String? = null,
        page: Int = 1,
        pageSize: Int = 20,
    ): ApiHttpResponse<ApiResponse<PagedResponse<Product>>> = call(
        HttpMethod.Get, "products",
        query = mapOf("category_id" to categoryId, "search" to search, "sort" to sort, "page" to page, "pageSize" to pageSize),
    )

    suspend fun getProduct(id: Int): ApiHttpResponse<ApiResponse<Product>> =
        call(HttpMethod.Get, "products/$id")

    suspend fun getNewProducts(): ApiHttpResponse<ApiResponse<List<Product>>> =
        call(HttpMethod.Get, "products/new")

    suspend fun getPopularProducts(): ApiHttpResponse<ApiResponse<List<Product>>> =
        call(HttpMethod.Get, "products/popular")

    suspend fun getSaleProducts(): ApiHttpResponse<ApiResponse<List<Product>>> =
        call(HttpMethod.Get, "products/sale")

    // ── Categories (public) ───────────────────────────────────────────────
    suspend fun getCategories(): ApiHttpResponse<ApiResponse<List<Category>>> =
        call(HttpMethod.Get, "categories")

    // ── Branches (public) ─────────────────────────────────────────────────
    suspend fun getBranches(): ApiHttpResponse<ApiResponse<List<Branch>>> =
        call(HttpMethod.Get, "branches")

    // ── Banners (public) ──────────────────────────────────────────────────
    suspend fun getBanners(): ApiHttpResponse<ApiResponse<List<Banner>>> =
        call(HttpMethod.Get, "banners")

    // ── Cart (auth required) ──────────────────────────────────────────────
    suspend fun getCart(): ApiHttpResponse<ApiResponse<List<CartItemResponse>>> =
        call(HttpMethod.Get, "cart")

    suspend fun addToCart(request: AddToCartRequest): ApiHttpResponse<ApiResponse<Unit>> =
        call(HttpMethod.Post, "cart", requestBody = request)

    suspend fun removeFromCart(productId: Int): ApiHttpResponse<ApiResponse<Unit>> =
        call(HttpMethod.Delete, "cart/$productId")

    suspend fun clearCart(): ApiHttpResponse<ApiResponse<Unit>> =
        call(HttpMethod.Delete, "cart")

    // ── Crash reports (no auth — a crash can happen with an expired/missing token) ────────
    suspend fun reportCrash(request: CrashReportRequest): ApiHttpResponse<ApiResponse<Unit>> =
        call(HttpMethod.Post, "crash-reports", requestBody = request)

    // ── Support tickets (auth required) ───────────────────────────────────
    suspend fun createSupportTicket(request: CreateSupportTicketRequest): ApiHttpResponse<ApiResponse<SupportTicket>> =
        call(HttpMethod.Post, "support", requestBody = request)

    suspend fun getSupportTickets(page: Int = 1, pageSize: Int = 20): ApiHttpResponse<ApiResponse<PagedResponse<SupportTicket>>> =
        call(HttpMethod.Get, "support", query = mapOf("page" to page, "pageSize" to pageSize))

    // ── Orders (auth required) ────────────────────────────────────────────
    suspend fun getOrders(page: Int = 1, pageSize: Int = 20): ApiHttpResponse<ApiResponse<PagedResponse<OrderResponse>>> =
        call(HttpMethod.Get, "orders", query = mapOf("page" to page, "pageSize" to pageSize))

    suspend fun createOrder(request: CreateOrderRequest): ApiHttpResponse<ApiResponse<OrderResponse>> =
        call(HttpMethod.Post, "orders", requestBody = request)

    suspend fun getOrder(id: String): ApiHttpResponse<ApiResponse<OrderResponse>> =
        call(HttpMethod.Get, "orders/$id")

    // ── Profile (auth required) ───────────────────────────────────────────
    suspend fun getProfile(): ApiHttpResponse<ApiResponse<UserProfile>> =
        call(HttpMethod.Get, "profile")

    suspend fun updateProfile(request: UpdateProfileRequest): ApiHttpResponse<ApiResponse<UserProfile>> =
        call(HttpMethod.Patch, "profile", requestBody = request)

    suspend fun uploadAvatar(bytes: ByteArray, filename: String, contentType: String): ApiHttpResponse<ApiResponse<UserProfile>> {
        val response = client.request(ApiConfig.BASE_URL + "profile/avatar") {
            method = HttpMethod.Post
            setBody(MultiPartFormDataContent(formData {
                append("avatar", bytes, io.ktor.http.Headers.build {
                    append(HttpHeaders.ContentType, contentType)
                    append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                })
            }))
        }
        return response.toApiHttpResponse()
    }

    suspend fun deleteAvatar(): ApiHttpResponse<ApiResponse<UserProfile>> =
        call(HttpMethod.Delete, "profile/avatar")

    suspend fun changePassword(request: ChangePasswordRequest): ApiHttpResponse<ApiResponse<Unit>> =
        call(HttpMethod.Post, "auth/change-password", requestBody = request)

    // ── Favorites (auth required) ─────────────────────────────────────────
    suspend fun getFavorites(): ApiHttpResponse<ApiResponse<List<Product>>> =
        call(HttpMethod.Get, "favorites")

    suspend fun addFavorite(productId: Int): ApiHttpResponse<ApiResponse<Unit>> =
        call(HttpMethod.Post, "favorites/$productId")

    suspend fun removeFavorite(productId: Int): ApiHttpResponse<ApiResponse<Unit>> =
        call(HttpMethod.Delete, "favorites/$productId")

    // ── Loyalty (auth required) ───────────────────────────────────────────
    suspend fun getLoyaltySummary(): ApiHttpResponse<ApiResponse<LoyaltySummaryResponse>> =
        call(HttpMethod.Get, "loyalty")

    suspend fun getLoyaltyLevels(): ApiHttpResponse<ApiResponse<List<LoyaltyLevelResponse>>> =
        call(HttpMethod.Get, "loyalty/levels")

    suspend fun getBonusTransactions(page: Int = 1, pageSize: Int = 20): ApiHttpResponse<ApiResponse<PagedResponse<BonusTransactionApiResponse>>> =
        call(HttpMethod.Get, "loyalty/transactions", query = mapOf("page" to page, "pageSize" to pageSize))

    suspend fun getAchievements(): ApiHttpResponse<ApiResponse<List<AchievementApiResponse>>> =
        call(HttpMethod.Get, "loyalty/achievements")

    suspend fun getQrToken(): ApiHttpResponse<ApiResponse<QrTokenResponse>> =
        call(HttpMethod.Get, "loyalty/qr-token")

    suspend fun updateFcmToken(request: UpdateFcmTokenRequest): ApiHttpResponse<ApiResponse<Unit>> =
        call(HttpMethod.Post, "profile/fcm-token", requestBody = request)

    // ── Promotions (public) ───────────────────────────────────────────────────
    suspend fun getPromotions(): ApiHttpResponse<ApiResponse<List<PromotionResponse>>> =
        call(HttpMethod.Get, "promotions")

    // ── Notifications (auth required) ─────────────────────────────────────────
    suspend fun getNotifications(page: Int = 1, pageSize: Int = 50): ApiHttpResponse<ApiResponse<List<NotificationResponse>>> =
        call(HttpMethod.Get, "notifications", query = mapOf("page" to page, "pageSize" to pageSize))

    suspend fun markAllNotificationsRead(): ApiHttpResponse<ApiResponse<Unit>> =
        call(HttpMethod.Post, "notifications/mark-all-read")

    suspend fun getInAppNotification(): ApiHttpResponse<ApiResponse<NotificationResponse>> =
        call(HttpMethod.Get, "notifications/in-app")

    suspend fun markNotificationRead(id: Int): ApiHttpResponse<ApiResponse<Unit>> =
        call(HttpMethod.Post, "notifications/$id/read")

    suspend fun getUnreadNotificationsCount(): ApiHttpResponse<ApiResponse<UnreadCountResponse>> =
        call(HttpMethod.Get, "notifications/unread-count")
}
