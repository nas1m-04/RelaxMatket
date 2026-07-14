package tj.relax.core.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationChannels {
    const val MAIN = "relax_main"

    /** Must exist before a background/killed-app push can be auto-displayed by the system —
     * previously this only ever ran inside the foreground notification-building path, so a device
     * that had never had the app in the foreground when a push arrived had no channel yet and could
     * silently drop it. Called eagerly at app startup so the channel is always there in time. */
    fun ensureCreated(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(MAIN, "Уведомления Relax", NotificationManager.IMPORTANCE_HIGH)
        )
    }
}
