package tj.relax.ui.screens.auth

import AuthField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import tj.relax.R
import tj.relax.ui.components.RelaxButton
import tj.relax.ui.theme.*

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    var name                   by remember { mutableStateOf("") }
    var phone                  by remember { mutableStateOf("") }
    var password               by remember { mutableStateOf("") }
    var confirmPassword        by remember { mutableStateOf("") }
    var passwordVisible        by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var secretQuestion         by remember { mutableStateOf("") }
    var secretAnswer           by remember { mutableStateOf("") }
    val state = viewModel.uiState

    // ДОЛЖНО БЫТЬ ТАК:
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onNavigateToMain()
    }

    if (state.isRegistered) {
        CongratulationsOverlay(
            name   = state.registeredName,
            onDone = { viewModel.onCongratulationsDone() }
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(RelaxDark)) {

        // ── Шапка ─────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            IconButton(
                onClick  = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(RelaxWhite.copy(alpha = 0.15f))
            ) {
                Icon(
                    Icons.Rounded.ArrowBackIosNew,
                    null,
                    tint     = RelaxWhite,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Brush.linearGradient(listOf(RelaxDark, RelaxDarkSecondary)),
                        RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text("R", color = RelaxWhite, fontSize = 36.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "RELAX",
                color         = RelaxWhite,
                fontSize      = 24.sp,
                fontWeight    = FontWeight.Black,
                letterSpacing = 8.sp
            )
        }

        // ── Карточка снизу ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    RelaxWhite,
                    RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
                .padding(top = 32.dp, bottom = 40.dp),
        ) {
            Text(
                stringResource(R.string.register_title),
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color      = RelaxTextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.register_subtitle),
                color      = RelaxTextSecondary,
                style      = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(24.dp))

            AuthField(
                value       = name,
                onChange    = { name = it },
                placeholder = stringResource(R.string.register_name_placeholder),
                icon        = Icons.Rounded.Person,
            )
            Spacer(Modifier.height(14.dp))
            AuthField(
                value       = phone,
                onChange    = { if (it.length <= 18) phone = it },
                placeholder = stringResource(R.string.auth_phone_placeholder),
                icon        = Icons.Rounded.Phone,
                keyboard    = KeyboardType.Phone,
            )
            Spacer(Modifier.height(14.dp))
            AuthField(
                value              = password,
                onChange           = { password = it },
                placeholder        = stringResource(R.string.register_password_placeholder),
                icon               = Icons.Rounded.Lock,
                keyboard           = KeyboardType.Password,
                isPassword         = true,
                passwordVisible    = passwordVisible,
                onTogglePassword   = { passwordVisible = !passwordVisible },
            )
            Spacer(Modifier.height(14.dp))
            AuthField(
                value              = confirmPassword,
                onChange           = { confirmPassword = it },
                placeholder        = stringResource(R.string.register_confirm_password_placeholder),
                icon               = Icons.Rounded.Lock,
                keyboard           = KeyboardType.Password,
                isPassword         = true,
                passwordVisible    = confirmPasswordVisible,
                onTogglePassword   = { confirmPasswordVisible = !confirmPasswordVisible },
            )

            Spacer(Modifier.height(18.dp))
            Text(
                stringResource(R.string.register_secret_hint),
                color      = RelaxTextHint,
                style      = MaterialTheme.typography.bodySmall,
                lineHeight = 16.sp,
            )
            Spacer(Modifier.height(10.dp))
            AuthField(
                value       = secretQuestion,
                onChange    = { secretQuestion = it },
                placeholder = stringResource(R.string.register_secret_question_placeholder),
                icon        = Icons.Rounded.HelpOutline,
            )
            Spacer(Modifier.height(14.dp))
            AuthField(
                value       = secretAnswer,
                onChange    = { secretAnswer = it },
                placeholder = stringResource(R.string.register_secret_answer_placeholder),
                icon        = Icons.Rounded.QuestionAnswer,
            )

            Spacer(Modifier.height(28.dp))

            RelaxButton(
                text      = stringResource(R.string.register_button),
                onClick   = { viewModel.register(name, phone, password, confirmPassword, secretQuestion, secretAnswer) },
                modifier  = Modifier.fillMaxWidth(),
                isLoading = state.isLoading,
            )

            if (state.error != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text      = state.error,
                    color     = RelaxError,
                    style     = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(R.string.register_have_account),
                    color = RelaxTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    stringResource(R.string.register_login_link),
                    color      = RelaxRed,
                    fontWeight = FontWeight.SemiBold,
                    style      = MaterialTheme.typography.bodyMedium,
                    modifier   = Modifier.clickable { onBack() },
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text      = stringResource(R.string.register_terms),
                color     = RelaxTextHint,
                style     = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier  = Modifier.fillMaxWidth()
            )
        }
    }
}