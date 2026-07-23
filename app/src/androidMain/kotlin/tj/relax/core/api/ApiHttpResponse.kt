package tj.relax.core.api

/** Minimal Retrofit-`Response`-shaped wrapper so repositories didn't need to change when the
 * networking layer moved from Retrofit to Ktor. */
class ApiHttpResponse<T>(
    val isSuccessful: Boolean,
    val code: Int,
    val body: T?,
    val errorBody: String?,
)
