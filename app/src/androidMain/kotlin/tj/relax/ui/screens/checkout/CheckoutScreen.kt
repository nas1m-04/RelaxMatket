package tj.relax.ui.screens.checkout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import java.util.Calendar
import kotlin.math.abs
import tj.relax.generated.resources.*
import tj.relax.data.Branch
import tj.relax.data.OrderItemRequest
import tj.relax.ui.components.RelaxTopBar
import tj.relax.ui.components.activityViewModel
import tj.relax.ui.screens.cart.CartViewModel
import tj.relax.ui.theme.*

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

    val subtotal      = cartState.items.sumOf { it.product.effectivePrice * it.quantity }
    val promoDiscount = if (cartState.promoApplied) subtotal * 0.05 else 0.0
    val bonusDiscount = if (cartState.useBonuses) {
        minOf(cartState.bonusBalance * cartState.bonusToCurrencyRate, subtotal * cartState.maxBonusPaymentPercent / 100.0, subtotal)
    } else 0.0
    val total          = subtotal - promoDiscount - bonusDiscount
    val estimatedBonus = subtotal * state.cashbackPercent / 100.0

    val timeSlots = remember {
        val slots = mutableListOf<String>()
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val startHour = maxOf(9, currentHour + 1)
        for (h in startHour..21) slots.add("Сегодня, %02d:00".format(h))
        for (h in 9..21) slots.add("Завтра, %02d:00".format(h))
        slots
    }
    val payments = listOf(
        Triple("card_on_delivery", Icons.Rounded.CreditCard,
            stringResource(Res.string.checkout_payment_card_title) to stringResource(Res.string.checkout_payment_card_sub)),
        Triple("cash", Icons.Rounded.Money,
            stringResource(Res.string.checkout_payment_cash_title) to stringResource(Res.string.checkout_payment_cash_sub)),
    )

    LaunchedEffect(Unit) {
        if (state.timeSlot == null) viewModel.setTimeSlot(timeSlots.first())
    }

    if (state.order != null) {
        OrderSuccessScreen(
            bonusEarned = state.order.bonusEarned,
            onDone = {
                cartViewModel.clear()
                viewModel.reset()
                onSuccess()
            },
        )
        return
    }

    if (state.showAddressPrompt) {
        AddressPromptDialog(
            onConfirm = { addr ->
                val items = cartState.items.map { OrderItemRequest(productId = it.product.id, quantity = it.quantity) }
                val promoCode = if (cartState.promoApplied) cartState.promoCode else null
                viewModel.confirmAddressAndSubmit(addr, items, cartState.useBonuses, promoCode)
            },
            onDismiss = { viewModel.dismissAddressPrompt() },
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        Box(modifier = Modifier.background(RelaxWhite)) {
            RelaxTopBar(title = stringResource(Res.string.checkout_title), onBack = onBack)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            // ── 1. Delivery method ──────────────────────────────────────────
            SectionCard(title = stringResource(Res.string.checkout_delivery_method_title)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(RelaxSurfaceAlt)
                        .padding(4.dp),
                ) {
                    listOf(
                        "delivery" to stringResource(Res.string.checkout_delivery_option),
                        "pickup"   to stringResource(Res.string.checkout_pickup_option),
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

                // Delivery address — free text, shown only for delivery
                AnimatedVisibility(
                    visible = state.deliveryType == "delivery",
                    enter   = expandVertically(),
                    exit    = shrinkVertically(),
                ) {
                    Column {
                        Spacer(Modifier.height(14.dp))
                        OutlinedTextField(
                            value         = state.address,
                            onValueChange = { viewModel.setAddress(it) },
                            label         = { Text(stringResource(Res.string.checkout_delivery_address_label), color = RelaxTextSecondary) },
                            placeholder   = { Text(stringResource(Res.string.checkout_address_placeholder), color = RelaxTextHint, fontSize = 13.sp) },
                            leadingIcon   = { Icon(Icons.Rounded.LocationOn, null, tint = RelaxRed) },
                            singleLine    = true,
                            modifier      = Modifier.fillMaxWidth(),
                            shape         = RoundedCornerShape(14.dp),
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor      = RelaxDark,
                                unfocusedBorderColor    = RelaxDivider,
                                focusedContainerColor   = RelaxWhite,
                                unfocusedContainerColor = RelaxInputBg,
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color      = RelaxTextPrimary,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleSaveAddress() }
                                .padding(vertical = 4.dp, horizontal = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked        = state.saveAddress,
                                onCheckedChange = { viewModel.toggleSaveAddress() },
                                colors         = CheckboxDefaults.colors(
                                    checkedColor   = RelaxDark,
                                    uncheckedColor = RelaxTextHint,
                                ),
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                stringResource(Res.string.checkout_save_address),
                                style = MaterialTheme.typography.bodySmall,
                                color = RelaxTextSecondary,
                            )
                        }
                    }
                }
            }

            // ── 2. Time slot ────────────────────────────────────────────────
            SectionCard(title = stringResource(Res.string.checkout_time_slot_title)) {
                TimeSlotWheelPicker(
                    slots    = timeSlots,
                    selected = state.timeSlot,
                    onSelect = viewModel::setTimeSlot,
                )
            }

            // ── 3. Payment ──────────────────────────────────────────────────
            SectionCard(title = stringResource(Res.string.checkout_payment_method_title)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    payments.forEach { (value, icon, texts) ->
                        val (title, sub) = texts
                        val selected     = state.paymentMethod == value
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) RelaxDark.copy(alpha = 0.06f) else Color.Transparent)
                                .border(1.5.dp, if (selected) RelaxDark else RelaxDivider, RoundedCornerShape(12.dp))
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

            // ── 4. Comment ──────────────────────────────────────────────────
            SectionCard(title = stringResource(Res.string.checkout_comment_title)) {
                OutlinedTextField(
                    value         = state.comment,
                    onValueChange = { viewModel.setComment(it) },
                    placeholder   = { Text(stringResource(Res.string.checkout_comment_placeholder), color = RelaxTextHint, fontSize = 13.sp) },
                    modifier      = Modifier.fillMaxWidth().height(96.dp),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = RelaxDark,
                        unfocusedBorderColor    = RelaxDivider,
                        focusedContainerColor   = RelaxWhite,
                        unfocusedContainerColor = RelaxInputBg,
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = RelaxTextPrimary),
                )
            }

            // ── 5. Branch selection (bottom, beautiful dropdown) ────────────
            val branchTitle = if (state.deliveryType == "pickup")
                stringResource(Res.string.checkout_branch_pickup_title)
            else
                stringResource(Res.string.checkout_branch_delivery_title)

            SectionCard(title = branchTitle) {
                BranchDropdown(
                    branches         = state.branches,
                    selectedBranchId = state.selectedBranchId,
                    onSelect         = { viewModel.setBranch(it) },
                )
            }
        }

        // ── Bottom bar ──────────────────────────────────────────────────────
        Surface(
            modifier   = Modifier.fillMaxWidth(),
            color      = RelaxWhite,
            shadowElevation = 12.dp,
        ) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Bonus preview
                if (estimatedBonus > 0.0) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Stars, null, tint = Color(0xFFD4AF37), modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                stringResource(Res.string.checkout_bonus_preview_label),
                                style = MaterialTheme.typography.bodySmall,
                                color = RelaxTextSecondary,
                            )
                        }
                        Text(
                            stringResource(Res.string.checkout_bonus_preview_value, "%.2f".format(estimatedBonus)),
                            style      = MaterialTheme.typography.bodySmall,
                            color      = Color(0xFFD4AF37),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    HorizontalDivider(color = RelaxDivider)
                }

                // Total
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Text(stringResource(Res.string.checkout_total_label), style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
                    Text("${total.toInt()} TJS", style = MaterialTheme.typography.titleLarge, color = RelaxTextPrimary, fontWeight = FontWeight.Bold)
                }

                // Error
                state.error?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = RelaxRed, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }

                // Confirm button
                Button(
                    onClick  = {
                        val items     = cartState.items.map { OrderItemRequest(productId = it.product.id, quantity = it.quantity) }
                        val promoCode = if (cartState.promoApplied) cartState.promoCode else null
                        viewModel.submitOrder(items, cartState.useBonuses, promoCode)
                    },
                    enabled  = !state.isSubmitting && cartState.items.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = RelaxRed),
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = RelaxWhite, strokeWidth = 2.dp)
                    } else {
                        Text(stringResource(Res.string.checkout_confirm_button), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BranchDropdown(
    branches: List<Branch>,
    selectedBranchId: Int?,
    onSelect: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedBranch = branches.firstOrNull { it.id == selectedBranchId }

    ExposedDropdownMenuBox(
        expanded         = expanded,
        onExpandedChange = { if (branches.isNotEmpty()) expanded = it },
    ) {
        // Trigger row
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .clip(RoundedCornerShape(14.dp))
                .border(
                    width = 1.5.dp,
                    color = if (expanded) RelaxDark else RelaxDivider,
                    shape = RoundedCornerShape(14.dp),
                )
                .background(RelaxInputBg)
                .clickable(enabled = branches.isNotEmpty()) { expanded = true }
                .padding(14.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedBranch != null) RelaxDark else RelaxSurfaceAlt),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Rounded.Store, null,
                        tint     = if (selectedBranch != null) RelaxWhite else RelaxTextSecondary,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    if (selectedBranch != null) {
                        Text(
                            selectedBranch.name,
                            style      = MaterialTheme.typography.bodyMedium,
                            color      = RelaxTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(selectedBranch.address, style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                    } else {
                        Text(
                            text  = if (branches.isEmpty()) "Загрузка..." else stringResource(Res.string.checkout_pickup_store_value),
                            style = MaterialTheme.typography.bodyMedium,
                            color = RelaxTextHint,
                        )
                    }
                }
                if (branches.isNotEmpty()) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            }
        }

        // Dropdown list
        ExposedDropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
            modifier         = Modifier.background(RelaxWhite),
        ) {
            branches.forEachIndexed { index, branch ->
                val isSelected = branch.id == selectedBranchId
                if (index > 0) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = RelaxDivider, thickness = 0.5.dp)
                }
                DropdownMenuItem(
                    text = {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) RelaxDark else RelaxSurfaceAlt),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    if (isSelected) Icons.Rounded.CheckCircle else Icons.Rounded.Store,
                                    null,
                                    tint     = if (isSelected) RelaxWhite else RelaxTextSecondary,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    branch.name,
                                    style      = MaterialTheme.typography.bodyMedium,
                                    color      = RelaxTextPrimary,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                )
                                Text(branch.address, style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                            }
                            if (isSelected) {
                                Spacer(Modifier.width(6.dp))
                                Icon(Icons.Rounded.RadioButtonChecked, null, tint = RelaxDark, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    onClick          = { onSelect(branch.id); expanded = false },
                    modifier         = Modifier.background(if (isSelected) RelaxDark.copy(alpha = 0.05f) else Color.Transparent),
                    contentPadding   = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                )
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
private fun OrderSuccessScreen(bonusEarned: Double, onDone: () -> Unit) {
    Box(
        modifier         = Modifier.fillMaxSize().background(RelaxBackground),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.padding(32.dp),
        ) {
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
            Text(
                stringResource(Res.string.checkout_success_title),
                style      = MaterialTheme.typography.displaySmall,
                color      = RelaxTextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(Res.string.checkout_success_subtitle),
                color     = RelaxTextSecondary,
                style     = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
            )
            Spacer(Modifier.height(32.dp))
            if (bonusEarned > 0.0) {
                val bonusDisplay = "%.2f".format(bonusEarned)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFFBEB))
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Stars, null, tint = Color(0xFFD4AF37), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            stringResource(Res.string.checkout_bonus_earned, bonusDisplay),
                            color      = Color(0xFFD4AF37),
                            style      = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
            Button(
                onClick  = onDone,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = RelaxDark),
            ) {
                Text(stringResource(Res.string.checkout_go_home), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun AddressPromptDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var address by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape     = RoundedCornerShape(24.dp),
            colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
            elevation = CardDefaults.cardElevation(8.dp),
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(RelaxDark, RelaxDarkSecondary))),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.LocationOn, null, tint = RelaxWhite, modifier = Modifier.size(32.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    stringResource(Res.string.checkout_address_prompt_title),
                    style      = MaterialTheme.typography.titleLarge,
                    color      = RelaxTextPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    stringResource(Res.string.checkout_address_prompt_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = RelaxTextSecondary,
                )
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value         = address,
                    onValueChange = { address = it },
                    label         = { Text(stringResource(Res.string.checkout_delivery_address_label)) },
                    placeholder   = { Text(stringResource(Res.string.checkout_address_placeholder), fontSize = 13.sp) },
                    leadingIcon   = { Icon(Icons.Rounded.LocationOn, null, tint = RelaxRed) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(14.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = RelaxDark,
                        unfocusedBorderColor    = RelaxDivider,
                        focusedContainerColor   = RelaxWhite,
                        unfocusedContainerColor = RelaxInputBg,
                    ),
                )
                Spacer(Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(Res.string.cancel), color = RelaxTextSecondary)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick  = { if (address.isNotBlank()) onConfirm(address) },
                        enabled  = address.isNotBlank(),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = RelaxDark),
                    ) {
                        Text(stringResource(Res.string.checkout_address_prompt_confirm), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeSlotWheelPicker(
    slots: List<String>,
    selected: String?,
    onSelect: (String) -> Unit,
) {
    if (slots.isEmpty()) return

    val itemHeight = 54.dp
    val selectedIndex = slots.indexOfFirst { it == selected }.coerceAtLeast(0)
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (selectedIndex - 1).coerceAtLeast(0),
    )
    val flingBehavior = rememberSnapFlingBehavior(listState)
    val centerIndex by remember { derivedStateOf { listState.firstVisibleItemIndex + 1 } }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val center = listState.firstVisibleItemIndex + 1
            if (center in slots.indices) onSelect(slots[center])
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight * 3)
            .clip(RoundedCornerShape(16.dp))
            .background(RelaxSurfaceAlt),
    ) {
        LazyColumn(
            state           = listState,
            flingBehavior   = flingBehavior,
            contentPadding  = PaddingValues(vertical = itemHeight),
            modifier        = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(slots) { index, slot ->
                val dist = abs(index - centerIndex)
                Box(
                    modifier        = Modifier.height(itemHeight).fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text       = slot,
                        color      = RelaxTextPrimary.copy(alpha = when (dist) { 0 -> 1f; 1 -> 0.4f; else -> 0.18f }),
                        fontWeight = if (dist == 0) FontWeight.Bold else FontWeight.Normal,
                        fontSize   = if (dist == 0) 15.sp else 13.sp,
                        textAlign  = TextAlign.Center,
                    )
                }
            }
        }

        // selection highlight ring
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(itemHeight)
                .padding(horizontal = 16.dp)
                .border(1.5.dp, RelaxDark.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
        )

        // top fade
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .align(Alignment.TopCenter)
                .background(Brush.verticalGradient(listOf(RelaxSurfaceAlt, Color.Transparent))),
        )
        // bottom fade
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(Color.Transparent, RelaxSurfaceAlt))),
        )
    }
}

private val CircleShape = RoundedCornerShape(50)
