package tj.dastras.ui.screens.orders

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
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import tj.dastras.R
import tj.dastras.data.Order
import tj.dastras.data.OrderStatus
import tj.dastras.ui.components.RelaxDivider
import tj.dastras.ui.components.RelaxTopBar
import tj.dastras.ui.screens.loyaltycard.LoyaltyCardViewModel.formatTransactionDate
import tj.dastras.ui.theme.*

@Composable
fun OrderHistoryScreen(
    onBack: () -> Unit,
    viewModel: OrdersViewModel = hiltViewModel(),
) {
    val state = viewModel.uiState

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        Box(modifier = Modifier.background(RelaxWhite)) {
            RelaxTopBar(title = stringResource(R.string.order_history_title), onBack = onBack)
        }

        when {
            state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RelaxDark)
            }
            state.orders.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📦", fontSize = 56.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(R.string.order_empty_title), style = MaterialTheme.typography.headlineSmall, color = RelaxTextPrimary)
                    Text(stringResource(R.string.order_empty_subtitle), style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
                }
            }
            else -> LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.orders) { order -> OrderCard(order) }
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = RelaxWhite), elevation = CardDefaults.cardElevation(0.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(
                        stringResource(R.string.order_number_label, order.id.take(8).uppercase()),
                        style = MaterialTheme.typography.titleSmall,
                        color = RelaxTextPrimary,
                    )
                    Text(formatTransactionDate(order.date), style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                }
                StatusBadge(order.status)
            }

            if (order.items.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy((-10).dp)) {
                    order.items.take(3).forEach { item ->
                        Box(modifier = Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)).background(RelaxInputBg).border(2.dp, RelaxWhite, RoundedCornerShape(12.dp))) {
                            AsyncImage(model = item.product.imageUrl, contentDescription = item.product.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        }
                    }
                    if (order.items.size > 3) {
                        Box(modifier = Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)).background(RelaxSurfaceAlt).border(2.dp, RelaxWhite, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.order_more_items, order.items.size - 3), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = RelaxTextSecondary)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            RelaxDivider()
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.order_total_label, order.total.toInt()), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RelaxTextPrimary)
                TextButton(onClick = {}) {
                    Icon(Icons.Rounded.Refresh, null, tint = RelaxDark, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.order_repeat_button), color = RelaxDark, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: OrderStatus) {
    val bg    = Color(status.color).copy(alpha = 0.12f)
    val color = Color(status.color)
    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(bg).padding(horizontal = 10.dp, vertical = 4.dp)) {
        Text(stringResource(orderStatusLabel(status)), color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

private fun orderStatusLabel(status: OrderStatus): Int = when (status) {
    OrderStatus.PENDING    -> R.string.order_status_pending
    OrderStatus.CONFIRMED  -> R.string.order_status_confirmed
    OrderStatus.PREPARING  -> R.string.order_status_preparing
    OrderStatus.DELIVERING -> R.string.order_status_in_progress
    OrderStatus.DELIVERED  -> R.string.order_status_delivered
    OrderStatus.CANCELLED  -> R.string.order_status_cancelled
}
