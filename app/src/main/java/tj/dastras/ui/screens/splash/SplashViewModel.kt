package tj.dastras.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.dastras.core.api.RelaxApiService
import tj.dastras.core.firebase.RelaxFcmTokenManager
import tj.dastras.data.AuthRepository
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
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