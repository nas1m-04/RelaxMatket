package tj.dastras.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SecondaryContainer = Color(0xFFFFE5E5)
private val TertiaryContainer  = Color(0xFFFFF0E8)

private val RelaxColorScheme = lightColorScheme(
    primary          = RelaxDark,
    onPrimary        = RelaxWhite,
    primaryContainer = RelaxDarkSecondary,
    onPrimaryContainer = RelaxWhite,

    secondary        = RelaxRed,
    onSecondary      = RelaxWhite,
    secondaryContainer   = SecondaryContainer,
    onSecondaryContainer = RelaxRed,

    tertiary         = RelaxOrange,
    onTertiary       = RelaxWhite,
    tertiaryContainer    = TertiaryContainer,
    onTertiaryContainer  = RelaxOrange,

    background       = RelaxBackground,
    onBackground     = RelaxTextPrimary,

    surface          = RelaxSurface,
    onSurface        = RelaxTextPrimary,
    surfaceVariant   = RelaxSurfaceAlt,
    onSurfaceVariant = RelaxTextSecondary,

    outline          = RelaxBorder,
    outlineVariant   = RelaxDivider,

    error            = RelaxError,
    onError          = RelaxWhite,
)

@Composable
fun DastrasTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            // true = тёмные иконки (для светлого фона)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = RelaxColorScheme,
        typography  = Typography,
        content     = content
    )
}