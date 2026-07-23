package tj.relax.data

import android.content.Context
import androidx.core.content.edit

/** Stores the JWT access/refresh tokens issued by the backend on login/registration. */
class TokenManager(
    context: Context,
) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
        }
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun clearTokens() {
        prefs.edit {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
        }
    }

    fun isLoggedIn(): Boolean = !getAccessToken().isNullOrEmpty()

    companion object {
        private const val KEY_ACCESS_TOKEN  = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}
