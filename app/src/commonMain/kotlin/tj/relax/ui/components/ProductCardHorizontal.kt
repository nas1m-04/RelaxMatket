package tj.relax.ui.components

import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import coil3.compose.AsyncImage
import tj.relax.generated.resources.*
import tj.relax.data.Product
import tj.relax.ui.theme.RelaxDark
import tj.relax.ui.theme.RelaxGold
import tj.relax.ui.theme.RelaxRed
import tj.relax.ui.theme.RelaxShadow
import tj.relax.ui.theme.RelaxTextHint
import tj.relax.ui.theme.RelaxTextPrimary
import tj.relax.ui.theme.RelaxWhite

// ── Product Card (Horizontal) ──────────────────────────────────
@Composable
fun ProductCardHorizontal(
    product: Product,
    onClick: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    quantity: Int = 0,
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
                    .background(Color(0xFFECEEF1))
            ) {
                AsyncImage(
                    model             = product.imageUrl,
                    contentDescription = product.name,
                    contentScale      = ContentScale.Crop,
                    modifier          = Modifier.fillMaxSize(),
                )
                if (product.hasCardDiscount) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(RelaxGold)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(stringResource(Res.string.product_card_price_badge), color = RelaxWhite, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                    }
                } else if (product.oldPrice != null) {
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
                        Text(
                            "${product.effectivePrice.toInt()} TJS",
                            style = MaterialTheme.typography.labelLarge,
                            color = when {
                                product.hasCardDiscount -> RelaxGold
                                product.oldPrice != null -> RelaxRed
                                else -> RelaxTextPrimary
                            },
                            fontWeight = FontWeight.Bold
                        )
                        if (product.hasCardDiscount) {
                            Text("${product.price.toInt()} TJS", style = MaterialTheme.typography.labelSmall, color = RelaxTextHint, textDecoration = TextDecoration.LineThrough)
                        } else if (product.oldPrice != null) {
                            Text("${product.oldPrice.toInt()} TJS", style = MaterialTheme.typography.labelSmall, color = RelaxTextHint, textDecoration = TextDecoration.LineThrough)
                        }
                    }
                    // Add-to-cart stepper — disabled, no delivery/pickup cart flow
                    /*
                    if (quantity == 0) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(RelaxDark)
                                .clickable(onClick = onIncrease),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Rounded.Add, null, tint = RelaxWhite, modifier = Modifier.size(14.dp))
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(RelaxDark)
                                .padding(horizontal = 2.dp),
                        ) {
                            IconButton(onClick = onDecrease, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Rounded.Remove, null, tint = RelaxWhite, modifier = Modifier.size(12.dp))
                            }
                            Text("$quantity", color = RelaxWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            IconButton(onClick = onIncrease, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Rounded.Add, null, tint = RelaxWhite, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                    */
                }
            }
        }
    }
}