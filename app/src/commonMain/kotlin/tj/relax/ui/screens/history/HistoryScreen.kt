package tj.relax.ui.screens.history

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Redeem
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import org.koin.compose.viewmodel.koinViewModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import tj.relax.generated.resources.*
import tj.relax.data.CartItem
import tj.relax.data.Order
import tj.relax.data.OrderStatus
import tj.relax.ui.components.RelaxDivider
import tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel.formatFullDate
import tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel.formatTimeOnly
import tj.relax.ui.screens.orders.OrdersViewModel
import tj.relax.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onOrder: (String) -> Unit,
    viewModel: OrdersViewModel = koinViewModel(),
) {
    val state = viewModel.uiState

    // The header is pinned on top (drawn last = topmost in the Box) with rounded bottom corners,
    // while the list is the full-size layer underneath it — scrolling up runs list content behind
    // the header instead of the header scrolling away with everything else. headerHeight is
    // measured (not hardcoded) so it always matches the header's real content/locale/text size.
    var headerHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        when {
            state.isLoading -> Box(modifier = Modifier.fillMaxSize().padding(top = headerHeight), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RelaxDark)
            }
            state.orders.isEmpty() -> Box(modifier = Modifier.fillMaxSize().padding(top = headerHeight), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🧾", fontSize = 56.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(Res.string.order_empty_title), style = MaterialTheme.typography.headlineSmall, color = RelaxTextPrimary)
                    Text(stringResource(Res.string.order_empty_subtitle), style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
                }
            }
            else -> {
                val grouped = remember(state.orders) {
                    state.orders.groupBy { dayLabel(it.date) }
                }
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh    = { scope.launch { viewModel.refresh() } },
                    modifier     = Modifier.fillMaxSize().padding(top = headerHeight),
                ) {
                    LazyColumn(
                        modifier       = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 16.dp),
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

                        if (state.hasMore || state.isLoadingMore) {
                            item(key = "load_more") {
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                    if (state.isLoadingMore) {
                                        CircularProgressIndicator(color = RelaxDark, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                    } else {
                                        TextButton(onClick = { viewModel.loadMore() }) {
                                            Text(stringResource(Res.string.loyalty_history_load_more), color = RelaxDark, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        HistoryHeader(
            orders     = state.orders,
            totalCount = state.totalCount,
            modifier   = Modifier.onGloballyPositioned { headerHeight = with(density) { it.size.height.toDp() } },
        )
    }
}

@Composable
private fun HistoryHeader(orders: List<Order>, totalCount: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(RelaxDark, RelaxDarkSecondary)), RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Column {
            Text(
                stringResource(Res.string.history_title),
                color      = RelaxWhite,
                style      = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
            )
            // Customers care about how many times they've shopped and how much cashback that
            // earned them — not a running total of money spent, which reads more like a bill
            // than a reward. Two icon badges instead of plain text chips make the pair feel like
            // an achievement rather than a receipt summary.
            if (orders.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    HeaderStatBadge(
                        icon      = Icons.Rounded.ShoppingBag,
                        iconTint  = RelaxWhite,
                        iconBg    = RelaxWhite.copy(alpha = 0.16f),
                        value     = totalCount.toString(),
                        label     = stringResource(Res.string.history_stat_orders),
                        modifier  = Modifier.weight(1f),
                    )
                    HeaderStatBadge(
                        icon      = Icons.Rounded.Redeem,
                        iconTint  = RelaxDark,
                        iconBg    = RelaxGold,
                        value     = formatAmount(orders.sumOf { it.bonusEarned }),
                        label     = stringResource(Res.string.history_stat_cashback),
                        modifier  = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderStatBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBg: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(RelaxWhite.copy(alpha = 0.10f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(value, color = RelaxWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
            Text(label, color = RelaxTextOnDarkSub, fontSize = 11.sp)
        }
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
                            stringResource(Res.string.order_number_label, order.id.take(8).uppercase()),
                            style = MaterialTheme.typography.titleSmall,
                            color = RelaxTextPrimary,
                        )
                        if (order.source == "pos") SourceBadge()
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(formatTimeOnly(order.date), style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
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
                            Text(stringResource(Res.string.order_more_items, order.items.size - 3), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = RelaxTextSecondary)
                        }
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Receipt, null, tint = RelaxTextHint, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(Res.string.history_items_unavailable), style = MaterialTheme.typography.bodySmall, color = RelaxTextHint)
                }
            }

            Spacer(Modifier.height(14.dp))
            RelaxDivider()
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(Res.string.order_total_label, formatAmount(order.total)), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RelaxTextPrimary)
                when {
                    order.bonusEarned > 0 -> BonusPill("+${formatAmount(order.bonusEarned)}", RelaxSuccess)
                    order.bonusesUsed > 0 -> BonusPill("−${formatAmount(order.bonusesUsed)}", RelaxRed)
                }
            }
        }
    }
}

// Bottom-right pill for the bonus points this order earned or spent — the card's secondary
// figure, set apart from the total rather than crammed next to the date.
@Composable
private fun BonusPill(text: String, color: Color) {
    Text(
        text,
        style      = MaterialTheme.typography.labelMedium,
        color      = color,
        fontWeight = FontWeight.Bold,
        modifier   = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
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
        Text(stringResource(Res.string.history_source_pos), color = RelaxTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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

internal fun orderStatusLabel(status: OrderStatus): org.jetbrains.compose.resources.StringResource = when (status) {
    OrderStatus.PENDING    -> Res.string.order_status_pending
    OrderStatus.CONFIRMED  -> Res.string.order_status_confirmed
    OrderStatus.PREPARING  -> Res.string.order_status_preparing
    OrderStatus.DELIVERING -> Res.string.order_status_in_progress
    OrderStatus.DELIVERED  -> Res.string.order_status_delivered
    OrderStatus.CANCELLED  -> Res.string.order_status_cancelled
}

private fun dayLabel(rawDate: String): String = formatFullDate(rawDate)

// Bonus amounts (cashback % of a purchase) are often fractional — .toInt() would silently
// truncate e.g. 0.15 down to "0". Show up to 2 decimals, trimmed of trailing zeros.
private fun formatAmount(value: Double): String = "%.2f".format(value).trimEnd('0').trimEnd('.')
