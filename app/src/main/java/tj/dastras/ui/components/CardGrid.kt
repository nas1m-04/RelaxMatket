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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import tj.dastras.data.Product
import tj.dastras.ui.theme.RelaxDark
import tj.dastras.ui.theme.RelaxOrange
import tj.dastras.ui.theme.RelaxRed
import tj.dastras.ui.theme.RelaxTextHint
import tj.dastras.ui.theme.RelaxTextPrimary
import tj.dastras.ui.theme.RelaxTextSecondary
import tj.dastras.ui.theme.RelaxWhite

// ── Product Card (Grid) ────────────────────────────────────────
@Composable
fun ProductCardGrid(
    product: Product,
    onClick: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    quantity: Int = 0,
    isFavorite: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick   = onClick,
        modifier  = modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x18000000), ambientColor = Color(0x08000000)),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
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
                            .padding(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(RelaxRed)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text("−$disc%", color = RelaxWhite, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
                if (product.isNew) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(RelaxOrange)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text("NEW", color = RelaxWhite, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
                if (onToggleFavorite != null) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(RelaxWhite.copy(alpha = 0.85f))
                            .align(Alignment.BottomEnd)
                            .clickable(onClick = onToggleFavorite),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = null,
                            tint     = if (isFavorite) RelaxRed else RelaxTextSecondary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text     = product.name,
                    style    = MaterialTheme.typography.titleSmall,
                    color    = RelaxTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                )
                if (!product.weight.isNullOrEmpty()) {
                    Text(
                        text  = product.weight ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = RelaxTextSecondary,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier            = Modifier.fillMaxWidth(),
                    verticalAlignment   = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            text       = "${product.price.toInt()} TJS",
                            style      = MaterialTheme.typography.titleMedium,
                            color      = RelaxTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 16.sp,
                        )
                        if (product.oldPrice != null) {
                            Text(
                                text           = "${product.oldPrice.toInt()} TJS",
                                style          = MaterialTheme.typography.bodySmall,
                                color          = RelaxTextHint,
                                textDecoration = TextDecoration.LineThrough,
                            )
                        }
                    }
                    if (quantity == 0) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(RelaxDark)
                                .clickable(onClick = onIncrease),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Rounded.Add, null, tint = RelaxWhite, modifier = Modifier.size(20.dp))
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(RelaxDark)
                                .padding(horizontal = 4.dp),
                        ) {
                            IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Rounded.Remove, null, tint = RelaxWhite, modifier = Modifier.size(14.dp))
                            }
                            Text("$quantity", color = RelaxWhite, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Rounded.Add, null, tint = RelaxWhite, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}