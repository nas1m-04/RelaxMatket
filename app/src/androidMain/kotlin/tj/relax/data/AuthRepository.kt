package tj.relax.data

import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.apiErrorMessage
import tj.relax.core.api.friendlyErrorMessage
import tj.relax.core.firebase.RelaxFcmTokenManager
import tj.relax.ui.screens.auth.data.dto.request.ChangePasswordRequest
import tj.relax.ui.screens.auth.data.dto.request.LoginRequest
import tj.relax.ui.screens.auth.data.dto.request.RegisterRequest

private const val TAG = "AuthRepository"

class AuthRepository(
    private val api: RelaxApiService,
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository,
) {
    val isLoggedIn: Boolean
        get() = tokenManager.isLoggedIn()

    suspend fun register(
        phone: String,
        password: String,
        name: String,
        secretQuestion: String? = null,
        secretAnswer: String? = null,
    ): Result<UserProfile> {
        return try {
            Napier.d("register: phone=$phone", tag = TAG)
            val response = api.register(RegisterRequest(
                phone = phone,
                password = password,
                name = name,
                secretQuestion = secretQuestion,
                secretAnswer = secretAnswer,
            ))
            val body = response.body

            if (response.isSuccessful && body?.success == true && body.data != null) {
                tokenManager.saveTokens(body.data.accessToken, body.data.refreshToken)
                uploadFcmToken()
                Napier.i("register: success uid=${body.data.user?.uid}", tag = TAG)
                Result.success(body.data.user ?: UserProfile())
            } else {
                val message = response.apiErrorMessage() ?: body?.error ?: when (response.code) {
                    409, 400 -> "Пользователь с таким номером уже зарегистрирован"
                    else     -> "Не удалось зарегистрироваться. Попробуйте позже"
                }
                Napier.w("register: failed code=${response.code} message=$message", tag = TAG)
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Napier.e("register: error", e, tag = TAG)
            Result.failure(Exception(friendlyErrorMessage(e)))
        }
    }

    suspend fun login(phone: String, password: String): Result<UserProfile> {
        return try {
            Napier.d("login: phone=$phone", tag = TAG)
            val response = api.login(LoginRequest(phone = phone, password = password))
            val body = response.body

            if (response.isSuccessful && body?.success == true && body.data != null) {
                tokenManager.saveTokens(body.data.accessToken, body.data.refreshToken)
                uploadFcmToken()
                Napier.i("login: success uid=${body.data.user?.uid}", tag = TAG)
                Result.success(body.data.user ?: UserProfile())
            } else {
                val message = response.apiErrorMessage() ?: body?.error ?: when (response.code) {
                    401, 400, 404 -> "Неверный номер телефона или пароль"
                    else          -> "Не удалось выполнить вход. Попробуйте позже"
                }
                Napier.w("login: failed code=${response.code} message=$message", tag = TAG)
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Napier.e("login: error", e, tag = TAG)
            Result.failure(Exception(friendlyErrorMessage(e)))
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            val response = api.changePassword(ChangePasswordRequest(currentPassword, newPassword))
            val body = response.body

            if (response.isSuccessful && body?.success == true) {
                Napier.i("changePassword: success", tag = TAG)
                Result.success(Unit)
            } else {
                val message = response.apiErrorMessage() ?: body?.error ?: when (response.code) {
                    401 -> "Текущий пароль неверен"
                    else -> "Не удалось сменить пароль. Попробуйте позже"
                }
                Napier.w("changePassword: failed code=${response.code} message=$message", tag = TAG)
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Napier.e("changePassword: error", e, tag = TAG)
            Result.failure(Exception(friendlyErrorMessage(e)))
        }
    }

    fun logout() {
        Napier.d("logout", tag = TAG)
        tokenManager.clearTokens()
        userRepository.clearCache()
    }

    private suspend fun uploadFcmToken() {
        try {
            RelaxFcmTokenManager.getTokenAndUpload { token ->
                kotlinx.coroutines.GlobalScope.launch {
                    try {
                        api.updateFcmToken(RelaxApiService.UpdateFcmTokenRequest(token))
                        Napier.i("FCM token uploaded: $token", tag = TAG)
                    } catch (e: Exception) {
                        Napier.w("FCM token upload failed", e, tag = TAG)
                    }
                }
            }
        } catch (e: Exception) {
            Napier.w("uploadFcmToken error", e, tag = TAG)
        }
    }
}
