package tj.dastras.ui.screens.checkout

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import tj.dastras.ui.components.RelaxTopBar
import tj.dastras.ui.theme.*

@Composable
fun CheckoutScreen(onBack: () -> Unit, onSuccess: () -> Unit) {
    var deliveryType    by remember { mutableStateOf(0) } // 0=delivery, 1=pickup
    var selectedSlot    by remember { mutableStateOf(0) }
    var selectedPayment by remember { mutableStateOf(0) }
    var comment         by remember { mutableStateOf("") }
    var showSuccess     by remember { mutableStateOf(false) }

    val timeSlots = listOf("Сегодня 18:00 – 20:00", "Сегодня 20:00 – 22:00", "Завтра 10:00 – 12:00", "Завтра 14:00 – 16:00")
    val payments  = listOf(
        Triple(Icons.Rounded.CreditCard, "Картой при получении", "Любая карта"),
        Triple(Icons.Rounded.Phone,      "Онлайн-оплата",        "Visa, Mastercard, СБП"),
        Triple(Icons.Rounded.Money,      "Наличными",            "При получении"),
    )

    if (showSuccess) {
        OrderSuccessScreen(onDone = onSuccess)
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        Box(modifier = Modifier.background(RelaxWhite)) {
            RelaxTopBar(title = "Оформление заказа", onBack = onBack)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Delivery type toggle
            SectionCard(title = "Способ получения") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(RelaxSurfaceAlt)
                        .padding(4.dp),
                ) {
                    listOf("🚀 Доставка", "🏪 Самовывоз").forEachIndexed { idx, label ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (deliveryType == idx) RelaxDark else Color.Transparent)
                                .clickable { deliveryType = idx }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                label,
                                color      = if (deliveryType == idx) RelaxWhite else RelaxTextSecondary,
                                fontWeight = if (deliveryType == idx) FontWeight.Bold else FontWeight.Normal,
                                fontSize   = 14.sp,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))

                if (deliveryType == 0) {
                    // Address
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(RelaxInputBg)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.LocationOn, null, tint = RelaxRed, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Адрес доставки", style = MaterialTheme.typography.labelSmall, color = RelaxTextSecondary)
                            Text("ул. Ленина, 45, кв. 12", style = MaterialTheme.typography.bodyMedium, color = RelaxTextPrimary, fontWeight = FontWeight.Medium)
                        }
                        Icon(Icons.Rounded.Edit, null, tint = RelaxTextSecondary, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(RelaxInputBg)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.Store, null, tint = RelaxDark, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Магазин RELAX", style = MaterialTheme.typography.labelSmall, color = RelaxTextSecondary)
                            Text("ул. Центральная, 1", style = MaterialTheme.typography.bodyMedium, color = RelaxTextPrimary, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Time slot
            SectionCard(title = "Время получения") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    timeSlots.forEachIndexed { idx, slot ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedSlot == idx) RelaxDark.copy(alpha = 0.06f) else Color.Transparent)
                                .border(
                                    width = 1.5.dp,
                                    color = if (selectedSlot == idx) RelaxDark else RelaxDivider,
                                    shape = RoundedCornerShape(12.dp),
                                )
                                .clickable { selectedSlot = idx }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = selectedSlot == idx,
                                onClick  = { selectedSlot = idx },
                                colors   = RadioButtonDefaults.colors(selectedColor = RelaxDark),
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(slot, style = MaterialTheme.typography.bodyMedium, color = RelaxTextPrimary, fontWeight = if (selectedSlot == idx) FontWeight.SemiBold else FontWeight.Normal)
                        }
                    }
                }
            }

            // Payment method
            SectionCard(title = "Способ оплаты") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    payments.forEachIndexed { idx, (icon, title, sub) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedPayment == idx) RelaxDark.copy(alpha = 0.06f) else Color.Transparent)
                                .border(
                                    width = 1.5.dp,
                                    color = if (selectedPayment == idx) RelaxDark else RelaxDivider,
                                    shape = RoundedCornerShape(12.dp),
                                )
                                .clickable { selectedPayment = idx }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selectedPayment == idx) RelaxDark else RelaxSurfaceAlt),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(icon, null, tint = if (selectedPayment == idx) RelaxWhite else RelaxTextSecondary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(title, style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary)
                                Text(sub,   style = MaterialTheme.typography.bodySmall,  color = RelaxTextSecondary)
                            }
                            if (selectedPayment == idx) {
                                Icon(Icons.Rounded.CheckCircle, null, tint = RelaxDark, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            // Comment
            SectionCard(title = "Комментарий к заказу") {
                OutlinedTextField(
                    value         = comment,
                    onValueChange = { comment = it },
                    placeholder   = { Text("Например: оставить у двери, позвонить заранее...", color = RelaxTextHint, fontSize = 14.sp) },
                    modifier      = Modifier.fillMaxWidth().height(100.dp),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor       = RelaxDark,
                        unfocusedBorderColor     = RelaxDivider,
                        focusedContainerColor    = RelaxWhite,
                        unfocusedContainerColor  = RelaxInputBg,
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = RelaxTextPrimary),
                )
            }
        }

        // Confirm button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(RelaxWhite)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Итого", style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
                    Text("2 687 ₽", style = MaterialTheme.typography.titleLarge, color = RelaxTextPrimary, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick   = { showSuccess = true },
                    modifier  = Modifier.fillMaxWidth().height(56.dp),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = ButtonDefaults.buttonColors(containerColor = RelaxRed),
                ) {
                    Text("Подтвердить заказ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = RelaxTextPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun OrderSuccessScreen(onDone: () -> Unit) {
    Box(
        modifier          = Modifier.fillMaxSize().background(RelaxBackground),
        contentAlignment  = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDCFCE7)),
                contentAlignment = Alignment.Center,
            ) {
                Text("✅", fontSize = 56.sp)
            }
            Spacer(Modifier.height(24.dp))
            Text("Заказ оформлен!", style = MaterialTheme.typography.displaySmall, color = RelaxTextPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Мы уже готовим ваш заказ. Ожидайте доставку в выбранное время.", color = RelaxTextSecondary, style = MaterialTheme.typography.bodyLarge, textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 24.sp)
            Spacer(Modifier.height(32.dp))
            Text("🎁 Начислено 350 бонусов", color = Color(0xFFD4AF37), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(32.dp))
            Button(
                onClick  = onDone,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = RelaxDark),
            ) {
                Text("На главную", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

private val CircleShape = RoundedCornerShape(50)
