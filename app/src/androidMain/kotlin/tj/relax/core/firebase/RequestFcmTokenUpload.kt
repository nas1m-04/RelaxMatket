package tj.relax.core.firebase

actual fun requestFcmTokenUpload(onToken: (String) -> Unit) {
    RelaxFcmTokenManager.getTokenAndUpload(onToken)
}
