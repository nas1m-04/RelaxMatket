package tj.relax.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import org.koin.compose.viewmodel.koinViewModel
import tj.relax.R
import tj.relax.data.Notification
import tj.relax.data.NotificationType
import tj.relax.ui.components.RelaxDivider
import tj.relax.ui.components.RelaxTopBar
import tj.relax.ui.theme.*

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    viewModel: NotificationsViewModel = koinViewModel(),
) {
    val state = viewModel.uiState

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        Box(modifier = Modifier.background(RelaxWhite)) {
            RelaxTopBar(
                title  = stringResource(R.string.notif_title),
                onBack = onBack,
                actions = {
                    TextButton(onClick = { viewModel.markAllRead() }) {
                        Text(stringResource(R.string.notif_mark_all_read), color = RelaxRed, style = MaterialTheme.typography.labelMedium)
                    }
                }
            )
        }

        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = RelaxRed)
                }
            }
            state.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("⚠️", fontSize = 48.sp)
                        Text(state.error, style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
                        TextButton(onClick = { viewModel.load() }) {
                            Text("Повторить", color = RelaxRed)
                        }
                    }
                }
            }
            state.notifications.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔔", fontSize = 64.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(stringResource(R.string.notif_empty_title), style = MaterialTheme.typography.headlineSmall, color = RelaxTextPrimary)
                    }
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize().background(RelaxWhite)) {
                    items(state.notifications, key = { it.id }) { notif ->
                        NotificationItem(notif, onClick = { viewModel.markRead(notif.id) })
                        RelaxDivider(modifier = Modifier.padding(start = 72.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(notif: Notification, onClick: () -> Unit) {
    val (icon, bg, color) = when (notif.type) {
        NotificationType.PROMO  -> Triple(Icons.Rounded.LocalOffer,      Color(0xFFFFE5E5), Color(0xFFE8393A))
        NotificationType.BONUS  -> Triple(Icons.Rounded.Stars,            Color(0xFFFFF8DC), Color(0xFFD4AF37))
        NotificationType.ORDER  -> Triple(Icons.Rounded.ShoppingBag,     Color(0xFFE3F2FD), Color(0xFF1976D2))
        NotificationType.SYSTEM -> Triple(Icons.Rounded.NotificationsNone, Color(0xFFF3E5F5), Color(0xFF7B1FA2))
        NotificationType.NEWS   -> Triple(Icons.Rounded.Article,          Color(0xFFE8F5E9), Color(0xFF2E7D32))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (!notif.isRead) RelaxDark.copy(alpha = 0.02f) else RelaxWhite)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(bg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(notif.title, style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary, modifier = Modifier.weight(1f))
                if (!notif.isRead) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(RelaxRed))
                }
            }
            Spacer(Modifier.height(3.dp))
            Text(notif.body, style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary, lineHeight = 18.sp)
            Spacer(Modifier.height(4.dp))
            Text(notif.time, style = MaterialTheme.typography.labelSmall, color = RelaxTextHint)
        }
    }
}

private val CircleShape = RoundedCornerShape(50)
