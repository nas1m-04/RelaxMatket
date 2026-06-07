package tj.dastras.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import tj.dastras.data.Product
import tj.dastras.ui.theme.*

// ── Primary Button ─────────────────────────────────────────────
@Composable
fun RelaxButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    Button(
        onClick   = onClick,
        enabled   = enabled && !isLoading,
        modifier  = modifier.height(56.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = ButtonDefaults.buttonColors(
            containerColor = RelaxRed,
            contentColor   = RelaxWhite,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color    = RelaxWhite,
                strokeWidth = 2.dp,
            )
        } else {
            Text(
                text       = text,
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

// ── Text Field ────────────────────────────────────────────────
@Composable
fun RelaxTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    singleLine: Boolean = true,
    keyboardType: androidx.compose.ui.text.input.KeyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value          = value,
            onValueChange  = onValueChange,
            placeholder    = {
                Text(
                    placeholder,
                    color = RelaxTextHint,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            leadingIcon    = if (leadingIcon != null) ({
                Icon(leadingIcon, null, tint = RelaxTextSecondary, modifier = Modifier.size(20.dp))
            }) else null,
            trailingIcon   = trailingIcon,
            isError        = isError,
            singleLine     = singleLine,
            modifier       = Modifier.fillMaxWidth().height(56.dp),
            shape          = RoundedCornerShape(14.dp),
            colors         = OutlinedTextFieldDefaults.colors(
                focusedContainerColor    = RelaxWhite,
                unfocusedContainerColor  = RelaxInputBg,
                focusedBorderColor       = RelaxDark,
                unfocusedBorderColor     = RelaxDivider,
                errorBorderColor         = RelaxError,
                focusedTextColor         = RelaxTextPrimary,
                unfocusedTextColor       = RelaxTextPrimary,
            ),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
            textStyle      = MaterialTheme.typography.bodyLarge,
        )
        if (isError && errorMessage.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(errorMessage, color = RelaxError, style = MaterialTheme.typography.bodySmall)
        }
    }
}

// ── Search Bar ────────────────────────────────────────────────
@Composable
fun RelaxSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Поиск товаров...",
    onSearch: () -> Unit = {},
) {
    OutlinedTextField(
        value         = query,
        onValueChange = onQueryChange,
        placeholder   = {
            Text(placeholder, color = RelaxTextHint, style = MaterialTheme.typography.bodyLarge)
        },
        leadingIcon   = {
            Icon(Icons.Rounded.Search, null, tint = RelaxTextSecondary, modifier = Modifier.size(22.dp))
        },
        trailingIcon  = if (query.isNotEmpty()) ({
            IconButton(onClick = { onQueryChange("") }) {
                Icon(Icons.Rounded.Clear, null, tint = RelaxTextSecondary, modifier = Modifier.size(18.dp))
            }
        }) else null,
        singleLine    = true,
        modifier      = modifier.fillMaxWidth().height(52.dp),
        shape         = RoundedCornerShape(16.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedContainerColor   = RelaxWhite,
            unfocusedContainerColor = RelaxWhite,
            focusedBorderColor      = RelaxDark,
            unfocusedBorderColor    = Color.Transparent,
            focusedTextColor        = RelaxTextPrimary,
            unfocusedTextColor      = RelaxTextPrimary,
        ),
        textStyle     = MaterialTheme.typography.bodyLarge,
    )
}

// ── Section Header ─────────────────────────────────────────────
@Composable
fun SectionHeader(
    title: String,
    onSeeAll: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier            = modifier.fillMaxWidth(),
        verticalAlignment   = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text  = title,
            style = MaterialTheme.typography.headlineSmall,
            color = RelaxTextPrimary,
        )
        if (onSeeAll != null) {
            TextButton(onClick = onSeeAll) {
                Text(
                    text  = "Все",
                    style = MaterialTheme.typography.labelLarge,
                    color = RelaxRed,
                )
            }
        }
    }
}

// ── Product Card (Grid) ────────────────────────────────────────
@Composable
fun ProductCardGrid(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var count by remember { mutableStateOf(product.cartCount) }

    Card(
        onClick   = onClick,
        modifier  = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp), spotColor = RelaxShadow),
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
                if (product.weight.isNotEmpty()) {
                    Text(
                        text  = product.weight,
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
                            text       = "${product.price.toInt()} ₽",
                            style      = MaterialTheme.typography.titleMedium,
                            color      = RelaxTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 16.sp,
                        )
                        if (product.oldPrice != null) {
                            Text(
                                text           = "${product.oldPrice.toInt()} ₽",
                                style          = MaterialTheme.typography.bodySmall,
                                color          = RelaxTextHint,
                                textDecoration = TextDecoration.LineThrough,
                            )
                        }
                    }
                    if (count == 0) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(RelaxDark)
                                .clickable { count = 1; onAddToCart() },
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
                            IconButton(onClick = { if (count > 0) count-- }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Rounded.Remove, null, tint = RelaxWhite, modifier = Modifier.size(14.dp))
                            }
                            Text("$count", color = RelaxWhite, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { count++ }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Rounded.Add, null, tint = RelaxWhite, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

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
                        Text("${product.price.toInt()} ₽", style = MaterialTheme.typography.labelLarge, color = RelaxTextPrimary, fontWeight = FontWeight.Bold)
                        if (product.oldPrice != null) {
                            Text("${product.oldPrice.toInt()} ₽", style = MaterialTheme.typography.labelSmall, color = RelaxTextHint, textDecoration = TextDecoration.LineThrough)
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
fun PriceTag(price: Double, oldPrice: Double? = null, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text       = "${price.toInt()} ₽",
            style      = MaterialTheme.typography.headlineMedium,
            color      = RelaxTextPrimary,
            fontWeight = FontWeight.Bold,
        )
        if (oldPrice != null) {
            Text(
                text           = "${oldPrice.toInt()} ₽",
                style          = MaterialTheme.typography.bodyMedium,
                color          = RelaxTextHint,
                textDecoration = TextDecoration.LineThrough,
            )
        }
    }
}

// ── Rating Row ────────────────────────────────────────────────
@Composable
fun RatingRow(rating: Float, reviewCount: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(Icons.Rounded.Star, null, tint = Color(0xFFFBBC04), modifier = Modifier.size(16.dp))
        Text("$rating", style = MaterialTheme.typography.labelMedium, color = RelaxTextPrimary, fontWeight = FontWeight.Bold)
        Text("($reviewCount отзывов)", style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
    }
}

// ── Top App Bar ────────────────────────────────────────────────
@Composable
fun RelaxTopBar(
    title: String,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick  = onBack,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(RelaxSurfaceAlt)
        ) {
            Icon(Icons.Rounded.ArrowBackIosNew, null, tint = RelaxTextPrimary, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text      = title,
            style     = MaterialTheme.typography.headlineSmall,
            color     = RelaxTextPrimary,
            modifier  = Modifier.weight(1f),
        )
        actions()
    }
}

// ── Divider ───────────────────────────────────────────────────
@Composable
fun RelaxDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier, color = RelaxDivider, thickness = 1.dp)
}
