package tj.dastras.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import tj.dastras.data.Product
import tj.dastras.ui.theme.RelaxDark
import tj.dastras.ui.theme.RelaxRed
import tj.dastras.ui.theme.RelaxShadow
import tj.dastras.ui.theme.RelaxTextHint
import tj.dastras.ui.theme.RelaxTextPrimary
import tj.dastras.ui.theme.RelaxWhite

// ── Product Card (Horizontal) ──────────────────────────────────
@Composable
fun ProductCardHorizontal(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick   = onClick,
        modifier  = modifier
            .width(160.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(20.dp), spotColor = RelaxShadow),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                AsyncImage(
                    model             = product.imageUrl,
                    contentDescription = product.name,
                    contentScale      = ContentScale.Crop,
                    modifier          = Modifier.fillMaxSize(),
                )
                if (product.oldPrice != null) {
                    val disc = ((1 - product.price / product.oldPrice) * 100).toInt()
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(RelaxRed)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text("−$disc%", color = RelaxWhite, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(product.name, style = MaterialTheme.typography.bodySmall, color = RelaxTextPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("${product.price.toInt()} TJS", style = MaterialTheme.typography.labelLarge, color = RelaxTextPrimary, fontWeight = FontWeight.Bold)
                        if (product.oldPrice != null) {
                            Text("${product.oldPrice.toInt()} TJS", style = MaterialTheme.typography.labelSmall, color = RelaxTextHint, textDecoration = TextDecoration.LineThrough)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(RelaxDark)
                            .clickable(onClick = onAddToCart),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Add, null, tint = RelaxWhite, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}