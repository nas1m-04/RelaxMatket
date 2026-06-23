package tj.dastras.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import tj.dastras.ui.components.RelaxButton
import tj.dastras.ui.theme.*

@Composable
fun CongratulationsOverlay(
    name: String,
    onDone: () -> Unit,
) {
    var cardVisible   by remember { mutableStateOf(false) }
    var flashVisible  by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Сначала вспышка
        flashVisible = true
        delay(300)
        flashVisible = false
        // Потом карточка
        cardVisible = true
        // Автопереход
        delay(4000)
        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center,
    ) {

        // ── Вспышка ───────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = flashVisible,
            enter   = fadeIn(tween(100)),
            exit    = fadeOut(tween(400)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(Color.White.copy(alpha = 0.9f), Color.Transparent)
                        )
                    )
            )
        }

        // ── Карточка ──────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = cardVisible,
            enter   = scaleIn(
                initialScale  = 0.6f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness    = Spring.StiffnessMedium,
                )
            ) + fadeIn(tween(300)),
        ) {
            Card(
                modifier  = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                shape     = RoundedCornerShape(32.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(32.dp),
            ) {
                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    // ── Плавающие эмодзи ──────────────────────────────────
                    FloatingEmojis()

                    Spacer(Modifier.height(16.dp))

                    // ── Заголовок с градиентом ────────────────────────────
                    GradientText(
                        text     = "Поздравляем!",
                        fontSize = 30.sp,
                    )

                    Spacer(Modifier.height(10.dp))

                    // ── Имя пользователя ──────────────────────────────────
                    PulsingName(name = name.ifBlank { "Добро пожаловать" })

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text       = "Ваш аккаунт успешно создан.\nДобро пожаловать в RELAX!",
                        fontSize   = 14.sp,
                        color      = RelaxTextSecondary,
                        textAlign  = TextAlign.Center,
                        lineHeight = 22.sp,
                    )

                    Spacer(Modifier.height(28.dp))

                    // ── Кнопка ────────────────────────────────────────────
                    RelaxButton(
                        text     = "Войти",
                        onClick  = onDone,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text      = "Перейдёт автоматически...",
                        fontSize  = 11.sp,
                        color     = RelaxTextHint,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

// ── Плавающие эмодзи конфетти ─────────────────────────────────────────────────

@Composable
private fun FloatingEmojis() {
    val emojis = listOf("🎉", "🎊", "✨", "🥳", "⭐", "🎈")

    val infiniteTransition = rememberInfiniteTransition(label = "emojis")

    // У каждого эмодзи своя фаза анимации
    val offsets = emojis.mapIndexed { i, _ ->
        infiniteTransition.animateFloat(
            initialValue  = 0f,
            targetValue   = 1f,
            animationSpec = infiniteRepeatable(
                animation  = tween(1200 + i * 150, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "emoji_$i"
        )
    }

    Box(
        modifier          = Modifier.fillMaxWidth().height(64.dp),
        contentAlignment  = Alignment.Center,
    ) {
        emojis.forEachIndexed { i, emoji ->
            val yOffset = (offsets[i].value * 14f) - 7f
            val xOffset = (-emojis.size / 2f + i) * 36f
            Text(
                text     = emoji,
                fontSize = (20 + (i % 3) * 4).sp,
                modifier = Modifier.offset(x = xOffset.dp, y = yOffset.dp)
            )
        }
    }
}

// ── Имя с пульсирующей анимацией ─────────────────────────────────────────────

@Composable
private fun PulsingName(name: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "name")
    val scale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.05f,
        animationSpec = infiniteRepeatable(
            animation  = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scale"
    )

    Text(
        text      = name,
        fontSize  = 20.sp,
        fontWeight = FontWeight.Bold,
        color     = RelaxDark,
        textAlign = TextAlign.Center,
        modifier  = Modifier.scale(scale)
    )
}

// ── Текст с градиентом ────────────────────────────────────────────────────────

@Composable
private fun GradientText(text: String, fontSize: TextUnit) {
    val gradient = Brush.linearGradient(listOf(RelaxRed, RelaxOrange))
    Text(
        text       = text,
        fontSize   = fontSize,
        fontWeight = FontWeight.Black,
        textAlign  = TextAlign.Center,
        style      = LocalTextStyle.current.copy(
            brush = gradient
        )
    )
}