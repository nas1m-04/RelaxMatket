package tj.dastras.data.remote

import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException

private val errorGson = Gson()

private data class ApiErrorEnvelope(val error: String? = null)

/** Extracts the human-readable `error` field from a failed response's JSON body, if present. */
fun Response<*>.apiErrorMessage(): String? {
    val raw = errorBody()?.string()?.takeIf { it.isNotBlank() } ?: return null
    return try {
        errorGson.fromJson(raw, ApiErrorEnvelope::class.java)?.error?.takeIf { it.isNotBlank() }
    } catch (e: Exception) {
        null
    }
}

/** Maps a thrown exception to a user-facing Russian message. */
fun friendlyErrorMessage(e: Throwable): String = when (e) {
    is IOException -> "Нет подключения к интернету. Проверьте сеть и попробуйте снова"
    else -> e.message?.takeIf { it.isNotBlank() } ?: "Что-то пошло не так. Попробуйте позже"
}
