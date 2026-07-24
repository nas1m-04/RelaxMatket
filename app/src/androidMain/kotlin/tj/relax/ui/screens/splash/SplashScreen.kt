package tj.relax.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import tj.relax.generated.resources.*
import tj.relax.ui.components.TelegramLinkRow
import tj.relax.ui.theme.*

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val alphaAnim     = remember { Animatable(0f) }
    val scaleAnim     = remember { Animatable(0.7f) }
    val slideAnim     = remember { Animatable(30f) }
    val taglineAlpha  = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(200)
        alphaAnim.animateTo(1f, animationSpec = tween(700, easing = EaseOutCubic))
        scaleAnim.animateTo(1f, animationSpec = tween(700, easing = EaseOutBack))
        slideAnim.animateTo(0f, animationSpec = tween(600, easing = EaseOutCubic))
        delay(300)
        taglineAlpha.animateTo(1f, animationSpec = tween(500))
        delay(1000)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(RelaxBlack, RelaxDark, RelaxDarkSecondary)
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = 120.dp, y = (-180).dp)
                .alpha(0.06f)
                .background(RelaxWhite, shape = androidx.compose.foundation.shape.CircleShape)
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = 200.dp)
                .alpha(0.05f)
                .background(RelaxRed, shape = androidx.compose.foundation.shape.CircleShape)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Logo container
            Box(
                modifier = Modifier
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
                    .offset(y = slideAnim.value.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(Res.drawable.logo_mark),
                        contentDescription = null,
                        modifier = Modifier.size(88.dp),
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text       = "RELAX",
                        color      = RelaxWhite,
                        fontSize   = 36.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 8.sp,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tagline
            Column(
                modifier = Modifier.alpha(taglineAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text      = stringResource(Res.string.app_tagline),
                    color     = RelaxTextOnDarkSub,
                    fontSize  = 14.sp,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                )
            }
        }

        // Loading indicator
        LinearProgressIndicator(
            modifier  = Modifier
                .fillMaxWidth(0.4f)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .alpha(taglineAlpha.value),
            color     = RelaxRed,
            trackColor = RelaxWhite.copy(alpha = 0.15f),
        )

        TelegramLinkRow(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
                .alpha(taglineAlpha.value),
        )
    }
}
