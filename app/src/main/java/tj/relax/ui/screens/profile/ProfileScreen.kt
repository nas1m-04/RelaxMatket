package tj.relax.ui.screens.profile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import tj.relax.BuildConfig
import tj.relax.R
import tj.relax.ui.components.RelaxDivider
import tj.relax.ui.theme.*
import tj.relax.core.util.LocaleManager
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
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

    var name  by remember(user?.name)  { mutableStateOf(user?.name  ?: "") }
    var email by remember(user?.email) { mutableStateOf(user?.email ?: "") }
    val hasChanges = user != null && (name.trim() != user.name || email.trim() != user.email)
    val nameError  = name.trim().length < 2 && name.isNotBlank()
    val emailError = email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

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

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground).verticalScroll(rememberScrollState())) {

        // ── Header — light treatment, this is "you", not the brand hero ──
        Column(
            modifier            = Modifier.fillMaxWidth().statusBarsPadding().padding(top = 24.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(88.dp).shadow(8.dp, CircleShape).clip(CircleShape)
                    .border(3.dp, RelaxDark.copy(alpha = 0.12f), CircleShape)
                    .background(RelaxDarkSecondary)
                    .clickable { showAvatarSheet = true },
                contentAlignment = Alignment.Center,
            ) {
                if (user.avatarUrl.isNullOrEmpty()) {
                    Icon(Icons.Rounded.Person, null, tint = RelaxWhite, modifier = Modifier.size(44.dp))
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
                        .background(RelaxRed).border(2.dp, RelaxBackground, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.PhotoCamera, null, tint = RelaxWhite, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(user.name.ifEmpty { stringResource(R.string.profile_user_default) }, color = RelaxTextPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(user.phone, color = RelaxTextSecondary, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier              = Modifier.clip(RoundedCornerShape(20.dp)).background(Color(user.level.color)).padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(Icons.Rounded.Stars, null, tint = RelaxWhite, modifier = Modifier.size(16.dp))
                Text(stringResource(R.string.profile_points, user.level.name, user.bonusBalance.toInt()), color = RelaxWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Personal data — inline, no separate screen ──
        ProfileSection(title = stringResource(R.string.menu_personal_data)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ProfileField(
                    label       = stringResource(R.string.register_name_label),
                    value       = name,
                    onChange    = { name = it },
                    icon        = Icons.Rounded.Person,
                    placeholder = stringResource(R.string.register_name_placeholder),
                    isError     = nameError,
                )
                ProfileField(
                    label       = "Email",
                    value       = email,
                    onChange    = { email = it },
                    icon        = Icons.Rounded.Email,
                    placeholder = "example@mail.com",
                    keyboard    = KeyboardType.Email,
                    isError     = emailError,
                )
                ProfileFieldReadOnly(
                    label = stringResource(R.string.auth_phone_label),
                    value = user.phone,
                    icon  = Icons.Rounded.Phone,
                )
                AnimatedVisibility(visible = hasChanges && !nameError && !emailError) {
                    Button(
                        onClick  = { viewModel.updateProfile(name.trim(), email.trim()) },
                        enabled  = !state.isLoading,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = RelaxDark),
                    ) {
                        Icon(Icons.Rounded.Check, null, tint = RelaxWhite, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.save_changes), color = RelaxWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        ProfileSection(title = stringResource(R.string.section_account)) {
            ProfileMenuItem(Icons.Rounded.Store,         stringResource(R.string.menu_branch),         state.branchName ?: stringResource(R.string.menu_branch_not_selected), onClick = onSelectBranch)
            ProfileMenuItem(Icons.Rounded.Notifications, stringResource(R.string.menu_notifications),  stringResource(R.string.menu_notifications_on), onClick = onNotifications)
        }

        Spacer(Modifier.height(12.dp))

        ProfileSection(title = stringResource(R.string.section_settings)) {
            ProfileMenuItem(Icons.Rounded.Language, stringResource(R.string.menu_language), languageDisplayName(currentLanguage), onClick = { showLanguageSheet = true })
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

        Spacer(Modifier.height(20.dp))

        Text(
            "RELAX · v${BuildConfig.VERSION_NAME}",
            style     = MaterialTheme.typography.bodySmall,
            color     = RelaxTextHint,
            modifier  = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))
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
private fun ProfileField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    icon: ImageVector,
    placeholder: String,
    keyboard: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = RelaxTextSecondary)
        OutlinedTextField(
            value           = value,
            onValueChange   = onChange,
            singleLine      = true,
            isError         = isError,
            placeholder     = { Text(placeholder, color = RelaxTextHint) },
            leadingIcon     = { Icon(icon, null, tint = if (isError) RelaxError else RelaxTextSecondary, modifier = Modifier.size(20.dp)) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboard),
            shape           = RoundedCornerShape(14.dp),
            colors          = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = RelaxDark,
                unfocusedBorderColor = RelaxDivider,
                focusedTextColor     = RelaxTextPrimary,
                unfocusedTextColor   = RelaxTextPrimary,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ProfileFieldReadOnly(label: String, value: String, icon: ImageVector) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = RelaxTextSecondary)
        OutlinedTextField(
            value         = value,
            onValueChange = {},
            readOnly      = true,
            singleLine    = true,
            leadingIcon   = { Icon(icon, null, tint = RelaxTextHint, modifier = Modifier.size(20.dp)) },
            trailingIcon  = { Icon(Icons.Rounded.Lock, null, tint = RelaxTextHint, modifier = Modifier.size(18.dp)) },
            shape         = RoundedCornerShape(14.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = RelaxDivider,
                unfocusedBorderColor = RelaxDivider,
                focusedTextColor     = RelaxTextHint,
                unfocusedTextColor   = RelaxTextHint,
            ),
            modifier = Modifier.fillMaxWidth().alpha(0.7f),
        )
    }
}

@Composable
private fun languageDisplayName(code: String): String = when (code) {
    LocaleManager.ENGLISH -> stringResource(R.string.language_english)
    LocaleManager.TAJIK   -> stringResource(R.string.language_tajik)
    else                  -> stringResource(R.string.language_russian)
}

private val CircleShape = RoundedCornerShape(50)
private val RelaxError  = Color(0xFFEF4444)
