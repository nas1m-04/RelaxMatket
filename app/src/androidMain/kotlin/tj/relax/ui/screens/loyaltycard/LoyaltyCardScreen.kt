package tj.relax.ui.screens.loyaltycard

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
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
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch
import tj.relax.generated.resources.*
import tj.relax.core.api.ErrorPresenter
import tj.relax.ui.components.activityViewModel
import tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel.LiveQrCode
import tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel.LoyaltyViewModel
import tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel.formatMemberSince
import tj.relax.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoyaltyCardScreen(
    onDetails: () -> Unit = {},
    viewModel: LoyaltyViewModel = activityViewModel(),
) {
    val state   = viewModel.uiState
    val summary = state.summary
    var isRefreshing by remember { mutableStateOf(false) }
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
                        stringResource(Res.string.loyalty_title),
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
                        .clickable(onClick = onDetails)
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
                            Text(stringResource(Res.string.loyalty_bonus_balance), color = RelaxTextOnDarkSub, fontSize = 11.sp, letterSpacing = 0.5.sp)
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
                                    stringResource(Res.string.loyalty_points_suffix),
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
                                Text(stringResource(Res.string.loyalty_owner_label), color = RelaxTextOnDarkSub, fontSize = 9.sp, letterSpacing = 1.sp)
                                Text(viewModel.userName, color = RelaxWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(stringResource(Res.string.loyalty_member_since_label), color = RelaxTextOnDarkSub, fontSize = 9.sp, letterSpacing = 1.sp)
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
                            stringResource(Res.string.loyalty_qr_title),
                            style = MaterialTheme.typography.titleSmall,
                            color = RelaxTextSecondary,
                        )
                        Spacer(Modifier.height(16.dp))

                        LiveQrCode(viewModel = viewModel)

                        Spacer(Modifier.height(16.dp))
                        Text(
                            stringResource(Res.string.loyalty_barcode_title),
                            style = MaterialTheme.typography.bodySmall,
                            color = RelaxTextHint,
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }

        }
    }
}

