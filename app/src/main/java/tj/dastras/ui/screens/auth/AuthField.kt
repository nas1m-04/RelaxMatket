import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import tj.dastras.ui.theme.RelaxBackground
import tj.dastras.ui.theme.RelaxDark
import tj.dastras.ui.theme.RelaxDivider
import tj.dastras.ui.theme.RelaxTextHint
import tj.dastras.ui.theme.RelaxTextPrimary
import tj.dastras.ui.theme.RelaxTextSecondary

@Composable
fun AuthField(
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboard: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onChange,
        placeholder   = {
            Text(placeholder, color = RelaxTextHint, style = MaterialTheme.typography.bodyMedium)
        },
        leadingIcon   = {
            Icon(icon, null, tint = RelaxTextSecondary, modifier = Modifier.size(20.dp))
        },
        trailingIcon  = if (isPassword && onTogglePassword != null) ({
            IconButton (onClick = onTogglePassword) {
                Icon(
                    if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                    null,
                    tint     = RelaxTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }) else null,
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        singleLine      = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        shape           = RoundedCornerShape(14.dp),
        colors          = OutlinedTextFieldDefaults.colors(
            focusedContainerColor   = RelaxBackground,
            unfocusedContainerColor = RelaxBackground,
            focusedBorderColor      = RelaxDark,
            unfocusedBorderColor    = RelaxDivider,
            focusedTextColor        = RelaxTextPrimary,
            unfocusedTextColor      = RelaxTextPrimary,
        ),
        textStyle = MaterialTheme.typography.bodyLarge,
        modifier  = Modifier.fillMaxWidth().height(56.dp),
    )
}

private val CircleShape = RoundedCornerShape(50)