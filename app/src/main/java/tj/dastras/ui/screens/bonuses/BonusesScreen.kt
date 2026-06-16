package tj.dastras.ui.screens.bonuses

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import tj.dastras.R
import tj.dastras.data.remote.BonusTransactionApiResponse
import tj.dastras.ui.components.RelaxDivider
import tj.dastras.ui.screens.loyalty.LoyaltyViewModel
import tj.dastras.ui.screens.loyalty.formatTransactionDate
import tj.dastras.ui.theme.*

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

    val transactions = state.transactions
    val level = summary.level

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
                        Text("${summary.bonusBalance.toInt()}", color = RelaxWhite, fontSize = 36.sp, fontWeight = FontWeight.Black)
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
            item {
                Spacer(Modifier.height(24.dp))
                Text(stringResource(R.string.bonuses_achievements_title), style = MaterialTheme.typography.headlineSmall, color = RelaxTextPrimary, modifier = Modifier.padding(horizontal = 20.dp))
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier        = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AchievementChip("🛒", stringResource(R.string.bonuses_achievement_first_purchase), true)
                    AchievementChip("🔥", stringResource(R.string.bonuses_achievement_10_purchases), true)
                    AchievementChip("💎", stringResource(R.string.bonuses_achievement_50_purchases), false)
                    AchievementChip("👑", stringResource(R.string.bonuses_achievement_vip), false)
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

            items(filtered) { tx ->
                BonusTransactionItem(tx)
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
private fun AchievementChip(emoji: String, label: String, unlocked: Boolean) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (unlocked) RelaxDark else RelaxSurfaceAlt)
            .padding(12.dp)
            .alpha(if (unlocked) 1f else 0.5f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(emoji, fontSize = 28.sp)
        Spacer(Modifier.height(6.dp))
        Text(label, color = if (unlocked) RelaxWhite else RelaxTextSecondary, fontSize = 9.sp, textAlign = TextAlign.Center, lineHeight = 12.sp)
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
            text       = "${if (tx.isCredit) "+" else "−"}${tx.amount.toInt()}",
            fontSize   = 16.sp,
            fontWeight = FontWeight.Bold,
            color      = if (tx.isCredit) RelaxSuccess else RelaxError,
        )
    }
    RelaxDivider(modifier = Modifier.padding(horizontal = 20.dp))
}

private val CircleShape    = RoundedCornerShape(50)
private val RelaxSuccessBg = Color(0xFFDCFCE7)
private val RelaxErrorBg   = Color(0xFFFEE2E2)
private val RelaxSuccess   = Color(0xFF22C55E)
private val RelaxError     = Color(0xFFEF4444)
private val RelaxGold      = Color(0xFFD4AF37)
