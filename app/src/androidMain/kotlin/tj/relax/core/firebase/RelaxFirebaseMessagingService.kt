package tj.relax.core.firebase
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.runBlocking
import tj.relax.R
import tj.relax.MainActivity
import tj.relax.core.events.LoyaltyPushEvents

class RelaxFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Сохраняем токен — отправим на сервер
        RelaxFcmTokenManager.onTokenRefresh(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Cashier just consumed this user's loyalty QR (accrue or redeem) — refresh it now rather
        // than waiting for the screen's own 5-minute timer, if the QR screen is currently open.
        val type = message.data["type"]
        if (type == "bonus_earned" || type == "bonus_spent") {
            LoyaltyPushEvents.notifyQrConsumed()
        }

        val title = message.notification?.title ?: message.data["title"] ?: return
        val body  = message.notification?.body  ?: message.data["body"]  ?: return
        val imageUrl = message.notification?.imageUrl?.toString() ?: message.data["imageUrl"]

        showNotification(title, body, imageUrl)
    }

    // onMessageReceived already runs off the main thread (FCM SDK's own contract), so a blocking
    // image fetch here is safe — no ANR risk, same reasoning typical FCM sample code relies on.
    private fun showNotification(title: String, body: String, imageUrl: String?) {
        NotificationChannels.ensureCreated(this)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE,
        )

        val bitmap: Bitmap? = imageUrl?.let { url ->
            runCatching {
                runBlocking {
                    val result = imageLoader.execute(ImageRequest.Builder(this@RelaxFirebaseMessagingService).data(url).build())
                    result.drawable?.toBitmap()
                }
            }.getOrNull()
        }

        val builder = NotificationCompat.Builder(this, NotificationChannels.MAIN)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (bitmap != null) {
            builder
                .setLargeIcon(bitmap)
                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null as Bitmap?))
        }

        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}