package tj.relax.core.crash

import android.content.Context
import android.os.Build
import android.util.Log
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import tj.relax.BuildConfig
import tj.relax.core.api.RelaxApiService
import tj.relax.data.CrashReportRequest
import java.io.File

private const val TAG = "CrashReporter"
private const val CRASH_FILE_NAME = "pending_crash.json"
private const val MAX_MESSAGE_LENGTH = 2000
private const val MAX_STACK_TRACE_LENGTH = 20000

@Serializable
private data class PendingCrash(
    val message: String,
    val stackTrace: String,
    val occurredAt: String,
)

/**
 * Catches otherwise-silent crashes and reports them to the backend (api/crash-reports, category
 * AndroidCrash), so "the app crashed" turns into an actual stack trace we can look up instead of
 * a user having to describe what happened from memory.
 *
 * The crash itself is only written to local disk synchronously (fast, reliable even as the process
 * is dying) — the actual network upload happens on the NEXT app start, since a crashing process
 * can't be trusted to complete an HTTP call before it's killed.
 */
object CrashReporter {
    private val json = Json { ignoreUnknownKeys = true }

    fun install(context: Context) {
        val appContext = context.applicationContext
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                writeCrashFile(appContext, throwable)
            } catch (e: Throwable) {
                Napier.e("Failed to persist crash before process death", e, tag = TAG)
            }
            previousHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun writeCrashFile(context: Context, throwable: Throwable) {
        val crash = PendingCrash(
            message = throwable.message ?: throwable.toString(),
            stackTrace = Log.getStackTraceString(throwable),
            occurredAt = Clock.System.now().toString(),
        )
        File(context.filesDir, CRASH_FILE_NAME).writeText(json.encodeToString(PendingCrash.serializer(), crash))
    }

    /** Call once on app start — uploads a crash captured just before the previous process died. */
    suspend fun sendPendingCrashIfAny(context: Context, api: RelaxApiService, uid: String?) {
        val file = File(context.applicationContext.filesDir, CRASH_FILE_NAME)
        if (!file.exists()) return

        val crash = try {
            json.decodeFromString<PendingCrash>(file.readText())
        } catch (e: Exception) {
            null
        }
        // Always delete first — a crash report that keeps failing to send (no network, server
        // down) must never turn into a retry loop that reports the same crash forever.
        file.delete()
        if (crash == null) return

        try {
            api.reportCrash(
                CrashReportRequest(
                    uid = uid,
                    message = crash.message.take(MAX_MESSAGE_LENGTH),
                    stackTrace = crash.stackTrace.take(MAX_STACK_TRACE_LENGTH),
                    appVersion = BuildConfig.VERSION_NAME,
                    deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
                    osVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                    occurredAtClient = crash.occurredAt,
                )
            )
        } catch (e: Exception) {
            Napier.w("Failed to upload pending crash report", e, tag = TAG)
        }
    }
}
