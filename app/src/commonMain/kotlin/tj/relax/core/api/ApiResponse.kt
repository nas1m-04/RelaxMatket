package tj.relax.core.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean = false,
    val data: T? = null,
    val error: String? = null,
    val code: String? = null,
    val traceId: String? = null,
    val exceptionType: String? = null,
    val exceptionMessage: String? = null,
)