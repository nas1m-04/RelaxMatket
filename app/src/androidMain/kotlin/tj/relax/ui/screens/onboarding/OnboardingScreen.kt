package tj.relax.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch
import tj.relax.generated.resources.*
import tj.relax.ui.components.RelaxButton
import tj.relax.ui.theme.*

private data class OnboardingPage(
    val emoji: String,
    val titleRes: org.jetbrains.compose.resources.StringResource,
    val subtitleRes: org.jetbrains.compose.resources.StringResource,
    val bg: List<Color>,
    val accent: Color,
)

private val pages = listOf(
    OnboardingPage(
        emoji       = "🛒",
        titleRes    = Res.string.onboarding_title_1,
        subtitleRes = Res.string.onboarding_subtitle_1,
        bg          = listOf(RelaxDark, RelaxDarkSecondary),
        accent      = RelaxRed,
    ),
    OnboardingPage(
        emoji       = "💎",
        titleRes    = Res.string.onboarding_title_2,
        subtitleRes = Res.string.onboarding_subtitle_2,
        bg          = listOf(RelaxBlack, RelaxDark),
        accent      = Color(0xFFD4AF37),
    ),
    OnboardingPage(
        emoji       = "🔥",
        titleRes    = Res.string.onboarding_title_3,
        subtitleRes = Res.string.onboarding_subtitle_3,
        bg          = listOf(Color(0xFF08080B), RelaxBlack),
        accent      = RelaxRed,
    ),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope      = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state    = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            OnboardingPage(pages[page])
        }

        // Dots + Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp)
                .padding(bottom = 52.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Indicator dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(pages.size) { idx ->
                    val selected = idx == pagerState.currentPage
                    val width by animateDpAsState(if (selected) 28.dp else 8.dp, label = "dot")
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (selected) RelaxWhite else RelaxWhite.copy(alpha = 0.35f))
                    )
                }
            }

            Spacer(Modifier.height(36.dp))

            if (pagerState.currentPage < pages.size - 1) {
                Row(
                    modifier  = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onFinished) {
                        Text(stringResource(Res.string.onboarding_skip), color = RelaxWhite.copy(alpha = 0.6f), style = MaterialTheme.typography.labelLarge)
                    }
                    Button(
                        onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                        shape   = RoundedCornerShape(16.dp),
                        colors  = ButtonDefaults.buttonColors(containerColor = RelaxWhite),
                        modifier = Modifier.height(50.dp).padding(start = 8.dp),
                    ) {
                        Text(stringResource(Res.string.onboarding_next), color = RelaxTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(Modifier.width(6.dp))
                        Text("→", color = RelaxTextPrimary, fontSize = 18.sp)
                    }
                }
            } else {
                Button(
                    onClick  = onFinished,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = RelaxRed),
                ) {
                    Text(stringResource(Res.string.onboarding_start_shopping), color = RelaxWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun OnboardingPage(page: OnboardingPage) {
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatY by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue  = 12f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "float",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(page.bg)),
        contentAlignment = Alignment.Center,
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-60).dp)
                .alpha(0.08f)
                .background(RelaxWhite, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 80.dp)
                .alpha(0.07f)
                .background(page.accent, CircleShape)
        )

        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(bottom = 180.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Hero emoji / illustration
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .offset(y = floatY.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(page.accent.copy(alpha = 0.25f), Color.Transparent)
                        ),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(page.emoji, fontSize = 80.sp)
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text       = stringResource(page.titleRes),
                color      = RelaxWhite,
                fontSize   = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign  = TextAlign.Center,
                lineHeight = 38.sp,
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text      = stringResource(page.subtitleRes),
                color     = RelaxWhite.copy(alpha = 0.72f),
                fontSize  = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
            )
        }
    }
}
