package tj.relax.core.firebase

/** Asks the platform for the current push token and invokes [onToken] once available.
 * No-op on iOS — push notifications are a separate, deferred project (see migration plan). */
expect fun requestFcmTokenUpload(onToken: (String) -> Unit)
