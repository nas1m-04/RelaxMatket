package tj.relax.ui.screens.history

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import tj.relax.R
import tj.relax.data.CartItem
import tj.relax.data.Order
import tj.relax.data.OrderStatus
import tj.relax.ui.components.RelaxDivider
import tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel.formatDayTime
import tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel.formatMemberSince
import tj.relax.ui.screens.orders.OrdersViewModel
import tj.relax.ui.theme.*

@Composable
fun HistoryScreen(
    onOrder: (String) -> Unit,
    viewModel: OrdersViewModel = hiltViewModel(),
) {
    val state = viewModel.uiState

    // The header is pinned on top (drawn last = topmost in the Box) with rounded bottom corners,
    // while the list is the full-size layer underneath it — scrolling up runs list content behind
    // the header instead of the header scrolling away with everything else. headerHeight is
    // measured (not hardcoded) so it always matches the header's real content/locale/text size.
    var headerHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        when {
            state.isLoading -> Box(modifier = Modifier.fillMaxSize().padding(top = headerHeight), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RelaxDark)
            }
            state.orders.isEmpty() -> Box(modifier = Modifier.fillMaxSize().padding(top = headerHeight), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🧾", fontSize = 56.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(R.string.order_empty_title), style = MaterialTheme.typography.headlineSmall, color = RelaxTextPrimary)
                    Text(stringResource(R.string.order_empty_subtitle), style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
                }
            }
            else -> {
                val grouped = remember(state.orders) {
                    state.orders.groupBy { monthLabel(it.date) }
                }
                LazyColumn(
                    modifier       = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = headerHeight + 16.dp, bottom = 16.dp),
                ) {
                    grouped.forEach { (month, orders) ->
                        item(key = "header_$month") {
                            Text(
                                month.uppercase(),
                                style      = MaterialTheme.typography.labelLarge,
                                color      = RelaxTextHint,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                modifier   = Modifier.padding(top = 12.dp, bottom = 10.dp),
                            )
                        }
                        items(orders, key = { it.id }) { order ->
                            HistoryCard(order = order, onClick = { onOrder(order.id) })
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }

        HistoryHeader(
            orders   = state.orders,
            modifier = Modifier.onGloballyPositioned { headerHeight = with(density) { it.size.height.toDp() } },
        )
    }
}

@Composable
private fun HistoryHeader(orders: List<Order>, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(RelaxDark, RelaxDarkSecondary)), RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Column {
            Text(
                stringResource(R.string.history_title),
                color      = RelaxWhite,
                style      = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
            )
            if (orders.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    HeaderStatChip(orders.size.toString(), stringResource(R.string.history_stat_orders))
                    HeaderStatChip("${formatAmount(orders.sumOf { it.total })} TJS", stringResource(R.string.history_stat_spent))
                }
            }
        }
    }
}

@Composable
private fun HeaderStatChip(value: String, label: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(RelaxWhite.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(value, color = RelaxWhite, fontWeight = FontWeight.Black, fontSize = 15.sp)
        Spacer(Modifier.width(6.dp))
        Text(label, color = RelaxTextOnDarkSub, fontSize = 12.sp)
    }
}

@Composable
private fun HistoryCard(order: Order, onClick: () -> Unit) {
    Card(
        onClick   = onClick,
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            stringResource(R.string.order_number_label, order.id.take(8).uppercase()),
                            style = MaterialTheme.typography.titleSmall,
                            color = RelaxTextPrimary,
                        )
                        if (order.source == "pos") SourceBadge()
                    }
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(formatDayTime(order.date), style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                        when {
                            order.bonusEarned > 0 -> BonusNote("+${formatAmount(order.bonusEarned)}", RelaxSuccess)
                            order.bonusesUsed > 0 -> BonusNote("−${formatAmount(order.bonusesUsed)}", RelaxRed)
                        }
                    }
                }
                // POS purchases are always stamped "delivered" server-side (it's how a completed
                // in-store sale is represented) — that status label is meaningless here, it wasn't
                // a delivery, so we simply don't show a status badge for those.
                if (order.source != "pos") OrderStatusBadge(order.status)
            }

            Spacer(Modifier.height(14.dp))
            if (order.items.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy((-10).dp)) {
                    order.items.take(3).forEach { item -> ItemThumb(item) }
                    if (order.items.size > 3) {
                        Box(modifier = Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)).background(RelaxSurfaceAlt).border(2.dp, RelaxWhite, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.order_more_items, order.items.size - 3), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = RelaxTextSecondary)
                        }
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Receipt, null, tint = RelaxTextHint, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.history_items_unavailable), style = MaterialTheme.typography.bodySmall, color = RelaxTextHint)
                }
            }

            Spacer(Modifier.height(14.dp))
            RelaxDivider()
            Spacer(Modifier.height(12.dp))

            Text(stringResource(R.string.order_total_label, formatAmount(order.total)), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RelaxTextPrimary)
        }
    }
}

// Small, muted note next to the date — not a loud pill, this is secondary metadata, not the
// headline figure on the card (that's the total below).
@Composable
private fun BonusNote(text: String, color: Color) {
    Text(
        "  ·  $text",
        style      = MaterialTheme.typography.bodySmall,
        color      = color,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun ItemThumb(item: CartItem) {
    Box(
        modifier = Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)).background(RelaxInputBg).border(2.dp, RelaxWhite, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (item.product.imageUrl.isNullOrBlank()) {
            Icon(Icons.Rounded.Receipt, null, tint = RelaxTextHint, modifier = Modifier.size(20.dp))
        } else {
            AsyncImage(model = item.product.imageUrl, contentDescription = item.product.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun SourceBadge() {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(RelaxSurfaceAlt).padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Rounded.Storefront, null, tint = RelaxTextSecondary, modifier = Modifier.size(11.dp))
        Spacer(Modifier.width(3.dp))
        Text(stringResource(R.string.history_source_pos), color = RelaxTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun OrderStatusBadge(status: OrderStatus) {
    val bg    = Color(status.color).copy(alpha = 0.12f)
    val color = Color(status.color)
    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(bg).padding(horizontal = 10.dp, vertical = 4.dp)) {
        Text(stringResource(orderStatusLabel(status)), color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

internal fun orderStatusLabel(status: OrderStatus): Int = when (status) {
    OrderStatus.PENDING    -> R.string.order_status_pending
    OrderStatus.CONFIRMED  -> R.string.order_status_confirmed
    OrderStatus.PREPARING  -> R.string.order_status_preparing
    OrderStatus.DELIVERING -> R.string.order_status_in_progress
    OrderStatus.DELIVERED  -> R.string.order_status_delivered
    OrderStatus.CANCELLED  -> R.string.order_status_cancelled
}

private fun monthLabel(rawDate: String): String = formatMemberSince(rawDate)

// Bonus amounts (cashback % of a purchase) are often fractional — .toInt() would silently
// truncate e.g. 0.15 down to "0". Show up to 2 decimals, trimmed of trailing zeros.
private fun formatAmount(value: Double): String = "%.2f".format(value).trimEnd('0').trimEnd('.')
