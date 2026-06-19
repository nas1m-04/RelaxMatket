package tj.dastras.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import tj.dastras.ui.theme.RelaxDark
import tj.dastras.ui.theme.RelaxDivider
import tj.dastras.ui.theme.RelaxError
import tj.dastras.ui.theme.RelaxInputBg
import tj.dastras.ui.theme.RelaxTextHint
import tj.dastras.ui.theme.RelaxTextPrimary
import tj.dastras.ui.theme.RelaxTextSecondary
import tj.dastras.ui.theme.RelaxWhite

// ── Text Field ────────────────────────────────────────────────
@Composable
fun RelaxTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    singleLine: Boolean = true,
    keyboardType: androidx.compose.ui.text.input.KeyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value          = value,
            onValueChange  = onValueChange,
            placeholder    = {
                Text(
                    placeholder,
                    color = RelaxTextHint,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            leadingIcon    = if (leadingIcon != null) ({
                Icon(leadingIcon, null, tint = RelaxTextSecondary, modifier = Modifier.size(20.dp))
            }) else null,
            trailingIcon   = trailingIcon,
            isError        = isError,
            singleLine     = singleLine,
            modifier       = Modifier.fillMaxWidth().height(56.dp),
            shape          = RoundedCornerShape(14.dp),
            colors         = OutlinedTextFieldDefaults.colors(
                focusedContainerColor    = RelaxWhite,
                unfocusedContainerColor  = RelaxInputBg,
                focusedBorderColor       = RelaxDark,
                unfocusedBorderColor     = RelaxDivider,
                errorBorderColor         = RelaxError,
                focusedTextColor         = RelaxTextPrimary,
                unfocusedTextColor       = RelaxTextPrimary,
            ),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
            textStyle      = MaterialTheme.typography.bodyLarge,
        )
        if (isError && errorMessage.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(errorMessage, color = RelaxError, style = MaterialTheme.typography.bodySmall)
        }
    }
}