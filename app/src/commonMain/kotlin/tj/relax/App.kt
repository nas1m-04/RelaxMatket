package tj.relax

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import tj.relax.core.navigation.RelaxNavGraph
import tj.relax.ui.theme.RelaxBackground
import tj.relax.ui.theme.RelaxTheme

/** Shared entry point mounted by both `MainActivity.setContent { App() }` (Android) and
 * `MainViewController.kt`'s `ComposeUIViewController { App() }` (iOS). */
@Composable
fun App() {
    RelaxTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = RelaxBackground) {
            val navController = rememberNavController()
            RelaxNavGraph(navController = navController)
        }
    }
}
