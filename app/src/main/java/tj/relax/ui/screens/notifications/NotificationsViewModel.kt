package tj.relax.ui.screens.notifications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tj.relax.core.api.friendlyErrorMessage
import tj.relax.data.Notification
import tj.relax.data.NotificationsRepository
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: NotificationsRepository,
) : ViewModel() {

    var uiState by mutableStateOf(NotificationsUiState())
        private set

    init { load() }

    fun load() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val notifications = repository.getNotifications()
                uiState = uiState.copy(notifications = notifications, isLoading = false)
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = friendlyErrorMessage(e))
            }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            try {
                repository.markAllRead()
                uiState = uiState.copy(
                    notifications = uiState.notifications.map { it.copy(isRead = true) }
                )
            } catch (_: Exception) {}
        }
    }
}
