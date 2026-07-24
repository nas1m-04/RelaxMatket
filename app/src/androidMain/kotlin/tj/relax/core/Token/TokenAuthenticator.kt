package tj.relax.core.Token

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import tj.relax.core.api.ApiConfig
import tj.relax.core.api.ApiResponse
import tj.relax.data.SessionManager
import tj.relax.data.TokenManager
import tj.relax.ui.screens.auth.data.dto.request.RefreshRequest
import tj.relax.ui.screens.auth.data.dto.response.AuthResponse

private const val TAG = "TokenAuthenticator"

/**
 * Loads/refreshes the JWT bearer token pair for Ktor's `Auth { bearer { } }` plugin
 * (installed in NetworkModule). Ktor calls [refreshTokens] with a client that has the
 * Auth plugin stripped out, so there's no risk of a recursive auth loop, and Ktor
 * coalesces concurrent refresh attempts for the same client on its own — replacing the
 * old OkHttp Authenticator's manual `synchronized` dedupe.
 */
class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager,
) {
    fun loadTokens(): BearerTokens? {
        val access = tokenManager.getAccessToken() ?: return null
        return BearerTokens(access, tokenManager.getRefreshToken() ?: "")
    }

    suspend fun refreshTokens(refreshClient: HttpClient): BearerTokens? {
        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken.isNullOrEmpty()) {
            Napier.d("No refresh token — treating as guest request", tag = TAG)
            return null
        }
        return try {
            val response = refreshClient.post(ApiConfig.BASE_URL + "auth/refresh") {
                contentType(ContentType.Application.Json)
                setBody(RefreshRequest(refreshToken))
            }
            if (!response.status.isSuccess()) {
                Napier.w("auth/refresh returned ${response.status}", tag = TAG)
                tokenManager.clearTokens()
                sessionManager.notifySessionExpired()
                return null
            }
            val body: ApiResponse<AuthResponse> = response.body()
            val auth = body.data
            if (body.success && auth != null) {
                tokenManager.saveTokens(auth.accessToken, auth.refreshToken)
                Napier.i("Token refreshed successfully", tag = TAG)
                BearerTokens(auth.accessToken, auth.refreshToken)
            } else {
                Napier.w("auth/refresh responded with success=false: ${body.error}", tag = TAG)
                tokenManager.clearTokens()
                sessionManager.notifySessionExpired()
                null
            }
        } catch (e: Exception) {
            Napier.e("auth/refresh request failed", e, tag = TAG)
            null
        }
    }
}
