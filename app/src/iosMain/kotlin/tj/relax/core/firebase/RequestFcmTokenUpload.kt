package tj.relax.core.firebase

// Push notifications are out of scope for iOS for now (see migration plan) — deliberately a
// permanent no-op, not a TODO() to fill in during Phase 9.
actual fun requestFcmTokenUpload(onToken: (String) -> Unit) {
}
