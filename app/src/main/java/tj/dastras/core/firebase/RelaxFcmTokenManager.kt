package tj.dastras.core.firebase


import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

object RelaxFcmTokenManager {

    private var pendingToken: String? = null
    private var uploadCallback: ((String) -> Unit)? = null

    fun onTokenRefresh(token: String) {
        Log.d("FCM", "New token: $token")
        val cb = uploadCallback
        if (cb != null) {
            cb(token)
        } else {
            pendingToken = token
        }
    }

    fun getTokenAndUpload(onToken: (String) -> Unit) {
        uploadCallback = onToken
        // Если токен уже обновился до регистрации callback
        pendingToken?.let {
            onToken(it)
            pendingToken = null
            return
        }
        // Запрашиваем текущий токен
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            onToken(token)
        }
    }
}