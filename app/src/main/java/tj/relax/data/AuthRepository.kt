package tj.relax.data

import android.util.Log
import kotlinx.coroutines.launch
import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.apiErrorMessage
import tj.relax.core.api.friendlyErrorMessage
import tj.relax.core.firebase.RelaxFcmTokenManager
import tj.relax.ui.screens.auth.data.dto.request.LoginRequest
import tj.relax.ui.screens.auth.data.dto.request.RegisterRequest
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AuthRepository"

@Singleton
class AuthRepository @Inject constructor(
    private val api: RelaxApiService,
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository,
) {
    val isLoggedIn: Boolean
        get() = tokenManager.isLoggedIn()

    suspend fun register(phone: String, password: String, name: String): Result<UserProfile> {
        return try {
            Log.d(TAG, "register: phone=$phone")
            val response = api.register(RegisterRequest(phone = phone, password = password, name = name))
            val body = response.body()

            if (response.isSuccessful && body?.success == true && body.data != null) {
                tokenManager.saveTokens(body.data.accessToken, body.data.refreshToken)
                uploadFcmToken()
                Log.i(TAG, "register: success uid=${body.data.user?.uid}")
                Result.success(body.data.user ?: UserProfile())
            } else {
                val message = response.apiErrorMessage() ?: body?.error ?: when (response.code()) {
                    409, 400 -> "Пользователь с таким номером уже зарегистрирован"
                    else     -> "Не удалось зарегистрироваться. Попробуйте позже"
                }
                Log.w(TAG, "register: failed code=${response.code()} message=$message")
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Log.e(TAG, "register: error", e)
            Result.failure(Exception(friendlyErrorMessage(e)))
        }
    }

    suspend fun login(phone: String, password: String): Result<UserProfile> {
        return try {
            Log.d(TAG, "login: phone=$phone")
            val response = api.login(LoginRequest(phone = phone, password = password))
            val body = response.body()

            if (response.isSuccessful && body?.success == true && body.data != null) {
                tokenManager.saveTokens(body.data.accessToken, body.data.refreshToken)
                uploadFcmToken()
                Log.i(TAG, "login: success uid=${body.data.user?.uid}")
                Result.success(body.data.user ?: UserProfile())
            } else {
                val message = response.apiErrorMessage() ?: body?.error ?: when (response.code()) {
                    401, 400, 404 -> "Неверный номер телефона или пароль"
                    else          -> "Не удалось выполнить вход. Попробуйте позже"
                }
                Log.w(TAG, "login: failed code=${response.code()} message=$message")
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Log.e(TAG, "login: error", e)
            Result.failure(Exception(friendlyErrorMessage(e)))
        }
    }

    fun logout() {
        Log.d(TAG, "logout")
        tokenManager.clearTokens()
        userRepository.clearCache()
    }

    private suspend fun uploadFcmToken() {
        try {
            RelaxFcmTokenManager.getTokenAndUpload { token ->
                kotlinx.coroutines.GlobalScope.launch {
                    try {
                        api.updateFcmToken(RelaxApiService.UpdateFcmTokenRequest(token))
                        Log.i(TAG, "FCM token uploaded: $token")
                    } catch (e: Exception) {
                        Log.w(TAG, "FCM token upload failed", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "uploadFcmToken error", e)
        }
    }
}
