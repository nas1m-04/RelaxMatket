package tj.relax.ui.components

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
import androidx.compose.ui.res.stringResource
import tj.relax.R
import tj.relax.data.Product
import tj.relax.ui.theme.RelaxDark
import tj.relax.ui.theme.RelaxGold
import tj.relax.ui.theme.RelaxRed
import tj.relax.ui.theme.RelaxTextHint
import tj.relax.ui.theme.RelaxTextPrimary
import tj.relax.ui.theme.RelaxTextSecondary
import tj.relax.ui.theme.RelaxWhite

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
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = RelaxWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {

        Column {

            // ── IMAGE ─────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(Color(0xFFECEEF1))
            ) {

                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Card-discount badge takes priority over a generic sale badge — it's the more
                // relevant, personalized signal when both happen to be set on the same product.
                if (product.hasCardDiscount) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(RelaxGold)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            stringResource(R.string.product_card_price_badge),
                            color = RelaxWhite,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else product.oldPrice?.let {
                    val disc = ((1 - product.price / it) * 100).toInt()

                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            "−$disc%",
                            color = RelaxWhite,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // NEW badge (спокойный акцент)
                if (product.isNew) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(RelaxDark.copy(alpha = 0.8f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            "NEW",
                            color = RelaxWhite,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Favorite
                if (onToggleFavorite != null) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                            .align(Alignment.BottomEnd)
                            .clickable(onClick = onToggleFavorite),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFavorite)
                                Icons.Rounded.Favorite
                            else
                                Icons.Rounded.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isFavorite) RelaxRed else RelaxTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // ── CONTENT ───────────────────────────
            Column(modifier = Modifier.padding(12.dp)) {

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = RelaxTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                product.weight?.takeIf { it.isNotEmpty() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = RelaxTextSecondary
                    )
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {
                        Text(
                            text = "${product.effectivePrice.toInt()} TJS",
                            style = MaterialTheme.typography.titleMedium,
                            color = when {
                                product.hasCardDiscount -> RelaxGold
                                product.oldPrice != null -> RelaxRed
                                else -> RelaxTextPrimary
                            },
                            fontWeight = FontWeight.Bold
                        )

                        if (product.hasCardDiscount) {
                            Text(
                                text = "${product.price.toInt()} TJS",
                                style = MaterialTheme.typography.bodySmall,
                                color = RelaxTextHint,
                                textDecoration = TextDecoration.LineThrough
                            )
                        } else product.oldPrice?.let {
                            Text(
                                text = "${it.toInt()} TJS",
                                style = MaterialTheme.typography.bodySmall,
                                color = RelaxTextHint,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }

                    // ── ADD BUTTON — disabled, no delivery/pickup cart flow ──
                    /*
                    if (quantity == 0) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(RelaxDark)
                                .clickable(onClick = onIncrease),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.Add,
                                contentDescription = null,
                                tint = RelaxWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(RelaxDark)
                                .padding(horizontal = 6.dp)
                        ) {
                            IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Rounded.Remove, null, tint = RelaxWhite, modifier = Modifier.size(14.dp))
                            }

                            Text(
                                "$quantity",
                                color = RelaxWhite,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Rounded.Add, null, tint = RelaxWhite, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    */
                }
            }
        }
    }
}