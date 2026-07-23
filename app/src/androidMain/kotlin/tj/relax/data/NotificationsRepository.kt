package tj.relax.data

import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.dataOrThrow
import tj.relax.ui.screens.notifications.data.toDomain

class NotificationsRepository(
    private val api: RelaxApiService,
) {
    suspend fun getNotifications(): List<Notification> =
        api.getNotifications().dataOrThrow().map { it.toDomain() }

    suspend fun markAllRead() {
        api.markAllNotificationsRead()
    }

    suspend fun getInAppModal(): Notification? {
        val response = api.getInAppNotification()
        if (response.code() == 204) return null
        return response.dataOrThrow().toDomain()
    }

    suspend fun markRead(id: Int) {
        api.markNotificationRead(id)
    }

    suspend fun getUnreadCount(): Int =
        api.getUnreadNotificationsCount().dataOrThrow().count
}
