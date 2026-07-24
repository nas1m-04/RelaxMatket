package tj.relax.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import tj.relax.generated.resources.*
import tj.relax.data.Product
import tj.relax.ui.theme.*








// ── Shimmer ────────────────────────────────────────────────────
@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue  = 1000f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing)),
        label = "shimmer",
    )
    val brush = Brush.linearGradient(
        colors = listOf(Color(0xFFE5E7EB), Color(0xFFF3F4F6), Color(0xFFE5E7EB)),
        start  = Offset(translateAnim - 200f, 0f),
        end    = Offset(translateAnim, 0f),
    )
    Box(modifier = modifier.background(brush))
}

// ── Badge Chip ────────────────────────────────────────────────
@Composable
fun RelaxBadge(text: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelSmall, color = RelaxWhite, fontWeight = FontWeight.Bold)
    }
}

// ── Price Tag ─────────────────────────────────────────────────
@Composable
fun PriceTag(price: Double, oldPrice: Double? = null, cardPrice: Double? = null, modifier: Modifier = Modifier) {
    val hasCardDiscount = cardPrice != null && cardPrice > 0 && cardPrice < price
    val effectivePrice  = if (hasCardDiscount) cardPrice!! else price
    Row(modifier = modifier, verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text       = "${effectivePrice.toInt()} TJS",
            style      = MaterialTheme.typography.headlineMedium,
            color      = if (hasCardDiscount) RelaxGold else RelaxTextPrimary,
            fontWeight = FontWeight.Bold,
        )
        val strikePrice = if (hasCardDiscount) price else oldPrice
        if (strikePrice != null) {
            Text(
                text           = "${strikePrice.toInt()} TJS",
                style          = MaterialTheme.typography.bodyMedium,
                color          = RelaxTextHint,
                textDecoration = TextDecoration.LineThrough,
            )
        }
    }
}

// ── Rating Row ────────────────────────────────────────────────
@Composable
fun RatingRow(rating: Double, reviewCount: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(Icons.Rounded.Star, null, tint = Color(0xFFFBBC04), modifier = Modifier.size(16.dp))
        Text("$rating", style = MaterialTheme.typography.labelMedium, color = RelaxTextPrimary, fontWeight = FontWeight.Bold)
        Text(stringResource(Res.string.relax_reviews_count_parens, reviewCount), style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
    }
}



// ── Divider ───────────────────────────────────────────────────
@Composable
fun RelaxDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier, color = RelaxDivider, thickness = 1.dp)
}
