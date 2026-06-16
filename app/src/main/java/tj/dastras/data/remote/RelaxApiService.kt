package tj.dastras.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import tj.dastras.data.*

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
    ): Response<ApiResponse<List<Product>>>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<ApiResponse<Product>>

    @GET("products/new")
    suspend fun getNewProducts(): Response<ApiResponse<List<Product>>>

    @GET("products/sale")
    suspend fun getSaleProducts(): Response<ApiResponse<List<Product>>>

    // ── Categories (public) ───────────────────────────────────────────────
    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<List<Category>>>

    // ── Banners (public) ──────────────────────────────────────────────────
    @GET("banners")
    suspend fun getBanners(): Response<ApiResponse<List<Banner>>>

    // ── Branches (public) ────────────────────────────────────────────────
    @GET("branches")
    suspend fun getBranches(): Response<ApiResponse<List<Branch>>>

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
    suspend fun getOrders(): Response<ApiResponse<List<OrderApiResponse>>>

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<ApiResponse<OrderApiResponse>>

    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: String): Response<ApiResponse<OrderApiResponse>>

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

    // ── Favorites (auth required) ─────────────────────────────────────────
    @GET("favorites")
    suspend fun getFavorites(): Response<ApiResponse<List<Product>>>

    @POST("favorites/{productId}")
    suspend fun addFavorite(@Path("productId") productId: Int): Response<ApiResponse<Unit>>

    @DELETE("favorites/{productId}")
    suspend fun removeFavorite(@Path("productId") productId: Int): Response<ApiResponse<Unit>>
}
