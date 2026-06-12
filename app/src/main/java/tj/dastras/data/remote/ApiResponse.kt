package tj.dastras.data.remote

data class ApiResponse<T>(
    val success: Boolean = false,
    val data: T? = null,
    val error: String? = null,
    val code: String? = null,
)
