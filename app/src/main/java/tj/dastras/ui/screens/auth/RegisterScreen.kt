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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import tj.dastras.R
import tj.dastras.ui.components.RelaxButton
import tj.dastras.ui.theme.*

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    var name            by remember { mutableStateOf("") }
    var phone           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val state = viewModel.uiState

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onNavigateToMain()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RelaxBackground)
            .statusBarsPadding(),
    ) {
        // Back button
        IconButton(
            onClick  = onBack,
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(RelaxSurfaceAlt)
        ) {
            Icon(Icons.Rounded.ArrowBackIosNew, null, tint = RelaxTextPrimary, modifier = Modifier.size(16.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Text(stringResource(R.string.register_title), style = MaterialTheme.typography.headlineLarge, color = RelaxTextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(stringResource(R.string.register_subtitle), color = RelaxTextSecondary, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)

            Spacer(Modifier.height(28.dp))

            // Name field
            Text(stringResource(R.string.register_name_label), style = MaterialTheme.typography.labelLarge, color = RelaxTextPrimary)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = name,
                onValueChange = { name = it },
                placeholder   = { Text(stringResource(R.string.register_name_placeholder), color = RelaxTextHint) },
                leadingIcon   = {
                    Icon(Icons.Rounded.Person, null, tint = RelaxTextSecondary, modifier = Modifier.size(20.dp))
                },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth().height(56.dp),
                shape         = RoundedCornerShape(14.dp),
                colors        = textFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                textStyle     = MaterialTheme.typography.bodyLarge,
            )

            Spacer(Modifier.height(16.dp))

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
                colors        = textFieldColors(),
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
                placeholder   = { Text(stringResource(R.string.register_password_placeholder), color = RelaxTextHint) },
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
                colors        = textFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle     = MaterialTheme.typography.bodyLarge,
            )

            Spacer(Modifier.height(16.dp))

            // Confirm password field
            Text(stringResource(R.string.register_confirm_password_label), style = MaterialTheme.typography.labelLarge, color = RelaxTextPrimary)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder   = { Text(stringResource(R.string.register_confirm_password_placeholder), color = RelaxTextHint) },
                leadingIcon   = {
                    Icon(Icons.Rounded.Lock, null, tint = RelaxTextSecondary, modifier = Modifier.size(20.dp))
                },
                trailingIcon  = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            if (confirmPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                            null,
                            tint = RelaxTextSecondary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth().height(56.dp),
                shape         = RoundedCornerShape(14.dp),
                colors        = textFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle     = MaterialTheme.typography.bodyLarge,
            )

            Spacer(Modifier.height(24.dp))

            RelaxButton(
                text      = stringResource(R.string.register_button),
                onClick   = { viewModel.register(name, phone, password, confirmPassword) },
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
                Text(stringResource(R.string.register_have_account), color = RelaxTextSecondary, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text     = stringResource(R.string.register_login_link),
                    color    = RelaxRed,
                    fontWeight = FontWeight.SemiBold,
                    style    = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { onBack() },
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text      = stringResource(R.string.register_terms),
                color     = RelaxTextHint,
                style     = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor    = RelaxWhite,
    unfocusedContainerColor  = RelaxWhite,
    focusedBorderColor       = RelaxDark,
    unfocusedBorderColor     = RelaxDivider,
    focusedTextColor         = RelaxTextPrimary,
    unfocusedTextColor       = RelaxTextPrimary,
)
