package tj.relax.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tj.relax.core.api.RelaxApiService
import tj.relax.core.firebase.RelaxFcmTokenManager
import tj.relax.data.AuthRepository

class SplashViewModel(
    private val authRepository: AuthRepository,
    private val api: RelaxApiService,
) : ViewModel() {

    val isLoggedIn: Boolean get() = authRepository.isLoggedIn

    init {
        if (authRepository.isLoggedIn) {
            uploadFcmTokenIfNeeded()
        }
    }

    private fun uploadFcmTokenIfNeeded() {
        viewModelScope.launch {
            try {
                RelaxFcmTokenManager.getTokenAndUpload { token ->
                    viewModelScope.launch {
                        try {
                            api.updateFcmToken(RelaxApiService.UpdateFcmTokenRequest(token))
                        } catch (e: Exception) {
                            // тихо игнорируем — не критично
                        }
                    }
                }
            } catch (e: Exception) {
                // тихо игнорируем
            }
        }
    }
}