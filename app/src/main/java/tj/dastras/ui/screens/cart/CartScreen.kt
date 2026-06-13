package tj.dastras.ui.screens.cart

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import tj.dastras.R
import tj.dastras.data.CartItem
import tj.dastras.ui.components.RelaxDivider
import tj.dastras.ui.components.RelaxTopBar
import tj.dastras.ui.components.activityViewModel
import tj.dastras.ui.theme.*

@Composable
fun CartScreen(
    onBack: () -> Unit,
    onCheckout: () -> Unit,
    viewModel: CartViewModel = activityViewModel(),
) {
    val state         = viewModel.uiState
    val subtotal      = state.items.sumOf { it.product.price * it.quantity }
    val savings       = state.items.sumOf { ((it.product.oldPrice ?: it.product.price) - it.product.price) * it.quantity }
    val promoDiscount = if (state.promoApplied) subtotal * 0.05 else 0.0
    val bonusDiscount = if (state.useBonuses) minOf(state.bonusBalance, subtotal * 0.5) else 0.0
    val total         = subtotal - promoDiscount - bonusDiscount

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        Box(modifier = Modifier.fillMaxWidth().background(RelaxWhite)) {
            RelaxTopBar(
                title  = stringResource(R.string.cart_title),
                onBack = onBack,
                actions = {
                    if (state.items.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clear() }) {
                            Text(stringResource(R.string.cart_clear), color = RelaxRed, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            )
        }

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RelaxDark)
            }
            return@Column
        }

        if (state.items.isEmpty()) {
            EmptyCart()
            return@Column
        }

        LazyColumn(
            modifier            = Modifier.weight(1f),
            contentPadding      = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.items, key = { it.product.id }) { item ->
                CartItemCard(
                    item       = item,
                    onIncrease = { viewModel.increase(item.product.id) },
                    onDecrease = { viewModel.decrease(item.product.id) },
                    onRemove   = { viewModel.remove(item.product.id) },
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = RelaxWhite), elevation = CardDefaults.cardElevation(0.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.cart_promo_code_title), style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary)
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value         = state.promoCode,
                                onValueChange = { viewModel.setPromoCode(it.uppercase()) },
                                placeholder   = { Text(stringResource(R.string.cart_promo_code_placeholder), color = RelaxTextHint, fontSize = 14.sp) },
                                singleLine    = true,
                                modifier      = Modifier.weight(1f).height(50.dp),
                                shape         = RoundedCornerShape(12.dp),
                                colors        = OutlinedTextFieldDefaults.colors(focusedBorderColor = RelaxDark, unfocusedBorderColor = RelaxDivider, focusedContainerColor = RelaxWhite, unfocusedContainerColor = RelaxInputBg),
                                textStyle     = MaterialTheme.typography.bodyMedium.copy(color = RelaxTextPrimary, fontWeight = FontWeight.Bold),
                            )
                            Button(onClick = { viewModel.togglePromo() }, shape = RoundedCornerShape(12.dp), modifier = Modifier.height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = RelaxDark)) {
                                Text(if (state.promoApplied) "✓" else stringResource(R.string.cart_promo_ok), fontWeight = FontWeight.Bold)
                            }
                        }
                        if (state.promoApplied) {
                            Spacer(Modifier.height(8.dp))
                            Text(stringResource(R.string.cart_promo_applied), color = RelaxSuccess, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = RelaxWhite), elevation = CardDefaults.cardElevation(0.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(RelaxGoldLight), contentAlignment = Alignment.Center) {
                            Text("⭐", fontSize = 22.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.cart_bonus_points_title), style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary)
                            Text(stringResource(R.string.cart_bonus_available, state.bonusBalance.toInt()), style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                        }
                        Switch(checked = state.useBonuses, onCheckedChange = { viewModel.toggleBonuses(it) }, colors = SwitchDefaults.colors(checkedThumbColor = RelaxWhite, checkedTrackColor = RelaxDark, uncheckedThumbColor = RelaxWhite, uncheckedTrackColor = RelaxDivider))
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = RelaxWhite), elevation = CardDefaults.cardElevation(0.dp)) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(stringResource(R.string.cart_order_summary_title), style = MaterialTheme.typography.titleMedium, color = RelaxTextPrimary, fontWeight = FontWeight.Bold)
                        SummaryRow(stringResource(R.string.cart_items_count, state.items.size), "${subtotal.toInt()} TJS")
                        if (savings > 0) SummaryRow(stringResource(R.string.cart_discount), "−${savings.toInt()} TJS", RelaxSuccess)
                        if (state.promoApplied) SummaryRow(stringResource(R.string.cart_promo_discount), "−${promoDiscount.toInt()} TJS", RelaxSuccess)
                        if (state.useBonuses) SummaryRow(stringResource(R.string.cart_bonus_discount), "−${bonusDiscount.toInt()} TJS", Color(0xFFD4AF37))
                        RelaxDivider()
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.cart_total_to_pay), style = MaterialTheme.typography.headlineSmall, color = RelaxTextPrimary)
                            Text("${total.toInt()} TJS", fontSize = 26.sp, fontWeight = FontWeight.Black, color = RelaxTextPrimary)
                        }
                        if (savings + promoDiscount + bonusDiscount > 0) {
                            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(RelaxSuccessBg).padding(12.dp)) {
                                Text(stringResource(R.string.cart_savings_message, (savings + promoDiscount + bonusDiscount).toInt()), color = RelaxSuccess, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().background(RelaxWhite).navigationBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp)) {
            Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = RelaxRed)) {
                Text(stringResource(R.string.cart_checkout_button, total.toInt()), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun CartItemCard(item: CartItem, onIncrease: () -> Unit, onDecrease: () -> Unit, onRemove: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = RelaxWhite), elevation = CardDefaults.cardElevation(0.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(14.dp)).background(RelaxInputBg)) {
                AsyncImage(model = item.product.imageUrl, contentDescription = item.product.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.product.name, style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary, maxLines = 2)
                if (!item.product.weight.isNullOrEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(item.product.weight ?: "", style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("${(item.product.price * item.quantity).toInt()} TJS", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RelaxTextPrimary)
                        if (item.product.oldPrice != null) {
                            Text("${(item.product.oldPrice * item.quantity).toInt()} TJS", style = MaterialTheme.typography.bodySmall, color = RelaxTextHint, textDecoration = TextDecoration.LineThrough)
                        }
                    }
                    Row(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(RelaxSurfaceAlt).padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                            Icon(if (item.quantity == 1) Icons.Rounded.DeleteOutline else Icons.Rounded.Remove, null, tint = RelaxTextPrimary, modifier = Modifier.size(14.dp))
                        }
                        Text("${item.quantity}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = RelaxTextPrimary, modifier = Modifier.widthIn(min = 20.dp))
                        IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Rounded.Add, null, tint = RelaxTextPrimary, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, valueColor: Color = RelaxTextSecondary) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = valueColor, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun EmptyCart() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🛒", fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.cart_empty_title), style = MaterialTheme.typography.headlineSmall, color = RelaxTextPrimary)
            Text(stringResource(R.string.cart_empty_subtitle), style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
        }
    }
}

private val RelaxSuccessBg = Color(0xFFDCFCE7)
