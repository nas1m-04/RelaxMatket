package tj.relax.ui.components

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import org.koin.compose.koinInject

/**
 * Returns a ViewModel registered as an app-lifetime Koin singleton (`single { }`, not
 * `viewModel { }`) in ViewModelModule, so the same instance — and its already-loaded state —
 * is shared across every screen that requests it, avoiding duplicate network calls when
 * navigating between screens. Replaces the old Activity-scoped hiltViewModel(activity)/
 * koinViewModel(viewModelStoreOwner = activity) pattern, which had no Compose Multiplatform
 * equivalent (no ComponentActivity on iOS) — a Koin singleton has exactly the same lifetime
 * as the Activity-scoped instance did in this single-Activity app, just portable.
 */
@Composable
inline fun <reified T : ViewModel> sharedViewModel(): T = koinInject<T>()
