package tj.relax.data

import kotlinx.serialization.Serializable

@Serializable
data class CrashReportRequest(
    val uid: String? = null,
    val message: String,
    val stackTrace: String? = null,
    val appVersion: String? = null,
    val deviceModel: String? = null,
    val osVersion: String? = null,
    val occurredAtClient: String? = null,
)
