package tj.dastras.ui.screens.profile

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import tj.dastras.ui.theme.*
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state   = viewModel.uiState
    val profile = state.profile
    val context = LocalContext.current

    var showAvatarSheet by remember { mutableStateOf(false) }

    // Инициализируем поля как только профиль загрузится
    var name  by remember(profile?.name)  { mutableStateOf(profile?.name  ?: "") }
    var email by remember(profile?.email) { mutableStateOf(profile?.email ?: "") }

    val hasChanges = profile != null && (name.trim() != profile.name || email.trim() != profile.email)
    val nameError  = name.trim().length < 2 && name.isNotBlank()
    val emailError = email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // — Лаунчеры для аватара —
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val mimeType = context.contentResolver.getType(it) ?: "image/jpeg"
            context.contentResolver.openInputStream(it)?.use { input ->
                viewModel.uploadAvatar(input.readBytes(), mimeType)
            }
        }
        showAvatarSheet = false
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val stream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 85, stream)
            viewModel.uploadAvatar(stream.toByteArray(), "image/jpeg")
        }
        showAvatarSheet = false
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) cameraLauncher.launch(null)
    }

    LaunchedEffect(Unit) {
        viewModel.load(forceRefresh = false)
    }

    // — Bottom sheet выбора аватара —
    if (showAvatarSheet) {
        AvatarPickerSheet(
            hasAvatar   = !profile?.avatarUrl.isNullOrEmpty(),
            isUploading = state.isUploadingAvatar,
            onCamera    = {
                val granted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
                if (granted) cameraLauncher.launch(null)
                else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onGallery   = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onDelete    = { viewModel.removeAvatar(); showAvatarSheet = false },
            onDismiss   = { showAvatarSheet = false },
        )
    }

    // — Загрузка —
    if (state.isLoading && profile == null) {
        Scaffold(
            containerColor = RelaxBackground,
            topBar = {
                TopAppBar(
                    title = { Text("Личные данные", fontWeight = FontWeight.Bold, color = RelaxTextPrimary) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Rounded.ArrowBackIosNew, null, tint = RelaxTextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = RelaxWhite),
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = RelaxDark)
            }
        }
        return
    }

    Scaffold(
        containerColor = RelaxBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Личные данные",
                        fontWeight = FontWeight.Bold,
                        color      = RelaxTextPrimary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBackIosNew, null, tint = RelaxTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RelaxWhite),
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Шапка с аватаром ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
//                    .background(Brush.verticalGradient(listOf(RelaxDark, RelaxDarkSecondary)))
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .shadow(16.dp, CircleShape)
                            .clip(CircleShape)
                            .border(3.dp, RelaxWhite.copy(alpha = 0.25f), CircleShape)
                            .background(RelaxDarkSecondary)
                            .clickable { showAvatarSheet = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (profile?.avatarUrl.isNullOrEmpty()) {
                            Icon(
                                Icons.Rounded.Person,
                                null,
                                tint     = RelaxTextOnDarkSub,
                                modifier = Modifier.size(48.dp),
                            )
                        } else {
                            AsyncImage(
                                model              = profile?.avatarUrl,
                                contentDescription = profile?.name,
                                contentScale       = ContentScale.Crop,
                                modifier           = Modifier.fillMaxSize(),
                            )
                        }
                        // Оверлей загрузки аватара
                        if (state.isUploadingAvatar) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.45f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(
                                    modifier    = Modifier.size(28.dp),
                                    strokeWidth = 2.dp,
                                    color       = RelaxWhite,
                                )
                            }
                        } else {
                            // Иконка камеры поверх
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    Icons.Rounded.PhotoCamera,
                                    null,
                                    tint     = RelaxWhite,
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        text       = profile?.name?.ifEmpty { "Пользователь" } ?: "Пользователь",
                        color      = RelaxTextSecondary,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text     = profile?.phone ?: "",
                        color    = RelaxTextSecondary,
                        fontSize = 13.sp,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Форма ─────────────────────────────────────────────────────
            Column(
                modifier            = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {

                Text(
                    "Основная информация",
                    style    = MaterialTheme.typography.labelMedium,
                    color    = RelaxTextSecondary,
                    modifier = Modifier.padding(start = 4.dp),
                )

                ProfileField(
                    label       = "Имя",
                    value       = name,
                    onChange    = { name = it },
                    icon        = Icons.Rounded.Person,
                    placeholder = "Введите имя",
                    isError     = nameError,
                    errorText   = "Минимум 2 символа",
                )

                ProfileField(
                    label       = "Email",
                    value       = email,
                    onChange    = { email = it },
                    icon        = Icons.Rounded.Email,
                    placeholder = "example@mail.com",
                    keyboard    = KeyboardType.Email,
                    isError     = emailError,
                    errorText   = "Некорректный email",
                )

                // Телефон — только чтение
                ProfileFieldReadOnly(
                    label = "Телефон",
                    value = profile?.phone ?: "",
                    icon  = Icons.Rounded.Phone,
                )

                Spacer(Modifier.height(8.dp))

                // ── Кнопка сохранить ──────────────────────────────────────
                AnimatedVisibility(visible = hasChanges) {
                    Button(
                        onClick = {
                            if (!nameError && !emailError) {
                                viewModel.updateProfile(name.trim(), email.trim())
                                onBack()
                            }
                        },
                        enabled  = !state.isLoading && !nameError && !emailError,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = RelaxDark,
                            disabledContainerColor = RelaxDark.copy(alpha = 0.5f),
                        ),
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                color       = RelaxWhite,
                                modifier    = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Icon(Icons.Rounded.Check, null, tint = RelaxWhite)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Сохранить изменения",
                                color      = RelaxWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 15.sp,
                            )
                        }
                    }
                }

                // Инфо-карточка
                Card(
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = RelaxSurfaceAlt),
                    elevation = CardDefaults.cardElevation(0.dp),
                ) {
                    Row(
                        modifier              = Modifier.padding(16.dp),
                        verticalAlignment     = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            Icons.Rounded.Info,
                            null,
                            tint     = RelaxTextSecondary,
                            modifier = Modifier.size(18.dp).padding(top = 1.dp),
                        )
                        Text(
                            "Телефон изменить нельзя — это ваш логин. Для смены обратитесь в поддержку.",
                            style      = MaterialTheme.typography.bodySmall,
                            color      = RelaxTextSecondary,
                            lineHeight = 18.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Вспомогательные компоненты ────────────────────────────────────────────────

@Composable
private fun ProfileField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    placeholder: String,
    keyboard: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorText: String = "",
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = RelaxTextSecondary)
        OutlinedTextField(
            value           = value,
            onValueChange   = onChange,
            singleLine      = true,
            isError         = isError,
            placeholder     = { Text(placeholder, color = RelaxTextHint) },
            leadingIcon     = {
                Icon(
                    icon,
                    null,
                    tint     = if (isError) MaterialTheme.colorScheme.error else RelaxTextSecondary,
                    modifier = Modifier.size(20.dp),
                )
            },
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
        AnimatedVisibility(visible = isError) {
            Text(errorText, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ProfileFieldReadOnly(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
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
                focusedBorderColor     = RelaxDivider,
                unfocusedBorderColor   = RelaxDivider,
                focusedTextColor       = RelaxTextHint,
                unfocusedTextColor     = RelaxTextHint,
                disabledContainerColor = RelaxInputBg,
            ),
            modifier = Modifier.fillMaxWidth().alpha(0.7f),
        )
    }
}

private val CircleShape = RoundedCornerShape(50)