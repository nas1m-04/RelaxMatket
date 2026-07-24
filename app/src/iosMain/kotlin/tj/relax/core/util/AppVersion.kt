package tj.relax.core.util

import platform.Foundation.NSBundle

actual val appVersionName: String =
    (NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String) ?: "1.0"
