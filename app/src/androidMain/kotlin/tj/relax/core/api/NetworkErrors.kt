package tj.relax.core.api

import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException

private val errorGson = Gson()

private data class ApiErrorBody(
    val success: Boolean = false,
    val error: String? = null,
    val code: String? = null,
    val traceId: String? = null,
    val exceptionType: String? = null,
    val exceptionMessage: String? = null,
)

private fun Response<*>.apiErrorBody(): ApiErrorBody? {
    val raw = errorBody()?.string()?.takeIf { it.isNotBlank() } ?: return null
    return try {
        errorGson.fromJson(raw, ApiErrorBody::class.java)
    } catch (e: Exception) {
        null
    }
}

/** Extracts the human-readable `error` field from a failed response's JSON body, if present. */
fun Response<*>.apiErrorMessage(): String? = apiErrorBody()?.error?.takeIf { it.isNotBlank() }

/** Builds an [ApiException] describing why this response failed, ready to throw or report. */
fun Response<*>.toApiException(fallbackMessage: String = "Не удалось выполнить запрос. Попробуйте позже"): ApiException {
    val body = apiErrorBody()
    return ApiException(
        apiCode = body?.code,
        traceId = body?.traceId,
        exceptionType = body?.exceptionType,
        exceptionMessage = body?.exceptionMessage,
        httpCode = code(),
        message = body?.error?.takeIf { it.isNotBlank() } ?: fallbackMessage,
    )
}

/** Returns the response's `data` payload, or throws [ApiException] if the request failed. */
fun <T> Response<ApiResponse<T>>.dataOrThrow(fallbackMessage: String = "Не удалось выполнить запрос. Попробуйте позже"): T {
    val data = body()?.data
    if (isSuccessful && data != null) return data
    throw toApiException(fallbackMessage)
}

/** Maps a thrown exception to a user-facing Russian message. */
fun friendlyErrorMessage(e: Throwable): String = when (e) {
    is ApiException -> when {
        e.isServerUnavailable -> "Сервер обновляется, пожалуйста подождите немного и попробуйте снова"
        e.isInternalError     -> "Произошла ошибка, попробуйте позже"
        else                  -> e.message ?: "Что-то пошло не так. Попробуйте позже"
    }
    is IOException -> "Нет подключения к интернету. Проверьте сеть и попробуйте снова"
    else -> e.message?.takeIf { it.isNotBlank() } ?: "Что-то пошло не так. Попробуйте позже"
}
