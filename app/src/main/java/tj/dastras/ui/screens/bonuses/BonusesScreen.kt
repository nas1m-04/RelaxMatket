package tj.dastras.ui.screens.bonuses

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import tj.dastras.R
import tj.dastras.core.api.AchievementApiResponse
import tj.dastras.core.api.BonusTransactionApiResponse
import tj.dastras.ui.components.RelaxDivider
import tj.dastras.ui.screens.loyalty.LoyaltyViewModel
import tj.dastras.ui.screens.loyalty.formatTransactionDate
import tj.dastras.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BonusesScreen(viewModel: LoyaltyViewModel = hiltViewModel()) {
    val state = viewModel.uiState
    val summary = state.summary
    var activeTab by remember { mutableStateOf(0) }

    if (state.isLoading || summary == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(RelaxBackground),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = RelaxDark)
        }
        return
    }

    val transactions  = state.transactions
    val achievements  = state.achievements
    val level         = summary.level
    val listState     = rememberLazyListState()
    val refreshing = state.isLoading
    val pullState = rememberPullToRefreshState()
    // Auto-load next page when user scrolls near the bottom
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total       = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 3
        }
    }
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) viewModel.loadMoreTransactions()
    }

    LaunchedEffect(Unit) {
        viewModel.onScreenVisible()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RelaxBackground)
    ) {
        // Header with balance
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(RelaxDark, RelaxDarkSecondary)))
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.bonuses_title), color = RelaxWhite, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(24.dp))

                // Big balance
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(RelaxWhite.copy(alpha = 0.08f))
                        .border(2.dp, RelaxWhite.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⭐", fontSize = 28.sp)
                        Text(formatBonus(summary.bonusBalance), color = RelaxWhite, fontSize = 36.sp, fontWeight = FontWeight.Black)
                        Text(stringResource(R.string.bonuses_points), color = RelaxTextOnDarkSub, fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Stats row
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    BonusStatChip("${level.cashbackPercent.toInt()}%", stringResource(R.string.bonuses_cashback), RelaxOrange)
                    BonusStatChip(level.name, stringResource(R.string.bonuses_level), RelaxGold)
                    BonusStatChip("∞", stringResource(R.string.bonuses_days_to_expire), RelaxSuccess)
                }
            }
        }

        // Levels carousel
        LazyColumn(
            state           = listState,
            modifier        = Modifier.fillMaxSize(),
            contentPadding  = PaddingValues(bottom = 24.dp),
        ) {
            item {
                Spacer(Modifier.height(20.dp))
                Text(stringResource(R.string.bonuses_levels_title), style = MaterialTheme.typography.headlineSmall, color = RelaxTextPrimary, modifier = Modifier.padding(horizontal = 20.dp))
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier        = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    state.levels.forEach { lvl ->
                        LevelCard(
                            name       = lvl.name,
                            cashback   = lvl.cashbackPercent,
                            minSpent   = lvl.minSpent,
                            color      = Color(lvl.color),
                            isCurrent  = lvl.isCurrent,
                        )
                    }
                }
            }

            // Achievements
            if (achievements.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        stringResource(R.string.bonuses_achievements_title),
                        style    = MaterialTheme.typography.headlineSmall,
                        color    = RelaxTextPrimary,
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier              = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        achievements.forEach { achievement ->
                            AchievementCard(achievement)
                        }
                    }
                }
            }

            // Tabs
            item {
                Spacer(Modifier.height(24.dp))
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(RelaxSurfaceAlt)
                            .padding(4.dp),
                    ) {
                        listOf(stringResource(R.string.bonuses_tab_credits), stringResource(R.string.bonuses_tab_debits)).forEachIndexed { idx, label ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (activeTab == idx) RelaxWhite else Color.Transparent)
                                    .clickable { activeTab = idx }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    label,
                                    fontSize   = 14.sp,
                                    fontWeight = if (activeTab == idx) FontWeight.Bold else FontWeight.Normal,
                                    color      = if (activeTab == idx) RelaxTextPrimary else RelaxTextSecondary,
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            val filtered = if (activeTab == 0)
                transactions.filter { it.isCredit }
            else
                transactions.filter { !it.isCredit }

            // Count badge
            if (state.transactionTotal > 0) {
                item {
                    val typeLabel = if (activeTab == 0)
                        "начислений"
                    else
                        "списаний"
                    Text(
                        "Всего ${filtered.size} $typeLabel из ${state.transactionTotal}",
                        style    = MaterialTheme.typography.bodySmall,
                        color    = RelaxTextHint,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                    )
                }
            }

            items(filtered, key = { it.id }) { tx ->
                BonusTransactionItem(tx)
            }

            // Empty state for the current tab
            if (filtered.isEmpty() && !state.isLoading) {
                item {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(if (activeTab == 0) "💰" else "💸", fontSize = 40.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (activeTab == 0) "Начислений пока нет" else "Списаний пока нет",
                            style      = MaterialTheme.typography.titleMedium,
                            color      = RelaxTextPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (activeTab == 0) "Бонусы начислятся после первого заказа" else "Здесь появятся ваши траты бонусов",
                            style     = MaterialTheme.typography.bodySmall,
                            color     = RelaxTextSecondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            // Load-more footer
            if (state.isLoadingMore) {
                item {
                    Box(
                        modifier         = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = RelaxDark, strokeWidth = 2.dp)
                    }
                }
            } else if (state.hasMoreTransactions && filtered.isNotEmpty()) {
                item {
                    Box(
                        modifier         = Modifier.fillMaxWidth().padding(12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        TextButton(onClick = { viewModel.loadMoreTransactions() }) {
                            Text("Показать ещё", color = RelaxDark, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BonusStatChip(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Text(label, color = RelaxTextOnDarkSub, fontSize = 10.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun LevelCard(name: String, cashback: Double, minSpent: Double, color: Color, isCurrent: Boolean) {
    Box(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (isCurrent) color else RelaxWhite)
            .border(
                width = if (isCurrent) 0.dp else 1.5.dp,
                color = color.copy(alpha = 0.3f),
                shape = RoundedCornerShape(18.dp),
            )
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                name,
                color      = if (isCurrent) RelaxWhite else color,
                fontWeight = FontWeight.Black,
                fontSize   = 16.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "${cashback.toInt()}%",
                color      = if (isCurrent) RelaxWhite else RelaxTextPrimary,
                fontSize   = 28.sp,
                fontWeight = FontWeight.Black,
            )
            Text(
                stringResource(R.string.bonuses_cashback_suffix),
                color = if (isCurrent) RelaxWhite.copy(alpha = 0.7f) else RelaxTextSecondary,
                fontSize = 11.sp,
            )
            if (minSpent > 0) {
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.bonuses_min_points_from, minSpent.toInt()),
                    color    = if (isCurrent) RelaxWhite.copy(alpha = 0.6f) else RelaxTextHint,
                    fontSize = 10.sp,
                )
            }
            if (isCurrent) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RelaxWhite.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(stringResource(R.string.bonuses_your_level), color = RelaxWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AchievementCard(achievement: AchievementApiResponse) {
    val unlocked = achievement.unlocked

    Column(
        modifier = Modifier
            .width(96.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (unlocked) RelaxDark else RelaxSurfaceAlt)
            .alpha(if (unlocked) 1f else 0.85f)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (unlocked) RelaxWhite.copy(alpha = 0.15f) else RelaxWhite.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(achievement.emoji, fontSize = 24.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            achievement.title,
            color      = if (unlocked) RelaxWhite else RelaxTextPrimary,
            fontSize   = 10.sp,
            fontWeight = FontWeight.Bold,
            textAlign  = TextAlign.Center,
            lineHeight = 13.sp,
        )
        if (achievement.bonusReward > 0) {
            Spacer(Modifier.height(4.dp))
            Text(
                "+${achievement.bonusReward}",
                color      = if (unlocked) RelaxGold else RelaxTextHint,
                fontSize   = 10.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        if (unlocked) {
            Spacer(Modifier.height(4.dp))
            Text("✓ Выполнено", color = RelaxWhite.copy(alpha = 0.7f), fontSize = 9.sp)
        }
    }
}

@Composable
private fun BonusTransactionItem(tx: BonusTransactionApiResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RelaxWhite)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (tx.isCredit) RelaxSuccessBg else RelaxErrorBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (tx.isCredit) Icons.Rounded.AddCircle else Icons.Rounded.RemoveCircle,
                contentDescription = null,
                tint     = if (tx.isCredit) RelaxSuccess else RelaxError,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(tx.description, style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary)
            Text(formatTransactionDate(tx.createdAt), style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
        }
        Text(
            text       = "${if (tx.isCredit) "+" else "−"}${formatBonus(tx.amount)}",
            fontSize   = 16.sp,
            fontWeight = FontWeight.Bold,
            color      = if (tx.isCredit) RelaxSuccess else RelaxError,
        )
    }
    RelaxDivider(modifier = Modifier.padding(horizontal = 20.dp))
}

private fun formatBonus(amount: Double): String = "%.2f".format(amount)

private val CircleShape    = RoundedCornerShape(50)
private val RelaxSuccessBg = Color(0xFFDCFCE7)
private val RelaxErrorBg   = Color(0xFFFEE2E2)
private val RelaxSuccess   = Color(0xFF22C55E)
private val RelaxError     = Color(0xFFEF4444)
private val RelaxGold      = Color(0xFFD4AF37)
