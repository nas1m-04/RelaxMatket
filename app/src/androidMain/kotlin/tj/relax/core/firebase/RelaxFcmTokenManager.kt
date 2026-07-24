package tj.relax.core.firebase


import io.github.aakira.napier.Napier
import com.google.firebase.messaging.FirebaseMessaging

object RelaxFcmTokenManager {

    private var pendingToken: String? = null
    private var uploadCallback: ((String) -> Unit)? = null

    fun onTokenRefresh(token: String) {
        Napier.d("New token: $token", tag = "FCM")
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