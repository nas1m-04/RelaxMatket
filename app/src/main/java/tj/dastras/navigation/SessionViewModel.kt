package tj.dastras.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import tj.dastras.data.SessionManager
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    sessionManager: SessionManager,
) : ViewModel() {
    val sessionExpired: SharedFlow<Unit> = sessionManager.sessionExpired
}
