package tj.relax.ui.theme

import androidx.compose.runtime.Composable

// No-op: Compose Multiplatform doesn't expose iOS status-bar styling the same way Android's
// WindowCompat does. If this turns out to matter visually, it needs a UIKit-level hook from the
// iOS entry point (Phase 9) — documented as an accepted gap for now, not silently dropped.
@Composable
actual fun ConfigureSystemBars() {
}
