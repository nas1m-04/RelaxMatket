package tj.dastras.ui.screens.profile

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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import tj.dastras.data.MockData
import tj.dastras.ui.components.RelaxDivider
import tj.dastras.ui.theme.*

@Composable
fun ProfileScreen(
    onOrders: () -> Unit,
    onFavorites: () -> Unit,
    onNotifications: () -> Unit,
) {
    val user = MockData.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RelaxBackground)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(RelaxDark, RelaxDarkSecondary)))
                .statusBarsPadding()
        ) {
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .shadow(12.dp, CircleShape)
                        .clip(CircleShape)
                        .border(3.dp, RelaxWhite.copy(alpha = 0.3f), CircleShape)
                ) {
                    AsyncImage(
                        model             = user.avatarUrl,
                        contentDescription = user.name,
                        contentScale      = ContentScale.Crop,
                        modifier          = Modifier.fillMaxSize(),
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(user.name, color = RelaxWhite, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(user.phone, color = RelaxTextOnDarkSub, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))
                // Level badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(user.level.color).copy(alpha = 0.8f))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(Icons.Rounded.Stars, null, tint = RelaxWhite, modifier = Modifier.size(16.dp))
                    Text("${user.level.name} · ${user.bonusBalance} баллов", color = RelaxWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))

            // Quick stats
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                QuickStat("${user.bonusBalance}", "Бонусов", modifier = Modifier.weight(1f))
                QuickStat("${user.totalSpent.toInt()} ₽", "Потрачено", modifier = Modifier.weight(1f))
                QuickStat("3", "Заказа", modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))

            // Menu sections
            ProfileSection(title = "Покупки") {
                ProfileMenuItem(Icons.Rounded.Receipt, "История заказов", "3 заказа", onClick = onOrders)
                ProfileMenuItem(Icons.Rounded.FavoriteBorder, "Избранное", "5 товаров", onClick = onFavorites)
            }

            Spacer(Modifier.height(12.dp))

            ProfileSection(title = "Аккаунт") {
                ProfileMenuItem(Icons.Rounded.Person, "Личные данные", user.email, onClick = {})
                ProfileMenuItem(Icons.Rounded.Home, "Мои адреса", "1 адрес", onClick = {})
                ProfileMenuItem(Icons.Rounded.Notifications, "Уведомления", "Включены", onClick = onNotifications)
            }

            Spacer(Modifier.height(12.dp))

            ProfileSection(title = "Настройки") {
                ProfileMenuItem(Icons.Rounded.Language, "Язык", "Русский", onClick = {})
                ProfileMenuItem(Icons.Rounded.DarkMode, "Тема", "Светлая", onClick = {})
            }

            Spacer(Modifier.height(12.dp))

            ProfileSection(title = "Поддержка") {
                ProfileMenuItem(Icons.Rounded.Help, "Помощь", "", onClick = {})
                ProfileMenuItem(Icons.Rounded.Chat, "Чат с поддержкой", "Онлайн", onClick = {})
                ProfileMenuItem(Icons.Rounded.Info, "О приложении", "v1.0.0", onClick = {})
            }

            Spacer(Modifier.height(16.dp))

            // Logout
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFEE2E2))
                    .clickable {}
                    .padding(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Rounded.Logout, null, tint = RelaxError, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Выйти из аккаунта", color = RelaxError, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun QuickStat(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(
            modifier            = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, fontWeight = FontWeight.Black, fontSize = 16.sp, color = RelaxTextPrimary)
            Text(label, style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
        }
    }
}

@Composable
private fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(title, style = MaterialTheme.typography.labelMedium, color = RelaxTextSecondary, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(18.dp),
            colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
            elevation = CardDefaults.cardElevation(0.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun ColumnScope.ProfileMenuItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(RelaxSurfaceAlt),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = RelaxTextPrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary)
            if (subtitle.isNotEmpty()) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
            }
        }
        Icon(Icons.Rounded.ChevronRight, null, tint = RelaxTextHint, modifier = Modifier.size(20.dp))
    }
    RelaxDivider(modifier = Modifier.padding(start = 68.dp))
}

private val CircleShape  = RoundedCornerShape(50)
private val RelaxError   = Color(0xFFEF4444)
