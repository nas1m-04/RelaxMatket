package tj.relax.core.Token

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Authenticator
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import tj.relax.core.api.ApiConfig
import tj.relax.core.api.ApiResponse
import tj.relax.ui.screens.auth.data.dto.request.RefreshRequest
import tj.relax.ui.screens.auth.data.dto.response.AuthResponse
import tj.relax.data.SessionManager
import tj.relax.data.TokenManager
import javax.inject.Inject

private const val TAG = "TokenAuthenticator"

/**
 * Refreshes the JWT access token via `auth/refresh` when a request fails with 401.
 * Uses a plain OkHttpClient (without this authenticator) to avoid recursive auth loops.
 *
 * If the user had a session (a refresh token was stored) but it could no longer be
 * renewed, the session is cleared and [SessionManager] notifies the UI to return to login.
 */
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager,
) : Authenticator {

    private val refreshClient = OkHttpClient()
    private val gson = Gson()

    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = tokenManager.getRefreshToken()
        // No session at all (guest request) — a 401 here is expected, not a session expiry.
        if (refreshToken.isNullOrEmpty()) {
            Log.d(TAG, "401 for ${response.request.url} but no session — treating as guest request")
            return null
        }

        if (responseCount(response) >= 2) {
            // Refresh already happened for this request chain and the new token still
            // failed — the session is no longer valid.
            Log.w(TAG, "Refreshed token still rejected for ${response.request.url} — ending session")
            tokenManager.clearTokens()
            sessionManager.notifySessionExpired()
            return null
        }

        Log.d(TAG, "401 for ${response.request.url} — attempting token refresh")
        val newAccessToken = synchronized(this) {
            // Another thread may have already refreshed the token while we were waiting.
            val currentAccessToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            val latestAccessToken = tokenManager.getAccessToken()
            if (!latestAccessToken.isNullOrEmpty() && latestAccessToken != currentAccessToken) {
                Log.d(TAG, "Token was already refreshed by another request")
                latestAccessToken
            } else {
                requestNewAccessToken(refreshToken)
            }
        }

        if (newAccessToken == null) {
            Log.w(TAG, "Token refresh failed — ending session")
            sessionManager.notifySessionExpired()
            return null
        }

        Log.i(TAG, "Token refreshed successfully")
        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
    }

    private fun requestNewAccessToken(refreshToken: String): String? {
        val requestBody = gson.toJson(RefreshRequest(refreshToken))
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("${ApiConfig.BASE_URL}auth/refresh")
            .post(requestBody)
            .build()

        return try {
            refreshClient.newCall(request).execute().use { refreshResponse ->
                if (!refreshResponse.isSuccessful) {
                    Log.w(TAG, "auth/refresh returned ${refreshResponse.code}")
                    tokenManager.clearTokens()
                    return null
                }
                val type = object : TypeToken<ApiResponse<AuthResponse>>() {}.type
                val body: ApiResponse<AuthResponse> = gson.fromJson(refreshResponse.body?.charStream(), type)
                val auth = body.data
                if (body.success && auth != null) {
                    tokenManager.saveTokens(auth.accessToken, auth.refreshToken)
                    auth.accessToken
                } else {
                    Log.w(TAG, "auth/refresh responded with success=false: ${body.error}")
                    tokenManager.clearTokens()
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "auth/refresh request failed", e)
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
