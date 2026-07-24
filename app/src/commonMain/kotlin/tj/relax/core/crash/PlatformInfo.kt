package tj.relax.core.crash

data class PlatformInfo(
    val deviceModel: String,
    val osVersion: String,
)

expect fun currentPlatformInfo(): PlatformInfo

/** Directory the app can write private files to, used to persist a crash report across the
 * process death that triggered it, for upload on the next launch. */
expect fun appSupportDirectoryPath(): String
