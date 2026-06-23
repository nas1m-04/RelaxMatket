package tj.dastras.ui.screens.profile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import tj.dastras.R
import tj.dastras.ui.components.RelaxDivider
import tj.dastras.ui.theme.*
import tj.dastras.core.util.LocaleManager
import tj.dastras.ui.screens.loyalty.formatMemberSince
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onOrders: () -> Unit,
    onFavorites: () -> Unit,
    onNotifications: () -> Unit,
    onSelectBranch: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state   = viewModel.uiState
    val user    = state.profile
    val context = LocalContext.current
    var showLogoutDialog  by remember { mutableStateOf(false) }
    var showAvatarSheet   by remember { mutableStateOf(false) }
    var showLanguageSheet by remember { mutableStateOf(false) }
    var currentLanguage   by remember { mutableStateOf(LocaleManager.getCurrentLanguage()) }
    var showEditProfile by remember { mutableStateOf(false) }

    // Если показываем редактирование — рендерим поверх
    if (showEditProfile) {
        val profile = user ?: return
        EditProfileScreen(
            profile        = profile,
            isSaving       = state.isLoading,
            onBack         = { showEditProfile = false },
            onSave         = { name, email ->
                viewModel.updateProfile(name, email)
                showEditProfile = false
            },
            onChangeAvatar = { showAvatarSheet = true },
        )
        return
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            val mimeType = context.contentResolver.getType(it) ?: "image/jpeg"
            context.contentResolver.openInputStream(it)?.use { input ->
                viewModel.uploadAvatar(input.readBytes(), mimeType)
            }
        }
        showAvatarSheet = false
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val stream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 85, stream)
            viewModel.uploadAvatar(stream.toByteArray(), "image/jpeg")
        }
        showAvatarSheet = false
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(null)
    }

    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) onLoggedOut()
    }

    LaunchedEffect(Unit) {
        viewModel.load(forceRefresh = true)
    }

    if (showAvatarSheet) {
        AvatarPickerSheet(
            hasAvatar   = !user?.avatarUrl.isNullOrEmpty(),
            isUploading = state.isUploadingAvatar,
            onCamera    = {
                val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                if (granted) cameraLauncher.launch(null) else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onGallery   = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
            onDelete    = { viewModel.removeAvatar(); showAvatarSheet = false },
            onDismiss   = { showAvatarSheet = false },
        )
    }

    if (showLanguageSheet) {
        LanguageSheet(
            currentLanguage = currentLanguage,
            onSelect = { code ->
                LocaleManager.setLanguage(code)
                currentLanguage = code
                showLanguageSheet = false
            },
            onDismiss = { showLanguageSheet = false },
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon             = { Icon(Icons.Rounded.Logout, null, tint = RelaxError) },
            title            = { Text(stringResource(R.string.logout_dialog_title)) },
            text             = { Text(stringResource(R.string.logout_dialog_text)) },
            confirmButton    = {
                TextButton(onClick = { showLogoutDialog = false; viewModel.logout() }) {
                    Text(stringResource(R.string.logout_confirm), color = RelaxError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton    = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.logout_cancel))
                }
            },
        )
    }

    if (state.isLoading || user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = RelaxDark)
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        Box(
            modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(RelaxDark, RelaxDarkSecondary))).statusBarsPadding()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(88.dp).shadow(12.dp, CircleShape).clip(CircleShape)
                        .border(3.dp, RelaxWhite.copy(alpha = 0.3f), CircleShape)
                        .background(RelaxDarkSecondary)
                        .clickable { showAvatarSheet = true },
                    contentAlignment = Alignment.Center,
                ) {
                    if (user.avatarUrl.isNullOrEmpty()) {
                        Icon(Icons.Rounded.Person, null, tint = RelaxTextOnDarkSub, modifier = Modifier.size(44.dp))
                    } else {
                        AsyncImage(model = user.avatarUrl, contentDescription = user.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    }
                    if (state.isUploadingAvatar) {
                        Box(modifier = Modifier.fillMaxSize().background(RelaxOverlay), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 2.dp, color = RelaxWhite)
                        }
                    }
                    Box(
                        modifier = Modifier.align(Alignment.BottomEnd).size(26.dp).clip(CircleShape)
                            .background(RelaxOrange).border(2.dp, RelaxDark, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.PhotoCamera, null, tint = RelaxWhite, modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(user.name.ifEmpty { stringResource(R.string.profile_user_default) }, color = RelaxWhite, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(user.phone, color = RelaxTextOnDarkSub, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier              = Modifier.clip(RoundedCornerShape(20.dp)).background(Color(user.level.color).copy(alpha = 0.8f)).padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(Icons.Rounded.Stars, null, tint = RelaxWhite, modifier = Modifier.size(16.dp))
                    Text(stringResource(R.string.profile_points, user.level.name, user.bonusBalance.toInt()), color = RelaxWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickStat("${user.bonusBalance.toInt()}", stringResource(R.string.profile_stat_bonuses), modifier = Modifier.weight(1f))
                QuickStat("${user.totalSpent.toInt()} TJS", stringResource(R.string.profile_stat_spent), modifier = Modifier.weight(1f))
                QuickStat(formatMemberSince(user.memberSince), stringResource(R.string.profile_stat_with_us), modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))

            ProfileSection(title = stringResource(R.string.section_purchases)) {
                ProfileMenuItem(Icons.Rounded.Receipt,       stringResource(R.string.menu_order_history), stringResource(R.string.menu_my_orders), onClick = onOrders)
                ProfileMenuItem(Icons.Rounded.FavoriteBorder,stringResource(R.string.menu_favorites),      stringResource(R.string.menu_favorites_count, user.favoriteIds.size), onClick = onFavorites)
            }

            Spacer(Modifier.height(12.dp))

            ProfileSection(title = stringResource(R.string.section_account)) {
                ProfileMenuItem(Icons.Rounded.Person,        stringResource(R.string.menu_personal_data), user.email.ifEmpty { stringResource(R.string.menu_not_specified) }, onClick = {showEditProfile = true})
                ProfileMenuItem(Icons.Rounded.Store,         stringResource(R.string.menu_branch),         state.branchName ?: stringResource(R.string.menu_branch_not_selected), onClick = onSelectBranch)
                ProfileMenuItem(Icons.Rounded.Notifications, stringResource(R.string.menu_notifications),  stringResource(R.string.menu_notifications_on), onClick = onNotifications)
            }

            Spacer(Modifier.height(12.dp))

            ProfileSection(title = stringResource(R.string.section_settings)) {
                ProfileMenuItem(Icons.Rounded.Language, stringResource(R.string.menu_language), languageDisplayName(currentLanguage), onClick = { showLanguageSheet = true })
                ProfileMenuItem(Icons.Rounded.DarkMode, stringResource(R.string.menu_theme), stringResource(R.string.menu_theme_light), onClick = {})
            }

            Spacer(Modifier.height(12.dp))

            ProfileSection(title = stringResource(R.string.section_support)) {
                ProfileMenuItem(Icons.Rounded.Help,  stringResource(R.string.menu_help),         "",        onClick = {})
                ProfileMenuItem(Icons.Rounded.Chat,  stringResource(R.string.menu_chat_support), stringResource(R.string.menu_online), onClick = {})
                ProfileMenuItem(Icons.Rounded.Info,  stringResource(R.string.menu_about),        "v1.0.0",  onClick = {})
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)).background(Color(0xFFFEE2E2))
                    .clickable { showLogoutDialog = true }.padding(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Rounded.Logout, null, tint = RelaxError, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.logout_button), color = RelaxError, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun QuickStat(value: String, label: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = RelaxWhite), elevation = CardDefaults.cardElevation(0.dp)) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.Black, fontSize = 16.sp, color = RelaxTextPrimary)
            Text(label, style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
        }
    }
}

@Composable
private fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(title, style = MaterialTheme.typography.labelMedium, color = RelaxTextSecondary, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = RelaxWhite), elevation = CardDefaults.cardElevation(0.dp)) {
            content()
        }
    }
}

@Composable
private fun ColumnScope.ProfileMenuItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(RelaxSurfaceAlt), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = RelaxTextPrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary)
            if (subtitle.isNotEmpty()) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
        }
        Icon(Icons.Rounded.ChevronRight, null, tint = RelaxTextHint, modifier = Modifier.size(20.dp))
    }
    RelaxDivider(modifier = Modifier.padding(start = 68.dp))
}

@Composable
private fun languageDisplayName(code: String): String = when (code) {
    LocaleManager.ENGLISH -> stringResource(R.string.language_english)
    LocaleManager.TAJIK   -> stringResource(R.string.language_tajik)
    else                  -> stringResource(R.string.language_russian)
}

private val CircleShape = RoundedCornerShape(50)
private val RelaxError  = Color(0xFFEF4444)
