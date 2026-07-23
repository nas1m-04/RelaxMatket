package tj.relax.core.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.SharedFlow
import tj.relax.data.SessionManager

class SessionViewModel(
    sessionManager: SessionManager,
) : ViewModel() {
    val sessionExpired: SharedFlow<Unit> = sessionManager.sessionExpired
}
