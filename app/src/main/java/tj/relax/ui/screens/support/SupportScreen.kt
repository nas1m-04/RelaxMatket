package tj.relax.ui.screens.support

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import tj.relax.ui.components.RelaxTopBar
import tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel.formatDayTime
import tj.relax.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    onBack: () -> Unit,
    viewModel: SupportViewModel = hiltViewModel(),
) {
    val state = viewModel.uiState
    val scope = rememberCoroutineScope()
    var showNewTicketSheet by remember { mutableStateOf(false) }

    if (showNewTicketSheet) {
        NewTicketSheet(
            isSubmitting = state.isSubmitting,
            onSubmit = { message ->
                viewModel.submitTicket(message) { showNewTicketSheet = false }
            },
            onDismiss = { showNewTicketSheet = false },
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        RelaxTopBar(title = "Техподдержка", onBack = onBack)

        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Button(
                onClick = { showNewTicketSheet = true },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RelaxDark),
            ) {
                Icon(Icons.Rounded.Add, null, tint = RelaxWhite, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Написать в поддержку", color = RelaxWhite, fontWeight = FontWeight.Bold)
            }
        }

        when {
            state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RelaxDark)
            }
            state.tickets.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.SupportAgent, null, tint = RelaxTextHint, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("У вас пока нет обращений", style = MaterialTheme.typography.bodyLarge, color = RelaxTextSecondary)
                }
            }
            else -> {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh    = { scope.launch { viewModel.refresh() } },
                    modifier     = Modifier.fillMaxSize(),
                ) {
                    LazyColumn(
                        modifier       = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    ) {
                        items(state.tickets, key = { it.id }) { ticket ->
                            TicketCard(ticket)
                            Spacer(Modifier.height(12.dp))
                        }

                        if (state.hasMore || state.isLoadingMore) {
                            item(key = "load_more") {
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                    if (state.isLoadingMore) {
                                        CircularProgressIndicator(color = RelaxDark, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                    } else {
                                        TextButton(onClick = { viewModel.loadMore() }) {
                                            Text("Показать ещё", color = RelaxDark, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TicketCard(ticket: tj.relax.data.SupportTicket) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(formatDayTime(ticket.createdAt), style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                StatusPill(resolved = ticket.isResolved)
            }
            Spacer(Modifier.height(8.dp))
            Text(ticket.message, style = MaterialTheme.typography.bodyMedium, color = RelaxTextPrimary)
        }
    }
}

@Composable
private fun StatusPill(resolved: Boolean) {
    val color = if (resolved) RelaxSuccess else RelaxWarning
    val icon = if (resolved) Icons.Rounded.CheckCircle else Icons.Rounded.HourglassEmpty
    val label = if (resolved) "Решено" else "Открыто"

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(13.dp))
        Spacer(Modifier.width(4.dp))
        Text(label, color = color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewTicketSheet(
    isSubmitting: Boolean,
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var message by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = RelaxWhite) {
        Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 24.dp)) {
            Text("Опишите проблему", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = RelaxTextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(
                "Мы прочитаем ваше сообщение и свяжемся с вами, если потребуется",
                style = MaterialTheme.typography.bodySmall,
                color = RelaxTextSecondary,
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.fillMaxWidth().height(140.dp),
                placeholder = { Text("Например: не начисляются бонусы после покупки...") },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = RelaxDark,
                    unfocusedBorderColor = RelaxDivider,
                ),
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { onSubmit(message) },
                enabled = message.isNotBlank() && !isSubmitting,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RelaxDark),
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = RelaxWhite)
                } else {
                    Text("Отправить", color = RelaxWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
