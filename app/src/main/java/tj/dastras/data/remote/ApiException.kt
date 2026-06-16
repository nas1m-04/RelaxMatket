package tj.dastras.data.remote

/**
 * Thrown by repositories when the backend responds with `{success: false, ...}`.
 * Carries the full error envelope so [tj.dastras.ui.components.ErrorAlertDialog]
 * can render it appropriately.
 */
class ApiException(
    val apiCode: String?,
    val traceId: String? = null,
    val exceptionType: String? = null,
    val exceptionMessage: String? = null,
    val httpCode: Int? = null,
    message: String?,
) : Exception(message) {

    /** `code == "INTERNAL_ERROR"`, i.e. an unhandled server-side exception (HTTP 500). */
    val isInternalError: Boolean get() = apiCode == INTERNAL_ERROR_CODE

    /** `code == "VALIDATION_ERROR"`, i.e. the user's input failed validation. */
    val isValidationError: Boolean get() = apiCode == VALIDATION_ERROR_CODE

    /** HTTP 502/503/504 — the hosting platform is restarting/redeploying the backend. */
    val isServerUnavailable: Boolean get() = httpCode in SERVER_UNAVAILABLE_CODES

    companion object {
        const val INTERNAL_ERROR_CODE = "INTERNAL_ERROR"
        const val VALIDATION_ERROR_CODE = "VALIDATION_ERROR"
        private val SERVER_UNAVAILABLE_CODES = setOf(502, 503, 504)
    }
}
