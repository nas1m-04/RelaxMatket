package tj.relax.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tj.relax.core.util.openExternalUrl

private const val TELEGRAM_HANDLE = "tj_ba_hub"
private const val TELEGRAM_URL = "https://t.me/$TELEGRAM_HANDLE"

/** Clickable "@tj_ba_hub" Telegram link — opens the channel in Telegram (or a browser fallback). */
@Composable
fun TelegramLinkRow(modifier: Modifier = Modifier, color: Color = Color(0xFF229ED9)) {
    Row(
        modifier = modifier.clickable { openExternalUrl(TELEGRAM_URL) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Rounded.Send, contentDescription = null, tint = color, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(6.dp))
        Text("@$TELEGRAM_HANDLE", color = color, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
