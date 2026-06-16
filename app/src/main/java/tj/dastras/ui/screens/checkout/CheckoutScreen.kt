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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import tj.dastras.R
import tj.dastras.data.OrderItemRequest
import tj.dastras.ui.components.RelaxTopBar
import tj.dastras.ui.components.activityViewModel
import tj.dastras.ui.screens.cart.CartViewModel
import tj.dastras.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    cartViewModel: CartViewModel = activityViewModel(),
    viewModel: CheckoutViewModel = activityViewModel(),
) {
    val cartState = cartViewModel.uiState
    val state     = viewModel.uiState

    val subtotal      = cartState.items.sumOf { it.product.price * it.quantity }
    val promoDiscount = if (cartState.promoApplied) subtotal * 0.05 else 0.0
    val bonusDiscount = if (cartState.useBonuses) {
        minOf(cartState.bonusBalance * cartState.bonusToCurrencyRate, subtotal * cartState.maxBonusPaymentPercent / 100.0, subtotal)
    } else 0.0
    val total = subtotal - promoDiscount - bonusDiscount

    val timeSlots = listOf(
        stringResource(R.string.checkout_slot_today_1),
        stringResource(R.string.checkout_slot_today_2),
        stringResource(R.string.checkout_slot_tomorrow_1),
        stringResource(R.string.checkout_slot_tomorrow_2),
    )
    val payments = listOf(
        Triple("card_on_delivery", Icons.Rounded.CreditCard, stringResource(R.string.checkout_payment_card_title) to stringResource(R.string.checkout_payment_card_sub)),
        Triple("cash", Icons.Rounded.Money,       stringResource(R.string.checkout_payment_cash_title) to stringResource(R.string.checkout_payment_cash_sub)),
    )
    val defaultAddress = stringResource(R.string.checkout_delivery_address_value)

    LaunchedEffect(Unit) {
        if (state.timeSlot == null) viewModel.setTimeSlot(timeSlots.first())
    }
    LaunchedEffect(state.branches) {
        if (state.address.isBlank()) {
            val branchAddress = state.branches.firstOrNull { it.id == state.selectedBranchId }?.address
            viewModel.setAddress(branchAddress ?: defaultAddress)
        }
    }

    if (state.order != null) {
        OrderSuccessScreen(
            bonusEarned = state.order.bonusEarned.toInt(),
            onDone = {
                cartViewModel.clear()
                viewModel.reset()
                onSuccess()
            },
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        Box(modifier = Modifier.background(RelaxWhite)) {
            RelaxTopBar(title = stringResource(R.string.checkout_title), onBack = onBack)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Delivery type toggle
            SectionCard(title = stringResource(R.string.checkout_delivery_method_title)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(RelaxSurfaceAlt)
                        .padding(4.dp),
                ) {
                    listOf(
                        "delivery" to stringResource(R.string.checkout_delivery_option),
                        "pickup"   to stringResource(R.string.checkout_pickup_option),
                    ).forEach { (type, label) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (state.deliveryType == type) RelaxDark else Color.Transparent)
                                .clickable { viewModel.setDeliveryType(type) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                label,
                                color      = if (state.deliveryType == type) RelaxWhite else RelaxTextSecondary,
                                fontWeight = if (state.deliveryType == type) FontWeight.Bold else FontWeight.Normal,
                                fontSize   = 14.sp,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))

                if (state.deliveryType == "delivery") {
                    var branchMenuExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded         = branchMenuExpanded,
                        onExpandedChange = { if (state.branches.isNotEmpty()) branchMenuExpanded = it },
                    ) {
                        OutlinedTextField(
                            value         = state.address,
                            onValueChange = { viewModel.setAddress(it) },
                            label         = { Text(stringResource(R.string.checkout_delivery_address_label), color = RelaxTextSecondary) },
                            leadingIcon   = { Icon(Icons.Rounded.LocationOn, null, tint = RelaxRed) },
                            trailingIcon  = {
                                if (state.branches.isNotEmpty()) {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = branchMenuExpanded)
                                }
                            },
                            singleLine    = true,
                            modifier      = Modifier.fillMaxWidth().menuAnchor(),
                            shape         = RoundedCornerShape(14.dp),
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor      = RelaxDark,
                                unfocusedBorderColor    = RelaxDivider,
                                focusedContainerColor   = RelaxWhite,
                                unfocusedContainerColor = RelaxInputBg,
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = RelaxTextPrimary, fontWeight = FontWeight.Medium),
                        )
                        ExposedDropdownMenu(
                            expanded         = branchMenuExpanded,
                            onDismissRequest = { branchMenuExpanded = false },
                        ) {
                            state.branches.forEach { branch ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(branch.name, style = MaterialTheme.typography.bodyMedium, color = RelaxTextPrimary, fontWeight = FontWeight.Medium)
                                            Text(branch.address, style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                                        }
                                    },
                                    onClick = {
                                        viewModel.setBranch(branch.id)
                                        branchMenuExpanded = false
                                    },
                                )
                            }
                        }
                    }
                } else {
                    var branchMenuExpanded by remember { mutableStateOf(false) }
                    val selectedBranch = state.branches.firstOrNull { it.id == state.selectedBranchId }
                    ExposedDropdownMenuBox(
                        expanded         = branchMenuExpanded,
                        onExpandedChange = { if (state.branches.isNotEmpty()) branchMenuExpanded = it },
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .clip(RoundedCornerShape(14.dp))
                                .background(RelaxInputBg)
                                .clickable(enabled = state.branches.isNotEmpty()) { branchMenuExpanded = true }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Rounded.Store, null, tint = RelaxDark, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(stringResource(R.string.checkout_pickup_store_label), style = MaterialTheme.typography.labelSmall, color = RelaxTextSecondary)
                                Text(
                                    selectedBranch?.name ?: stringResource(R.string.checkout_pickup_store_value),
                                    style = MaterialTheme.typography.bodyMedium, color = RelaxTextPrimary, fontWeight = FontWeight.Medium,
                                )
                                selectedBranch?.let {
                                    Text(it.address, style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                                }
                            }
                            if (state.branches.isNotEmpty()) {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = branchMenuExpanded)
                            }
                        }
                        ExposedDropdownMenu(
                            expanded         = branchMenuExpanded,
                            onDismissRequest = { branchMenuExpanded = false },
                        ) {
                            state.branches.forEach { branch ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(branch.name, style = MaterialTheme.typography.bodyMedium, color = RelaxTextPrimary, fontWeight = FontWeight.Medium)
                                            Text(branch.address, style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                                        }
                                    },
                                    onClick = {
                                        viewModel.setBranch(branch.id)
                                        branchMenuExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }
            }

            // Time slot
            SectionCard(title = stringResource(R.string.checkout_time_slot_title)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    timeSlots.forEach { slot ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (state.timeSlot == slot) RelaxDark.copy(alpha = 0.06f) else Color.Transparent)
                                .border(
                                    width = 1.5.dp,
                                    color = if (state.timeSlot == slot) RelaxDark else RelaxDivider,
                                    shape = RoundedCornerShape(12.dp),
                                )
                                .clickable { viewModel.setTimeSlot(slot) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = state.timeSlot == slot,
                                onClick  = { viewModel.setTimeSlot(slot) },
                                colors   = RadioButtonDefaults.colors(selectedColor = RelaxDark),
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(slot, style = MaterialTheme.typography.bodyMedium, color = RelaxTextPrimary, fontWeight = if (state.timeSlot == slot) FontWeight.SemiBold else FontWeight.Normal)
                        }
                    }
                }
            }

            // Payment method
            SectionCard(title = stringResource(R.string.checkout_payment_method_title)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    payments.forEach { (value, icon, texts) ->
                        val (title, sub) = texts
                        val selected = state.paymentMethod == value
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) RelaxDark.copy(alpha = 0.06f) else Color.Transparent)
                                .border(
                                    width = 1.5.dp,
                                    color = if (selected) RelaxDark else RelaxDivider,
                                    shape = RoundedCornerShape(12.dp),
                                )
                                .clickable { viewModel.setPaymentMethod(value) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) RelaxDark else RelaxSurfaceAlt),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(icon, null, tint = if (selected) RelaxWhite else RelaxTextSecondary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(title, style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary)
                                Text(sub,   style = MaterialTheme.typography.bodySmall,  color = RelaxTextSecondary)
                            }
                            if (selected) {
                                Icon(Icons.Rounded.CheckCircle, null, tint = RelaxDark, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            // Comment
            SectionCard(title = stringResource(R.string.checkout_comment_title)) {
                OutlinedTextField(
                    value         = state.comment,
                    onValueChange = { viewModel.setComment(it) },
                    placeholder   = { Text(stringResource(R.string.checkout_comment_placeholder), color = RelaxTextHint, fontSize = 14.sp) },
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
                    Text(stringResource(R.string.checkout_total_label), style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
                    Text("${total.toInt()} TJS", style = MaterialTheme.typography.titleLarge, color = RelaxTextPrimary, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick  = {
                        val items = cartState.items.map { OrderItemRequest(productId = it.product.id, quantity = it.quantity) }
                        val promoCode = if (cartState.promoApplied) cartState.promoCode else null
                        viewModel.submitOrder(items, cartState.useBonuses, promoCode)
                    },
                    enabled   = !state.isSubmitting && cartState.items.isNotEmpty(),
                    modifier  = Modifier.fillMaxWidth().height(56.dp),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = ButtonDefaults.buttonColors(containerColor = RelaxRed),
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = RelaxWhite, strokeWidth = 2.dp)
                    } else {
                        Text(stringResource(R.string.checkout_confirm_button), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
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
private fun OrderSuccessScreen(bonusEarned: Int, onDone: () -> Unit) {
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
            Text(stringResource(R.string.checkout_success_title), style = MaterialTheme.typography.displaySmall, color = RelaxTextPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.checkout_success_subtitle), color = RelaxTextSecondary, style = MaterialTheme.typography.bodyLarge, textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 24.sp)
            Spacer(Modifier.height(32.dp))
            if (bonusEarned > 0) {
                Text(stringResource(R.string.checkout_bonus_earned, bonusEarned), color = Color(0xFFD4AF37), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(32.dp))
            }
            Button(
                onClick  = onDone,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = RelaxDark),
            ) {
                Text(stringResource(R.string.checkout_go_home), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

private val CircleShape = RoundedCornerShape(50)
