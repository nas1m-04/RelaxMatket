package tj.dastras.ui.screens.loyaltycard

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import tj.dastras.R
import tj.dastras.ui.components.activityViewModel
import tj.dastras.ui.screens.loyalty.LoyaltyViewModel
import tj.dastras.ui.screens.loyalty.formatMemberSince
import tj.dastras.ui.theme.*

@Composable
fun LoyaltyCardScreen(viewModel: LoyaltyViewModel = activityViewModel()) {
    val state   = viewModel.uiState
    val profile = state.profile
    val summary = state.summary

    if (state.isLoading || profile == null || summary == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(RelaxBackground),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = RelaxDark)
        }
        return
    }

    val level     = summary.level
    val nextLevel = summary.nextLevel
    val progress  = summary.progressToNextLevel.toFloat()
    val cardNumber = summary.cardNumber ?: profile.cardNumber

    // Shimmer for card
    val shimmer = rememberInfiniteTransition(label = "card_shimmer")
    val shimmerX by shimmer.animateFloat(-600f, 600f, infiniteRepeatable(tween(3000, easing = LinearEasing)), label = "shimmer")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RelaxBackground)
            .statusBarsPadding()
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(RelaxDark, RelaxDarkSecondary)))
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                stringResource(R.string.loyalty_title),
                color      = RelaxWhite,
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))

            // ── Premium Loyalty Card ────────────────────────────
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(220.dp)
                    .shadow(
                        elevation  = 24.dp,
                        shape      = RoundedCornerShape(24.dp),
                        spotColor  = Color(0xFF0F172A).copy(alpha = 0.4f),
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0F172A),
                                Color(0xFF1E3A5F),
                                Color(0xFF1E293B),
                            ),
                            start = Offset(0f, 0f),
                            end   = Offset(1000f, 600f),
                        )
                    )
            ) {
                // Decorative circles on card
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

                // Shimmer overlay
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

                // Card content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                ) {
                    // Top row — Logo + Level
                    Row(
                        modifier          = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        // RELAX logo on card
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Brush.linearGradient(listOf(RelaxRed, RelaxOrange)), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("R", color = RelaxWhite, fontSize = 20.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(Modifier.width(8.dp))
                            Text("RELAX", color = RelaxWhite, fontSize = 16.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                        }

                        // Level badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(level.color).copy(alpha = 0.9f))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text       = level.name.uppercase(),
                                color      = RelaxWhite,
                                fontSize   = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp,
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    // Bonus balance — big number
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
                            Text(stringResource(R.string.loyalty_points_suffix), color = RelaxTextOnDarkSub, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Bottom — name + card number
                    Row(
                        modifier          = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Column {
                            Text(stringResource(R.string.loyalty_owner_label), color = RelaxTextOnDarkSub, fontSize = 9.sp, letterSpacing = 1.sp)
                            Text(profile.name.uppercase(), color = RelaxWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(stringResource(R.string.loyalty_member_since_label), color = RelaxTextOnDarkSub, fontSize = 9.sp, letterSpacing = 1.sp)
                            Text(formatMemberSince(summary.memberSince).uppercase(), color = RelaxWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Card number
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

            Spacer(Modifier.height(20.dp))

            // ── QR Code Section ─────────────────────────────────
            Card(
                modifier  = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
                elevation = CardDefaults.cardElevation(0.dp),
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(stringResource(R.string.loyalty_qr_title), style = MaterialTheme.typography.titleSmall, color = RelaxTextSecondary)
                    Spacer(Modifier.height(16.dp))

                    // QR code placeholder (visual)
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, RelaxDivider, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        QrCodeVisual(modifier = Modifier.fillMaxSize())
                    }

                    Spacer(Modifier.height(16.dp))

                    // Barcode
                    Text(stringResource(R.string.loyalty_barcode_title), style = MaterialTheme.typography.bodySmall, color = RelaxTextHint)
                    Spacer(Modifier.height(8.dp))
                    BarcodeVisual(modifier = Modifier.fillMaxWidth().height(50.dp))
                    Spacer(Modifier.height(6.dp))
                    Text(cardNumber.replace("RELAX ", ""), style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Level Progress ──────────────────────────────────
            Card(
                modifier  = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
                elevation = CardDefaults.cardElevation(0.dp),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
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
                        progress      = { progress },
                        modifier      = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color         = Color(level.color),
                        trackColor    = RelaxDivider,
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

            Spacer(Modifier.height(20.dp))

            // ── Stats ───────────────────────────────────────────
            Card(
                modifier  = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = RelaxDark),
                elevation = CardDefaults.cardElevation(0.dp),
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    StatBlock("${summary.bonusBalance.toInt()}", stringResource(R.string.loyalty_stat_bonuses), RelaxGold)
                    Box(modifier = Modifier.width(1.dp).height(48.dp).background(RelaxWhite.copy(alpha = 0.12f)))
                    StatBlock("${summary.totalSpent.toInt()} TJS", stringResource(R.string.loyalty_stat_spent), RelaxWhite)
                    Box(modifier = Modifier.width(1.dp).height(48.dp).background(RelaxWhite.copy(alpha = 0.12f)))
                    StatBlock("${(summary.bonusBalance * (level.cashbackPercent / 100)).toInt()} TJS", stringResource(R.string.loyalty_stat_cashback), RelaxOrange)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatBlock(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(2.dp))
        Text(label, color = RelaxTextOnDarkSub, fontSize = 11.sp)
    }
}

// ── QR Code visual (drawn with Canvas) ────────────────────────
@Composable
private fun QrCodeVisual(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cell  = size.width / 12f
        val color = Color(0xFF0F172A)
        val cells = listOf(
            listOf(1,1,1,1,1,1,1,0,1,0,1,0),
            listOf(1,0,0,0,0,0,1,0,0,1,1,0),
            listOf(1,0,1,1,1,0,1,0,1,0,0,1),
            listOf(1,0,1,1,1,0,1,0,0,1,0,1),
            listOf(1,0,1,1,1,0,1,0,1,0,1,0),
            listOf(1,0,0,0,0,0,1,0,1,1,0,1),
            listOf(1,1,1,1,1,1,1,0,1,0,1,0),
            listOf(0,0,0,0,0,0,0,0,0,1,1,0),
            listOf(1,0,1,1,0,1,1,0,1,0,0,1),
            listOf(0,1,0,1,1,0,0,0,0,1,0,0),
            listOf(1,1,1,1,0,1,1,0,1,0,1,1),
            listOf(0,1,0,0,1,0,0,0,0,1,1,0),
        )
        cells.forEachIndexed { row, rowData ->
            rowData.forEachIndexed { col, filled ->
                if (filled == 1) {
                    drawRect(
                        color    = color,
                        topLeft  = Offset(col * cell + 1, row * cell + 1),
                        size     = androidx.compose.ui.geometry.Size(cell - 2, cell - 2),
                    )
                }
            }
        }
    }
}

// ── Barcode visual ─────────────────────────────────────────────
@Composable
private fun BarcodeVisual(modifier: Modifier = Modifier) {
    val bars = remember {
        val seed = 42
        List(40) { i ->
            val w = ((i * 13 + seed) % 3) + 1
            val gap = ((i * 7 + seed) % 2) + 1
            Pair(w, gap)
        }
    }
    Canvas(modifier = modifier) {
        var x = 0f
        bars.forEach { (barW, gapW) ->
            val bw = barW * size.height * 0.04f
            val gw = gapW * size.height * 0.03f
            drawRect(Color(0xFF0F172A), Offset(x, 0f), androidx.compose.ui.geometry.Size(bw, size.height))
            x += bw + gw
            if (x >= size.width) return@forEach
        }
    }
}

private val CircleShape = RoundedCornerShape(50)
private val RelaxGold   = Color(0xFFD4AF37)
