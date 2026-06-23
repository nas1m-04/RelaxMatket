package tj.dastras.ui.screens.auth

import AuthField
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import tj.dastras.R
import tj.dastras.ui.components.RelaxButton
import tj.dastras.ui.theme.*

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    var phone           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val state = viewModel.uiState

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onNavigateToMain()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RelaxDark)
    ) {

        // ── Верхняя часть — логотип ───────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .statusBarsPadding(),
            contentAlignment = Alignment.Center,
        ) {
            // Декоративный круг фоном
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 80.dp, y = (-60).dp)
                    .alpha(0.06f)
                    .background(RelaxWhite, RoundedCornerShape(50))
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            Brush.linearGradient(listOf(RelaxRed, RelaxOrange)),
                            RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text       = "R",
                        color      = RelaxWhite,
                        fontSize   = 40.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text          = "RELAX",
                    color         = RelaxWhite,
                    fontSize      = 28.sp,
                    fontWeight    = FontWeight.Black,
                    letterSpacing = 8.sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text     = stringResource(R.string.app_tagline),
                    color    = RelaxTextOnDarkSub,
                    fontSize = 13.sp
                )
            }
        }

        // ── Нижняя карточка ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    RelaxWhite,
                    RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 28.dp)
                .padding(top = 32.dp, bottom = 40.dp),
        ) {
            Text(
                text       = stringResource(R.string.login_title),
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color      = RelaxTextPrimary
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text       = stringResource(R.string.login_subtitle),
                color      = RelaxTextSecondary,
                style      = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(28.dp))

            AuthField(
                value       = phone,
                onChange    = { if (it.length <= 18) phone = it },
                placeholder = stringResource(R.string.auth_phone_placeholder),
                icon        = Icons.Rounded.Phone,
                keyboard    = KeyboardType.Phone,
            )

            Spacer(Modifier.height(14.dp))

            AuthField(
                value            = password,
                onChange         = { password = it },
                placeholder      = stringResource(R.string.login_password_placeholder),
                icon             = Icons.Rounded.Lock,
                keyboard         = KeyboardType.Password,
                isPassword       = true,
                passwordVisible  = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible },
            )

            Spacer(Modifier.height(28.dp))

            RelaxButton(
                text      = stringResource(R.string.login_button),
                onClick   = { viewModel.login(phone, password) },
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
                    text  = stringResource(R.string.login_no_account),
                    color = RelaxTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text       = stringResource(R.string.login_register_link),
                    color      = RelaxRed,
                    fontWeight = FontWeight.SemiBold,
                    style      = MaterialTheme.typography.bodyMedium,
                    modifier   = Modifier.clickable { onNavigateToRegister() },
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text       = stringResource(R.string.login_terms),
                color      = RelaxTextHint,
                style      = MaterialTheme.typography.bodySmall,
                textAlign  = TextAlign.Center,
                lineHeight = 16.sp,
                modifier   = Modifier.fillMaxWidth()
            )
        }
    }
}