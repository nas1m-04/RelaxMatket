package tj.relax.core.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import tj.relax.data.AddToCartRequest
import tj.relax.data.Banner
import tj.relax.data.Branch
import tj.relax.data.Category
import tj.relax.data.CreateOrderRequest
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

interface RelaxApiService {

    // ── Auth (public) ────────────────────────────────────────────────────
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): Response<ApiResponse<AuthResponse>>

    // ── Products (public) ─────────────────────────────────────────────────
    @GET("products")
    suspend fun getProducts(
        @Query("category_id") categoryId: Int? = null,
        @Query("search")      search: String? = null,
        @Query("sort")        sort: String? = null,
        @Query("page")        page: Int = 1,
        @Query("pageSize")    pageSize: Int = 20,
    ): Response<ApiResponse<PagedResponse<Product>>>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<ApiResponse<Product>>

    @GET("products/new")
    suspend fun getNewProducts(): Response<ApiResponse<List<Product>>>

    @GET("products/popular")
    suspend fun getPopularProducts(): Response<ApiResponse<List<Product>>>

    @GET("products/sale")
    suspend fun getSaleProducts(): Response<ApiResponse<List<Product>>>

    // ── Categories (public) ───────────────────────────────────────────────
    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<List<Category>>>

    // ── Branches (public) ─────────────────────────────────────────────────
    @GET("branches")
    suspend fun getBranches(): Response<ApiResponse<List<Branch>>>

    // ── Banners (public) ──────────────────────────────────────────────────
    @GET("banners")
    suspend fun getBanners(): Response<ApiResponse<List<Banner>>>

    // ── Cart (auth required) ──────────────────────────────────────────────
    @GET("cart")
    suspend fun getCart(): Response<ApiResponse<List<CartItemResponse>>>

    @POST("cart")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<ApiResponse<Unit>>

    @DELETE("cart/{productId}")
    suspend fun removeFromCart(@Path("productId") productId: Int): Response<ApiResponse<Unit>>

    @DELETE("cart")
    suspend fun clearCart(): Response<ApiResponse<Unit>>

    // ── Orders (auth required) ────────────────────────────────────────────
    @GET("orders")
    suspend fun getOrders(): Response<ApiResponse<List<OrderResponse>>>

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<ApiResponse<OrderResponse>>

    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: String): Response<ApiResponse<OrderResponse>>

    // ── Profile (auth required) ───────────────────────────────────────────
    @GET("profile")
    suspend fun getProfile(): Response<ApiResponse<UserProfile>>

    @PATCH("profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<UserProfile>>

    @Multipart
    @POST("profile/avatar")
    suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): Response<ApiResponse<UserProfile>>

    @DELETE("profile/avatar")
    suspend fun deleteAvatar(): Response<ApiResponse<UserProfile>>

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse<Unit>>

    // ── Favorites (auth required) ─────────────────────────────────────────
    @GET("favorites")
    suspend fun getFavorites(): Response<ApiResponse<List<Product>>>

    @POST("favorites/{productId}")
    suspend fun addFavorite(@Path("productId") productId: Int): Response<ApiResponse<Unit>>

    @DELETE("favorites/{productId}")
    suspend fun removeFavorite(@Path("productId") productId: Int): Response<ApiResponse<Unit>>

    // ── Loyalty (auth required) ───────────────────────────────────────────
    @GET("loyalty")
    suspend fun getLoyaltySummary(): Response<ApiResponse<LoyaltySummaryResponse>>

    @GET("loyalty/levels")
    suspend fun getLoyaltyLevels(): Response<ApiResponse<List<LoyaltyLevelResponse>>>

    @GET("loyalty/transactions")
    suspend fun getBonusTransactions(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
    ): Response<ApiResponse<PagedResponse<BonusTransactionApiResponse>>>

    @GET("loyalty/achievements")
    suspend fun getAchievements(): Response<ApiResponse<List<AchievementApiResponse>>>

    @GET("loyalty/qr-token")
    suspend fun getQrToken(): Response<ApiResponse<QrTokenResponse>>

    @POST("profile/fcm-token")
    suspend fun updateFcmToken(@Body request: UpdateFcmTokenRequest): Response<ApiResponse<Unit>>
    data class UpdateFcmTokenRequest(val fcmToken: String)

    // ── Promotions (public) ───────────────────────────────────────────────────
    @GET("promotions")
    suspend fun getPromotions(): Response<ApiResponse<List<PromotionResponse>>>

    // ── Notifications (auth required) ─────────────────────────────────────────
    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50,
    ): Response<ApiResponse<List<NotificationResponse>>>

    @POST("notifications/mark-all-read")
    suspend fun markAllNotificationsRead(): Response<ApiResponse<Unit>>

    @GET("notifications/in-app")
    suspend fun getInAppNotification(): Response<ApiResponse<NotificationResponse>>

    @POST("notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @GET("notifications/unread-count")
    suspend fun getUnreadNotificationsCount(): Response<ApiResponse<UnreadCountResponse>>
}
