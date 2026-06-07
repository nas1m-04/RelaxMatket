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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import tj.dastras.data.CartItem
import tj.dastras.data.MockData
import tj.dastras.ui.components.RelaxDivider
import tj.dastras.ui.components.RelaxTopBar
import tj.dastras.ui.theme.*

@Composable
fun CartScreen(onBack: () -> Unit, onCheckout: () -> Unit) {
    var cartItems by remember {
        mutableStateOf(
            listOf(
                CartItem(MockData.products[0], 1),
                CartItem(MockData.products[4], 2),
                CartItem(MockData.products[6], 1),
                CartItem(MockData.products[3], 1),
            )
        )
    }
    var promoCode       by remember { mutableStateOf("") }
    var promoApplied    by remember { mutableStateOf(false) }
    var useBonuses      by remember { mutableStateOf(false) }

    val subtotal        = cartItems.sumOf { it.product.price * it.quantity }
    val savings         = cartItems.sumOf { ((it.product.oldPrice ?: it.product.price) - it.product.price) * it.quantity }
    val promoDiscount   = if (promoApplied) subtotal * 0.05 else 0.0
    val bonusDiscount   = if (useBonuses) minOf(MockData.currentUser.bonusBalance.toDouble(), subtotal * 0.5) else 0.0
    val total           = subtotal - promoDiscount - bonusDiscount

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        // Top bar
        Box(modifier = Modifier.fillMaxWidth().background(RelaxWhite)) {
            RelaxTopBar(
                title  = "Корзина",
                onBack = onBack,
                actions = {
                    if (cartItems.isNotEmpty()) {
                        TextButton(onClick = { cartItems = emptyList() }) {
                            Text("Очистить", color = RelaxRed, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            )
        }

        if (cartItems.isEmpty()) {
            EmptyCart()
            return@Column
        }

        LazyColumn(
            modifier            = Modifier.weight(1f),
            contentPadding      = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Cart items
            items(cartItems, key = { it.product.id }) { item ->
                CartItemCard(
                    item       = item,
                    onIncrease = {
                        cartItems = cartItems.map {
                            if (it.product.id == item.product.id) it.copy(quantity = it.quantity + 1) else it
                        }
                    },
                    onDecrease = {
                        cartItems = cartItems.mapNotNull {
                            if (it.product.id == item.product.id) {
                                if (it.quantity > 1) it.copy(quantity = it.quantity - 1) else null
                            } else it
                        }
                    },
                    onRemove   = { cartItems = cartItems.filter { it.product.id != item.product.id } }
                )
            }

            // Promo code
            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(18.dp),
                    colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
                    elevation = CardDefaults.cardElevation(0.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Промокод", style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary)
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value         = promoCode,
                                onValueChange = { promoCode = it.uppercase() },
                                placeholder   = { Text("Введите промокод", color = RelaxTextHint, fontSize = 14.sp) },
                                singleLine    = true,
                                modifier      = Modifier.weight(1f).height(50.dp),
                                shape         = RoundedCornerShape(12.dp),
                                colors        = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor   = RelaxDark,
                                    unfocusedBorderColor = RelaxDivider,
                                    focusedContainerColor    = RelaxWhite,
                                    unfocusedContainerColor  = RelaxInputBg,
                                ),
                                textStyle     = MaterialTheme.typography.bodyMedium.copy(color = RelaxTextPrimary, fontWeight = FontWeight.Bold),
                            )
                            Button(
                                onClick  = { if (promoCode.isNotEmpty()) promoApplied = !promoApplied },
                                shape    = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(50.dp),
                                colors   = ButtonDefaults.buttonColors(containerColor = RelaxDark),
                            ) {
                                Text(if (promoApplied) "✓" else "ОК", fontWeight = FontWeight.Bold)
                            }
                        }
                        if (promoApplied) {
                            Spacer(Modifier.height(8.dp))
                            Text("✓ Промокод применён — скидка 5%", color = RelaxSuccess, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Bonus toggle
            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(18.dp),
                    colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
                    elevation = CardDefaults.cardElevation(0.dp),
                ) {
                    Row(
                        modifier          = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(RelaxGoldLight),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("⭐", fontSize = 22.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Бонусные баллы", style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary)
                            Text("Доступно: ${MockData.currentUser.bonusBalance} ₽", style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                        }
                        Switch(
                            checked       = useBonuses,
                            onCheckedChange = { useBonuses = it },
                            colors        = SwitchDefaults.colors(
                                checkedThumbColor   = RelaxWhite,
                                checkedTrackColor   = RelaxDark,
                                uncheckedThumbColor = RelaxWhite,
                                uncheckedTrackColor = RelaxDivider,
                            )
                        )
                    }
                }
            }

            // Summary
            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(18.dp),
                    colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
                    elevation = CardDefaults.cardElevation(0.dp),
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Итог заказа", style = MaterialTheme.typography.titleMedium, color = RelaxTextPrimary, fontWeight = FontWeight.Bold)

                        SummaryRow("Товары (${cartItems.size})", "${subtotal.toInt()} ₽")
                        if (savings > 0) SummaryRow("Скидка", "−${savings.toInt()} ₽", RelaxSuccess)
                        if (promoApplied) SummaryRow("Промокод (−5%)", "−${promoDiscount.toInt()} ₽", RelaxSuccess)
                        if (useBonuses) SummaryRow("Бонусы", "−${bonusDiscount.toInt()} ₽", Color(0xFFD4AF37))

                        RelaxDivider()

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("К оплате", style = MaterialTheme.typography.headlineSmall, color = RelaxTextPrimary)
                            Text("${total.toInt()} ₽", fontSize = 26.sp, fontWeight = FontWeight.Black, color = RelaxTextPrimary)
                        }

                        if (savings + promoDiscount + bonusDiscount > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(RelaxSuccessBg)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    "🎉 Ваша экономия: ${(savings + promoDiscount + bonusDiscount).toInt()} ₽",
                                    color = RelaxSuccess,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }
                }
            }
        }

        // Checkout button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(RelaxWhite)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Button(
                onClick   = onCheckout,
                modifier  = Modifier.fillMaxWidth().height(56.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = ButtonDefaults.buttonColors(containerColor = RelaxRed),
            ) {
                Text("Оформить заказ · ${total.toInt()} ₽", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun CartItemCard(item: CartItem, onIncrease: () -> Unit, onDecrease: () -> Unit, onRemove: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(RelaxInputBg)
            ) {
                AsyncImage(
                    model             = item.product.imageUrl,
                    contentDescription = item.product.name,
                    contentScale      = ContentScale.Crop,
                    modifier          = Modifier.fillMaxSize(),
                )
            }
            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.product.name, style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                if (item.product.weight.isNotEmpty()) {
                    Text(item.product.weight, style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                }
                Spacer(Modifier.height(8.dp))
                // Price + controls
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            "${(item.product.price * item.quantity).toInt()} ₽",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 16.sp,
                            color      = RelaxTextPrimary,
                        )
                        if (item.product.oldPrice != null) {
                            Text(
                                "${(item.product.oldPrice * item.quantity).toInt()} ₽",
                                style          = MaterialTheme.typography.bodySmall,
                                color          = RelaxTextHint,
                                textDecoration = TextDecoration.LineThrough,
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(RelaxSurfaceAlt)
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
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
            Text("Корзина пуста", style = MaterialTheme.typography.headlineSmall, color = RelaxTextPrimary)
            Text("Добавьте товары из каталога", style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
        }
    }
}

private val RelaxSuccessBg = Color(0xFFDCFCE7)
