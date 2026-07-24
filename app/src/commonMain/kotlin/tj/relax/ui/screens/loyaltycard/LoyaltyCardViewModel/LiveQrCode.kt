package tj.relax.ui.screens.loyaltycard.LoyaltyCardViewModel

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import tj.relax.core.util.generateQrImageBitmap
import tj.relax.ui.theme.RelaxDark
import tj.relax.ui.theme.RelaxDivider
import tj.relax.ui.theme.RelaxRed
import tj.relax.ui.theme.RelaxTextHint
import tj.relax.ui.theme.RelaxTextSecondary

@Composable
fun LiveQrCode(viewModel: LoyaltyViewModel) {

    DisposableEffect (Unit) {
        viewModel.startQrRefresh()
        onDispose { viewModel.stopQrRefresh() }
    }

    when (val state = viewModel.qrState) {

        is QrState.Loading -> {
            Box(
                modifier         = Modifier.size(180.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = RelaxDark, modifier = Modifier.size(32.dp))
            }
        }

        is QrState.Ready -> {
            // Обратный отсчёт
            var secondsLeft by remember { mutableIntStateOf(0) }
            LaunchedEffect (state.expiresAt) {
                while (true) {
                    secondsLeft = (state.expiresAt - Clock.System.now()).inWholeSeconds
                        .toInt()
                        .coerceAtLeast(0)
                    delay(1_000L)
                }
            }

            // Генерируем bitmap только когда токен меняется
            val bitmap = remember(state.token) {
                generateQrImageBitmap(state.token)
            }

            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, RelaxDivider, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Image(
                        bitmap             = bitmap,
                        contentDescription = "QR код",
                        modifier           = Modifier.fillMaxSize(),
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Таймер
                val minutes = secondsLeft / 60
                val seconds = secondsLeft % 60
                val isExpiring = secondsLeft < 30

                Row (verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Timer,
                        contentDescription = null,
                        tint     = if (isExpiring) RelaxRed else RelaxTextHint,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text  = "Обновится через %d:%02d".format(minutes, seconds),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isExpiring) RelaxRed else RelaxTextHint,
                    )
                }
            }
        }

        is QrState.Error -> {
            Column(
                modifier            = Modifier.size(180.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(Icons.Rounded.ErrorOutline, null, tint = RelaxRed, modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(8.dp))
                Text("Не удалось загрузить QR", style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
                Spacer(Modifier.height(8.dp))
                TextButton (onClick = { viewModel.startQrRefresh() }) {
                    Text("Повторить", color = RelaxDark)
                }
            }
        }
    }
}