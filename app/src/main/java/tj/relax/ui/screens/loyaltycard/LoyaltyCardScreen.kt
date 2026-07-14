package tj.relax.ui.screens.loyaltycard

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import tj.relax.R
import tj.relax.core.api.ErrorPresenter
import tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel.LiveQrCode
import tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel.LoyaltyViewModel
import tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel.formatMemberSince
import tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel.formatTransactionDate
import tj.relax.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoyaltyCardScreen(viewModel: LoyaltyViewModel = hiltViewModel()) {
    val state   = viewModel.uiState
    val summary = state.summary
    var isRefreshing by remember { mutableStateOf(false) }
    var activeTxTab   by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    val shimmer   = rememberInfiniteTransition(label = "card_shimmer")
    val shimmerX  by shimmer.animateFloat(
        initialValue = -600f,
        targetValue  = 600f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "shimmer",
    )

    if (state.isLoading && summary == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(RelaxBackground),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = RelaxDark)
        }
        return
    }

    val level      = summary!!.level
    val nextLevel  = summary.nextLevel
    val progress   = summary.progressToNextLevel.toFloat()
    val cardNumber = summary.cardNumber ?: ""

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
            modifier       = Modifier
                .fillMaxSize()
                .background(RelaxBackground),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(RelaxDark, RelaxDarkSecondary)))
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Text(
                        stringResource(R.string.loyalty_title),
                        color      = RelaxWhite,
                        style      = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            item {
                Spacer(Modifier.height(24.dp))

                // ── Premium Loyalty Card ────────────────────────
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .height(220.dp)
                        .shadow(
                            elevation = 24.dp,
                            shape     = RoundedCornerShape(24.dp),
                            spotColor = RelaxBlack.copy(alpha = 0.4f),
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    RelaxBlack,
                                    RelaxDark,
                                    RelaxDarkSecondary,
                                ),
                                start = Offset(0f, 0f),
                                end   = Offset(1000f, 600f),
                            )
                        )
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color  = Color.White.copy(alpha = 0.04f),
                            radius = 180.dp.toPx(),
                            center = Offset(size.width * 0.85f, size.height * 0.2f),
                        )
                        drawCircle(
                            color  = Color.White.copy(alpha = 0.03f),
                            radius = 120.dp.toPx(),
                            center = Offset(size.width * 0.1f, size.height * 0.9f),
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.04f),
                                        Color.Transparent,
                                    ),
                                    start = Offset(shimmerX - 200f, 0f),
                                    end   = Offset(shimmerX + 200f, 600f),
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                    ) {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.Top,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            Brush.linearGradient(listOf(RelaxRed, Color(0xFFB0262B))),
                                            RoundedCornerShape(10.dp),
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("R", color = RelaxWhite, fontSize = 20.sp, fontWeight = FontWeight.Black)
                                }
                                Spacer(Modifier.width(8.dp))
                                Text("RELAX", color = RelaxWhite, fontSize = 16.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(level.color).copy(alpha = 0.9f))
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text          = level.name.uppercase(),
                                    color         = RelaxWhite,
                                    fontSize      = 11.sp,
                                    fontWeight    = FontWeight.Black,
                                    letterSpacing = 1.5.sp,
                                )
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        Column {
                            Text(stringResource(R.string.loyalty_bonus_balance), color = RelaxTextOnDarkSub, fontSize = 11.sp, letterSpacing = 0.5.sp)
                            Spacer(Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text       = "${summary.bonusBalance.toInt()}",
                                    color      = RelaxWhite,
                                    fontSize   = 42.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Default,
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    stringResource(R.string.loyalty_points_suffix),
                                    color    = RelaxTextOnDarkSub,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(bottom = 8.dp),
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.Bottom,
                        ) {
                            Column {
                                Text(stringResource(R.string.loyalty_owner_label), color = RelaxTextOnDarkSub, fontSize = 9.sp, letterSpacing = 1.sp)
                                Text(viewModel.userName, color = RelaxWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(stringResource(R.string.loyalty_member_since_label), color = RelaxTextOnDarkSub, fontSize = 9.sp, letterSpacing = 1.sp)
                                Text(formatMemberSince(summary.memberSince).uppercase(), color = RelaxWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                }

                item {
                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(RelaxWhite)
                        .padding(16.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.CreditCard, null, tint = RelaxTextSecondary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(cardNumber, style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary, fontFamily = FontFamily.Monospace)
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Rounded.ContentCopy, null, tint = RelaxTextHint, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                }

            item {
                Spacer(Modifier.height(20.dp))

                Card(
                    modifier  = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                    shape     = RoundedCornerShape(20.dp),
                    colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
                    elevation = CardDefaults.cardElevation(0.dp),
                ) {
                    Column(
                        modifier            = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            stringResource(R.string.loyalty_qr_title),
                            style = MaterialTheme.typography.titleSmall,
                            color = RelaxTextSecondary,
                        )
                        Spacer(Modifier.height(16.dp))

                        LiveQrCode(viewModel = viewModel)

                        Spacer(Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.loyalty_barcode_title),
                            style = MaterialTheme.typography.bodySmall,
                            color = RelaxTextHint,
                        )
                    }
                }
            }

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
                                Text(stringResource(R.string.loyalty_level_label), style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
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
                                    Text(stringResource(R.string.loyalty_next_level_label), style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
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
                                stringResource(R.string.loyalty_progress_to_level, nextLevel.name, summary.amountToNextLevel.toInt()),
                                style = MaterialTheme.typography.bodySmall,
                                color = RelaxTextSecondary,
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(stringResource(R.string.loyalty_benefits_title), style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary, fontWeight = FontWeight.Bold)
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

            // ── Bonus history — credits & debits, front and center ──
            if (state.transactions.isNotEmpty() || state.isLoadingMoreTx) {
                item {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        stringResource(R.string.loyalty_history_title),
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
                        listOf(stringResource(R.string.bonuses_tab_credits), stringResource(R.string.bonuses_tab_debits)).forEachIndexed { idx, label ->
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
                            stringResource(R.string.loyalty_history_empty_tab),
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
                                Text(stringResource(R.string.loyalty_history_load_more), color = RelaxDark, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // ── Levels carousel ──────────────────────────────
            if (state.levels.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(20.dp))
                    Text(
                        stringResource(R.string.bonuses_levels_title),
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
                        state.achievements.forEach { achievement ->
                            AchievementCard(achievement)
                        }
                    }
                }
            }
            */

                item {
                Spacer(Modifier.height(20.dp))

                    Card(
                        modifier  = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                        shape     = RoundedCornerShape(20.dp),
                        colors    = CardDefaults.cardColors(containerColor = RelaxDark),
                        elevation = CardDefaults.cardElevation(0.dp),
                    ) {
                        Row(
                            modifier              = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment     = Alignment.CenterVertically,
                        ) {
                            StatBlock(formatBonus(summary.bonusBalance), stringResource(R.string.loyalty_stat_bonuses), RelaxGold)
                            Box(modifier = Modifier.width(1.dp).height(48.dp).background(RelaxWhite.copy(alpha = 0.12f)))
                            StatBlock("${formatBonus(summary.totalSpent)} TJS", stringResource(R.string.loyalty_stat_spent), RelaxWhite)
                            Box(modifier = Modifier.width(1.dp).height(48.dp).background(RelaxWhite.copy(alpha = 0.12f)))
                            // "Кэшбэк" is the customer's current earn rate, not a TJS amount —
                            // showing balance×rate as if it were money earned was meaningless and
                            // rounded to "0 TJS" for most real balances.
                            StatBlock("${level.cashbackPercent.toInt()}%", stringResource(R.string.loyalty_stat_cashback), RelaxDarkSecondary)
                        }
                    }

                Spacer(Modifier.height(24.dp))
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
private fun StatBlock(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(2.dp))
        Text(label, color = RelaxTextOnDarkSub, fontSize = 11.sp)
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
                        .background(RelaxRed)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(stringResource(R.string.bonuses_your_level), color = RelaxWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
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

//@Composable
//private fun BarcodeVisual(modifier: Modifier = Modifier) {
//    val bars = remember {
//        List(40) { i ->
//            Pair(((i * 13 + 42) % 3) + 1, ((i * 7 + 42) % 2) + 1)
//        }
//    }
//    Canvas(modifier = modifier) {
//        var x = 0f
//        bars.forEach { (barW, gapW) ->
//            val bw = barW * size.height * 0.04f
//            val gw = gapW * size.height * 0.03f
//            drawRect(Color(0xFF0F172A), Offset(x, 0f), androidx.compose.ui.geometry.Size(bw, size.height))
//            x += bw + gw
//            if (x >= size.width) return@forEach
//        }
//    }
//}

private val CircleShape = RoundedCornerShape(50)
private val RelaxGold   = Color(0xFFD4AF37)