package tj.relax.core.crash

import android.os.Build
import tj.relax.core.util.AndroidPlatformContext

actual fun currentPlatformInfo(): PlatformInfo = PlatformInfo(
    deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
    osVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
)

actual fun appSupportDirectoryPath(): String =
    AndroidPlatformContext.applicationContext.filesDir.absolutePath
