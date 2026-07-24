package tj.relax.core.crash

import platform.Foundation.NSHomeDirectory
import platform.UIKit.UIDevice

actual fun currentPlatformInfo(): PlatformInfo = PlatformInfo(
    deviceModel = UIDevice.currentDevice.model,
    osVersion = "iOS ${UIDevice.currentDevice.systemVersion}",
)

actual fun appSupportDirectoryPath(): String = NSHomeDirectory() + "/Library/Application Support"
