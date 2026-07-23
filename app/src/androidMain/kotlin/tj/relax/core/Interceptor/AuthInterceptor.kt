package tj.relax.core.Interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import tj.relax.data.TokenManager

private const val TAG = "AuthInterceptor"

class AuthInterceptor(
    private val tokenManager: TokenManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getAccessToken()
        val request = if (!token.isNullOrEmpty()) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            Log.d(TAG, "No access token — sending unauthenticated request to ${chain.request().url}")
            chain.request()
        }

        val response = chain.proceed(request)
        if (response.code == 401) {
            Log.w(TAG, "401 Unauthorized for ${request.url}")
        }
        return response
    }
}
