package tj.relax.ui.screens.profile

import AuthField
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tj.relax.R
import tj.relax.ui.components.RelaxButton
import tj.relax.ui.theme.RelaxError
import tj.relax.ui.theme.RelaxTextPrimary
import tj.relax.ui.theme.RelaxWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordSheet(
    state: ChangePasswordUiState,
    onSubmit: (current: String, new: String, confirm: String) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword     by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentVisible   by remember { mutableStateOf(false) }
    var newVisible        by remember { mutableStateOf(false) }
    var confirmVisible    by remember { mutableStateOf(false) }

    LaunchedEffect(state.success) {
        if (state.success) onDismiss()
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = RelaxWhite) {
        Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 24.dp)) {
            Text(
                stringResource(R.string.change_password_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = RelaxTextPrimary,
            )
            Spacer(Modifier.height(16.dp))

            AuthField(
                value = currentPassword,
                onChange = { currentPassword = it },
                placeholder = stringResource(R.string.change_password_current_placeholder),
                icon = Icons.Rounded.Lock,
                keyboard = KeyboardType.Password,
                isPassword = true,
                passwordVisible = currentVisible,
                onTogglePassword = { currentVisible = !currentVisible },
            )
            Spacer(Modifier.height(14.dp))
            AuthField(
                value = newPassword,
                onChange = { newPassword = it },
                placeholder = stringResource(R.string.change_password_new_placeholder),
                icon = Icons.Rounded.Lock,
                keyboard = KeyboardType.Password,
                isPassword = true,
                passwordVisible = newVisible,
                onTogglePassword = { newVisible = !newVisible },
            )
            Spacer(Modifier.height(14.dp))
            AuthField(
                value = confirmPassword,
                onChange = { confirmPassword = it },
                placeholder = stringResource(R.string.change_password_confirm_placeholder),
                icon = Icons.Rounded.Lock,
                keyboard = KeyboardType.Password,
                isPassword = true,
                passwordVisible = confirmVisible,
                onTogglePassword = { confirmVisible = !confirmVisible },
            )

            if (state.error != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = state.error,
                    color = RelaxError,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(20.dp))
            RelaxButton(
                text = stringResource(R.string.change_password_submit),
                onClick = { onSubmit(currentPassword, newPassword, confirmPassword) },
                modifier = Modifier.fillMaxWidth(),
                isLoading = state.isLoading,
            )
        }
    }
}
