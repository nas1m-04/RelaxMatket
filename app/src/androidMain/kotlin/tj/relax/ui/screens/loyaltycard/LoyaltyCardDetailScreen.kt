package tj.relax.ui.screens.loyaltycard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import tj.relax.generated.resources.*
import tj.relax.core.api.ErrorPresenter
import tj.relax.ui.components.RelaxDivider
import tj.relax.ui.components.RelaxTopBar
import tj.relax.ui.components.sharedViewModel
import tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel.LoyaltyViewModel
import tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel.formatTransactionDate
import tj.relax.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoyaltyCardDetailScreen(
    onBack: () -> Unit,
    viewModel: LoyaltyViewModel = sharedViewModel(),
) {
    val state   = viewModel.uiState
    val summary = state.summary
    var isRefreshing by remember { mutableStateOf(false) }
    var activeTxTab   by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        Box(modifier = Modifier.background(RelaxWhite)) {
            RelaxTopBar(title = stringResource(Res.string.loyalty_details_title), onBack = onBack)
        }

        if (summary == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RelaxDark)
            }
            return@Column
        }

        val level     = summary.level
        val nextLevel = summary.nextLevel
        val progress  = summary.progressToNextLevel.toFloat()

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                if (isRefreshing) return@PullToRefreshBox
                isRefreshing = true
                scope.launch {
                    try {
                        viewModel.refresh()
                    } catch (e: Exception) {
                        ErrorPresenter.report(e)
                    } finally {
                        isRefreshing = false
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier       = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                // ── Level progress ────────────────────────────────
                item {
                    Spacer(Modifier.height(20.dp))

                    Card(
                        modifier  = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                        shape     = RoundedCornerShape(20.dp),
                        colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
                        elevation = CardDefaults.cardElevation(0.dp),
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text(stringResource(Res.string.loyalty_level_label), style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(Color(level.color))
                                        )
                                        Text(level.name, style = MaterialTheme.typography.titleMedium, color = RelaxTextPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                                if (nextLevel != null) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(stringResource(Res.string.loyalty_next_level_label), style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                                        Text(nextLevel.name, style = MaterialTheme.typography.titleMedium, color = RelaxTextPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress   = { progress },
                                modifier   = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color      = Color(level.color),
                                trackColor = RelaxDivider,
                            )

                            if (nextLevel != null) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    stringResource(Res.string.loyalty_progress_to_level, nextLevel.name, summary.amountToNextLevel.toInt()),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = RelaxTextSecondary,
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            Text(stringResource(Res.string.loyalty_benefits_title), style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(10.dp))
                            level.benefits.forEach { benefit ->
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(Color(level.color).copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(Icons.Rounded.Check, null, tint = Color(level.color), modifier = Modifier.size(12.dp))
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Text(benefit, style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
                                }
                            }
                        }
                    }
                }

                // ── Levels carousel ──────────────────────────────
                if (state.levels.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(24.dp))
                        Text(
                            stringResource(Res.string.bonuses_levels_title),
                            style    = MaterialTheme.typography.headlineSmall,
                            color    = RelaxTextPrimary,
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            state.levels.forEach { lvl ->
                                LevelCard(
                                    name      = lvl.name,
                                    cashback  = lvl.cashbackPercent,
                                    minSpent  = lvl.minSpent,
                                    color     = Color(lvl.color),
                                    isCurrent = lvl.isCurrent,
                                )
                            }
                        }
                    }
                }

                // ── Achievements — parked for a future release, kept for reuse ──
                /*
                if (state.achievements.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(24.dp))
                        Text(
                            stringResource(Res.string.bonuses_achievements_title),
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
                            state.achievements.forEach { achievement ->
                                AchievementCard(achievement)
                            }
                        }
                    }
                }
                */

                // ── Bonus history — credits & debits ──────────────
                if (state.transactions.isNotEmpty() || state.isLoadingMoreTx) {
                    item {
                        Spacer(Modifier.height(24.dp))
                        Text(
                            stringResource(Res.string.loyalty_history_title),
                            style      = MaterialTheme.typography.headlineSmall,
                            color      = RelaxTextPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.padding(horizontal = 20.dp),
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(RelaxSurfaceAlt)
                                .padding(4.dp),
                        ) {
                            listOf(stringResource(Res.string.bonuses_tab_credits), stringResource(Res.string.bonuses_tab_debits)).forEachIndexed { idx, label ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (activeTxTab == idx) RelaxWhite else Color.Transparent)
                                        .clickable { activeTxTab = idx }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        label,
                                        fontSize   = 14.sp,
                                        fontWeight = if (activeTxTab == idx) FontWeight.Bold else FontWeight.Normal,
                                        color      = if (activeTxTab == idx) RelaxTextPrimary else RelaxTextSecondary,
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }

                run {
                    val filteredTx = if (activeTxTab == 0)
                        state.transactions.filter { it.isCredit }
                    else
                        state.transactions.filter { !it.isCredit }

                    items(filteredTx, key = { it.id }) { tx ->
                        BonusTransactionItem(tx)
                    }

                    if (state.transactions.isNotEmpty() && filteredTx.isEmpty() && !state.hasMoreTx) {
                        item {
                            Text(
                                stringResource(Res.string.loyalty_history_empty_tab),
                                style     = MaterialTheme.typography.bodySmall,
                                color     = RelaxTextHint,
                                modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                            )
                        }
                    }

                    if (state.isLoadingMoreTx) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = RelaxDark, strokeWidth = 2.dp)
                            }
                        }
                    } else if (state.hasMoreTx) {
                        // Shown even if the tab's filtered list is empty so far — the type we're
                        // looking for (credit/debit) may only appear on a later page.
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                                TextButton(onClick = { viewModel.loadMoreTransactions() }) {
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

@Composable
private fun BonusTransactionItem(tx: tj.relax.core.api.BonusTransactionApiResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (tx.isCredit) RelaxSuccessBg else RelaxErrorBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (tx.isCredit) Icons.Rounded.AddCircle else Icons.Rounded.RemoveCircle,
                contentDescription = null,
                tint     = if (tx.isCredit) RelaxSuccess else RelaxError,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(tx.description, style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary)
            Text(formatTransactionDate(tx.createdAt), style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
        }
        Text(
            text       = "${if (tx.isCredit) "+" else "−"}${formatBonus(tx.amount)}",
            fontSize   = 15.sp,
            fontWeight = FontWeight.Bold,
            color      = if (tx.isCredit) RelaxSuccess else RelaxError,
        )
    }
}

private fun formatBonus(amount: Double): String = "%.2f".format(amount).trimEnd('0').trimEnd('.')

private val RelaxErrorBg = Color(0xFFFEE2E2)

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
                stringResource(Res.string.bonuses_cashback_suffix),
                color = if (isCurrent) RelaxWhite.copy(alpha = 0.7f) else RelaxTextSecondary,
                fontSize = 11.sp,
            )
            if (minSpent > 0) {
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(Res.string.bonuses_min_points_from, minSpent.toInt()),
                    color    = if (isCurrent) RelaxWhite.copy(alpha = 0.6f) else RelaxTextHint,
                    fontSize = 10.sp,
                )
            }
            if (isCurrent) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RelaxRed)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(stringResource(Res.string.bonuses_your_level), color = RelaxWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Parked for a future release — re-enable together with the commented-out
// Achievements section above.
/*
@Composable
private fun AchievementCard(achievement: tj.relax.core.api.AchievementApiResponse) {
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
            textAlign  = androidx.compose.ui.text.style.TextAlign.Center,
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
*/

private val CircleShape = RoundedCornerShape(50)
private val RelaxGold   = Color(0xFFD4AF37)
