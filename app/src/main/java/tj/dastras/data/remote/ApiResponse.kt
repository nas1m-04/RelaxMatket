package tj.dastras.data.remote

data class ApiResponse<T>(
    val success: Boolean = false,
    val data: T? = null,
    val error: String? = null,
    val code: String? = null,
    /** Present on every error response; lets the user reference the failed request in support. */
    val traceId: String? = null,
    /** Only present when [code] == "INTERNAL_ERROR" (500): the real server-side exception type. */
    val exceptionType: String? = null,
    /** Only present when [code] == "INTERNAL_ERROR" (500): the real server-side exception message. */
    val exceptionMessage: String? = null,
)
