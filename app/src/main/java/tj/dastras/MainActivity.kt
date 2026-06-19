package tj.dastras

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import tj.dastras.core.navigation.RelaxNavGraph
import tj.dastras.ui.theme.DastrasTheme
import tj.dastras.ui.theme.RelaxBackground

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DastrasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = RelaxBackground,
                ) {
                    val navController = rememberNavController()
                    RelaxNavGraph(navController = navController)
                }
            }
        }
    }
}
