package tj.relax.core.crash

actual fun currentPlatformInfo(): PlatformInfo {
    TODO("iOS: UIDevice.currentDevice / NSProcessInfo (Phase 9)")
}

actual fun appSupportDirectoryPath(): String {
    TODO("iOS: NSHomeDirectory()-based app support path (Phase 9)")
}
