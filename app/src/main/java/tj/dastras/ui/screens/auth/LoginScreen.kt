package tj.dastras.ui.screens.auth

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.stringResource
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
    var phone    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val state = viewModel.uiState

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onNavigateToMain()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RelaxBackground)
    ) {
        // Dark header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Brush.verticalGradient(listOf(RelaxDark, RelaxDarkSecondary))),
        ) {
            // Decor
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 60.dp, y = (-40).dp)
                    .alpha(0.07f)
                    .background(RelaxWhite, CircleShape)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Brush.linearGradient(listOf(RelaxRed, RelaxOrange)), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("R", color = RelaxWhite, fontSize = 36.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(16.dp))
                Text("RELAX", color = RelaxWhite, fontSize = 26.sp, fontWeight = FontWeight.Black, letterSpacing = 6.sp)
                Spacer(Modifier.height(6.dp))
                Text(stringResource(R.string.app_tagline), color = RelaxTextOnDarkSub, fontSize = 13.sp)
            }
        }

        // Form card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(RelaxBackground)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 32.dp),
        ) {
            Text(stringResource(R.string.login_title), style = MaterialTheme.typography.headlineLarge, color = RelaxTextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(stringResource(R.string.login_subtitle), color = RelaxTextSecondary, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)

            Spacer(Modifier.height(28.dp))

            // Phone field
            Text(stringResource(R.string.auth_phone_label), style = MaterialTheme.typography.labelLarge, color = RelaxTextPrimary)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = phone,
                onValueChange = { if (it.length <= 18) phone = it },
                placeholder   = { Text(stringResource(R.string.auth_phone_placeholder), color = RelaxTextHint) },
                leadingIcon   = {
                    Icon(Icons.Rounded.Phone, null, tint = RelaxTextSecondary, modifier = Modifier.size(20.dp))
                },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth().height(56.dp),
                shape         = RoundedCornerShape(14.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor    = RelaxWhite,
                    unfocusedContainerColor  = RelaxWhite,
                    focusedBorderColor       = RelaxDark,
                    unfocusedBorderColor     = RelaxDivider,
                    focusedTextColor         = RelaxTextPrimary,
                    unfocusedTextColor       = RelaxTextPrimary,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                textStyle     = MaterialTheme.typography.bodyLarge,
            )

            Spacer(Modifier.height(16.dp))

            // Password field
            Text(stringResource(R.string.auth_password_label), style = MaterialTheme.typography.labelLarge, color = RelaxTextPrimary)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = password,
                onValueChange = { password = it },
                placeholder   = { Text(stringResource(R.string.login_password_placeholder), color = RelaxTextHint) },
                leadingIcon   = {
                    Icon(Icons.Rounded.Lock, null, tint = RelaxTextSecondary, modifier = Modifier.size(20.dp))
                },
                trailingIcon  = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                            null,
                            tint = RelaxTextSecondary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth().height(56.dp),
                shape         = RoundedCornerShape(14.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor    = RelaxWhite,
                    unfocusedContainerColor  = RelaxWhite,
                    focusedBorderColor       = RelaxDark,
                    unfocusedBorderColor     = RelaxDivider,
                    focusedTextColor         = RelaxTextPrimary,
                    unfocusedTextColor       = RelaxTextPrimary,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle     = MaterialTheme.typography.bodyLarge,
            )

            Spacer(Modifier.height(24.dp))

            RelaxButton(
                text      = stringResource(R.string.login_button),
                onClick   = { viewModel.login(phone, password) },
                modifier  = Modifier.fillMaxWidth(),
                isLoading = state.isLoading,
            )

            if (state.error != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text       = state.error,
                    color      = RelaxError,
                    style      = MaterialTheme.typography.bodySmall,
                    textAlign  = TextAlign.Center,
                    modifier   = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(stringResource(R.string.login_no_account), color = RelaxTextSecondary, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text     = stringResource(R.string.login_register_link),
                    color    = RelaxRed,
                    fontWeight = FontWeight.SemiBold,
                    style    = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { onNavigateToRegister() },
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Divider(modifier = Modifier.weight(1f), color = RelaxDivider)
                Text(stringResource(R.string.login_or_divider), color = RelaxTextHint, style = MaterialTheme.typography.bodySmall)
                Divider(modifier = Modifier.weight(1f), color = RelaxDivider)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text      = stringResource(R.string.login_terms),
                color     = RelaxTextHint,
                style     = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
            )
        }
    }
}
