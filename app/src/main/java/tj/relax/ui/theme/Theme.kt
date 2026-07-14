package tj.relax.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SecondaryContainer = Color(0xFFFFE5E5)
private val TertiaryContainer  = Color(0xFFE3EBFC)

private val RelaxColorScheme = lightColorScheme(
    primary          = RelaxDark,
    onPrimary        = RelaxWhite,
    primaryContainer = RelaxDarkSecondary,
    onPrimaryContainer = RelaxWhite,

    secondary        = RelaxRed,
    onSecondary      = RelaxWhite,
    secondaryContainer   = SecondaryContainer,
    onSecondaryContainer = RelaxRed,

    tertiary         = RelaxDarkSecondary,
    onTertiary       = RelaxWhite,
    tertiaryContainer    = TertiaryContainer,
    onTertiaryContainer  = RelaxDarkSecondary,

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
fun RelaxTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = RelaxColorScheme,
        typography  = Typography,
        content     = content
    )
}