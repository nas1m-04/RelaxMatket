package tj.dastras.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tj.dastras.ui.theme.RelaxDark
import tj.dastras.ui.theme.RelaxOrange
import tj.dastras.ui.theme.RelaxRed
import tj.dastras.ui.theme.RelaxWhite

@Composable
fun RelaxButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .shadow(
                elevation = if (enabled && !isLoading) 8.dp else 0.dp,
                shape     = RoundedCornerShape(16.dp),
                spotColor = RelaxRed.copy(alpha = 0.32f),
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (enabled && !isLoading)
                    Brush.linearGradient(listOf(RelaxRed, RelaxOrange))
                else
                    Brush.linearGradient(listOf(Color(0xFFCCCCCC), Color(0xFFDDDDDD)))
            )
            .clickable(enabled = enabled && !isLoading, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier    = Modifier.size(22.dp),
                color       = RelaxWhite,
                strokeWidth = 2.5.dp,
            )
        } else {
            Text(
                text       = text,
                color      = RelaxWhite,
                style      = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
            )
        }
    }
}

// ── Outlined Button ────────────────────────────────────────────
@Composable
fun RelaxOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    OutlinedButton(
        onClick  = onClick,
        modifier = modifier.height(52.dp),
        shape    = RoundedCornerShape(14.dp),
        border   = BorderStroke(1.5.dp, RelaxDark),
        colors   = ButtonDefaults.outlinedButtonColors(
            contentColor = RelaxDark,
        )
    ) {
        if (icon != null) {
            Icon(icon, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

// ── Dark Button ────────────────────────────────────────────────
@Composable
fun RelaxDarkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Button(
        onClick   = onClick,
        modifier  = modifier.height(56.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = ButtonDefaults.buttonColors(
            containerColor = RelaxDark,
            contentColor   = RelaxWhite,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (icon != null) {
            Icon(icon, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}