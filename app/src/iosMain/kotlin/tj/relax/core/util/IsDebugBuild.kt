package tj.relax.core.util

// Good enough for the debug-only Ktor request/response logging this currently gates — revisit
// with a real Kotlin/Native debug-binary check if this ends up controlling anything more.
actual val isDebugBuild: Boolean = true
