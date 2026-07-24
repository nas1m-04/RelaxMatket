package tj.relax.ui.screens.orders

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import org.koin.compose.viewmodel.koinViewModel
import coil.compose.AsyncImage
import tj.relax.generated.resources.*
import tj.relax.data.CartItem
import tj.relax.data.Order
import tj.relax.ui.components.RelaxDivider
import tj.relax.ui.components.RelaxTopBar
import tj.relax.ui.screens.history.OrderStatusBadge
import tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel.formatTransactionDate
import tj.relax.ui.theme.*

@Composable
fun OrderDetailScreen(
    onBack: () -> Unit,
    viewModel: OrderDetailViewModel = koinViewModel(),
) {
    val state = viewModel.uiState
    val order = state.order

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        Box(modifier = Modifier.background(RelaxWhite)) {
            RelaxTopBar(title = stringResource(Res.string.order_detail_title), onBack = onBack)
        }

        when {
            state.isLoading || order == null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RelaxDark)
            }
            else -> Column(
                modifier            = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Spacer(Modifier.height(4.dp))
                OrderHeaderCard(order)
                OrderItemsCard(order)
                OrderSummaryCard(order)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun OrderHeaderCard(order: Order) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    stringResource(Res.string.order_number_label, order.id.take(8).uppercase()),
                    style      = MaterialTheme.typography.titleMedium,
                    color      = RelaxTextPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(formatTransactionDate(order.date), style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                    when {
                        order.bonusEarned > 0 -> Text(
                            "  ·  +${formatBonusAmount(order.bonusEarned)}",
                            style      = MaterialTheme.typography.bodySmall,
                            color      = RelaxSuccess,
                            fontWeight = FontWeight.SemiBold,
                        )
                        order.bonusesUsed > 0 -> Text(
                            "  ·  −${formatBonusAmount(order.bonusesUsed)}",
                            style      = MaterialTheme.typography.bodySmall,
                            color      = RelaxRed,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                if (!order.address.isNullOrBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(order.address, style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                }
            }
            // Same as History: a POS sale is always stamped "delivered" server-side just to mark
            // it complete — that label isn't a real delivery status, so we don't show it here.
            if (order.source != "pos") OrderStatusBadge(order.status)
        }
    }
}

@Composable
private fun OrderItemsCard(order: Order) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                stringResource(Res.string.order_detail_items_title),
                style      = MaterialTheme.typography.titleSmall,
                color      = RelaxTextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(12.dp))
            if (order.items.isEmpty()) {
                Text(
                    stringResource(Res.string.history_items_unavailable),
                    style = MaterialTheme.typography.bodySmall,
                    color = RelaxTextHint,
                )
            } else {
                order.items.forEachIndexed { index, item ->
                    if (index > 0) {
                        Spacer(Modifier.height(12.dp))
                        RelaxDivider()
                        Spacer(Modifier.height(12.dp))
                    }
                    OrderItemRow(item)
                }
            }
        }
    }
}

@Composable
private fun OrderItemRow(item: CartItem) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp)).background(RelaxInputBg)) {
            AsyncImage(
                model              = item.product.imageUrl,
                contentDescription = item.product.name,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.product.name, style = MaterialTheme.typography.bodyMedium, color = RelaxTextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 2)
            Spacer(Modifier.height(2.dp))
            Text(
                stringResource(Res.string.order_detail_item_qty_price, item.quantity, formatBonusAmount(item.product.price)),
                style = MaterialTheme.typography.bodySmall,
                color = RelaxTextSecondary,
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            "${formatBonusAmount(item.product.price * item.quantity)} TJS",
            style      = MaterialTheme.typography.bodyMedium,
            color      = RelaxTextPrimary,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun OrderSummaryCard(order: Order) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (order.discount > 0) {
                SummaryRow(stringResource(Res.string.cart_discount), "−${formatBonusAmount(order.discount)} TJS", valueColor = RelaxSuccess)
                Spacer(Modifier.height(8.dp))
            }
            SummaryRow(stringResource(Res.string.checkout_total_label), "${formatBonusAmount(order.total)} TJS", bold = true)
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, bold: Boolean = false, valueColor: Color = RelaxTextPrimary) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = if (bold) RelaxTextPrimary else RelaxTextSecondary,
        )
        Text(
            value,
            style      = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color      = if (bold) RelaxTextPrimary else valueColor,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold,
        )
    }
}

// Bonus amounts (cashback % of a purchase) are often fractional — .toInt() would silently
// truncate e.g. 0.15 down to "0". Show up to 2 decimals, trimmed of trailing zeros.
private fun formatBonusAmount(value: Double): String = "%.2f".format(value).trimEnd('0').trimEnd('.')
