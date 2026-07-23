package tj.relax.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel

/**
 * Returns a ViewModel scoped to the host Activity, so the same instance (and its
 * already-loaded state) is shared across every screen that requests it — avoiding
 * duplicate network calls when navigating between screens.
 */
@Composable
inline fun <reified T : ViewModel> activityViewModel(): T {
    val activity = LocalContext.current as ComponentActivity
    return hiltViewModel(activity)
}
