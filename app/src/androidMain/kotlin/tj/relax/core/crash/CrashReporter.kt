package tj.relax.core.crash

import android.util.Log
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import tj.relax.BuildConfig
import tj.relax.core.api.RelaxApiService
import tj.relax.data.CrashReportRequest

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
    private val fileSystem = FileSystem.SYSTEM
    private val crashPath get() = "${appSupportDirectoryPath()}/$CRASH_FILE_NAME".toPath()

    fun install() {
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                writeCrashFile(throwable)
            } catch (e: Throwable) {
                Napier.e("Failed to persist crash before process death", e, tag = TAG)
            }
            previousHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun writeCrashFile(throwable: Throwable) {
        val crash = PendingCrash(
            message = throwable.message ?: throwable.toString(),
            stackTrace = Log.getStackTraceString(throwable),
            occurredAt = Clock.System.now().toString(),
        )
        fileSystem.write(crashPath) {
            writeUtf8(json.encodeToString(PendingCrash.serializer(), crash))
        }
    }

    /** Call once on app start — uploads a crash captured just before the previous process died. */
    suspend fun sendPendingCrashIfAny(api: RelaxApiService, uid: String?) {
        if (!fileSystem.exists(crashPath)) return

        val crash = try {
            json.decodeFromString<PendingCrash>(fileSystem.read(crashPath) { readUtf8() })
        } catch (e: Exception) {
            null
        }
        // Always delete first — a crash report that keeps failing to send (no network, server
        // down) must never turn into a retry loop that reports the same crash forever.
        fileSystem.delete(crashPath, mustExist = false)
        if (crash == null) return

        try {
            val platformInfo = currentPlatformInfo()
            api.reportCrash(
                CrashReportRequest(
                    uid = uid,
                    message = crash.message.take(MAX_MESSAGE_LENGTH),
                    stackTrace = crash.stackTrace.take(MAX_STACK_TRACE_LENGTH),
                    appVersion = BuildConfig.VERSION_NAME,
                    deviceModel = platformInfo.deviceModel,
                    osVersion = platformInfo.osVersion,
                    occurredAtClient = crash.occurredAt,
                )
            )
        } catch (e: Exception) {
            Napier.w("Failed to upload pending crash report", e, tag = TAG)
        }
    }
}
