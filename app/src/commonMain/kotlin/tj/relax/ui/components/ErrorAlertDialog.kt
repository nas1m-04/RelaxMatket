package tj.relax.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.ReportProblem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import tj.relax.core.util.isDebugBuild
import tj.relax.generated.resources.*
import tj.relax.core.api.ApiException
import tj.relax.core.api.ErrorPresenter
import tj.relax.ui.theme.RelaxDark
import tj.relax.ui.theme.RelaxError
import tj.relax.ui.theme.RelaxSurfaceAlt
import tj.relax.ui.theme.RelaxTextPrimary
import tj.relax.ui.theme.RelaxTextSecondary
import tj.relax.ui.theme.RelaxWarning
import tj.relax.ui.theme.RelaxWhite

@Composable
fun ErrorAlertDialogHost() {
    val error = ErrorPresenter.current
    if (error != null) {
        ErrorAlertDialog(error = error, onDismiss = { ErrorPresenter.dismiss() })
    }
}

@Composable
fun ErrorAlertDialog(error: ApiException, onDismiss: () -> Unit) {
    var detailsExpanded by remember { mutableStateOf(false) }

    if (error.isServerUnavailable) {
        DialogFrame(
            icon        = Icons.Rounded.CloudSync,
            iconTint    = RelaxDark,
            title       = stringResource(Res.string.error_server_updating_title),
            onDismiss   = onDismiss,
        ) {
            Text(
                stringResource(Res.string.error_server_updating_message),
                style      = MaterialTheme.typography.bodyMedium,
                color      = RelaxTextSecondary,
                textAlign  = TextAlign.Center,
            )
        }
        return
    }

    if (error.isValidationError) {
        DialogFrame(
            icon      = Icons.Rounded.ReportProblem,
            iconTint  = RelaxWarning,
            title     = stringResource(Res.string.error_validation_title),
            onDismiss = onDismiss,
        ) {
            Text(
                error.message ?: stringResource(Res.string.error_generic_message),
                style     = MaterialTheme.typography.bodyMedium,
                color     = RelaxTextSecondary,
                textAlign = TextAlign.Center,
            )
        }
        return
    }

    val mainMessage = if (error.isInternalError) {
        stringResource(Res.string.error_generic_message)
    } else {
        error.message ?: stringResource(Res.string.error_generic_message)
    }

    val showDetailsToggle = error.isInternalError &&
        isDebugBuild &&
        (error.exceptionType != null || error.exceptionMessage != null)

    DialogFrame(
        icon      = Icons.Rounded.ErrorOutline,
        iconTint  = RelaxError,
        title     = stringResource(Res.string.error_dialog_title),
        onDismiss = onDismiss,
    ) {
        Text(mainMessage, style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary, textAlign = TextAlign.Center)

        if (showDetailsToggle) {
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { detailsExpanded = !detailsExpanded }) {
                Text(stringResource(if (detailsExpanded) Res.string.error_details_hide else Res.string.error_details_show))
            }
            if (detailsExpanded) {
                error.exceptionType?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = RelaxTextSecondary)
                }
                error.exceptionMessage?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = RelaxTextSecondary)
                }
            }
        }

        if (!error.traceId.isNullOrBlank()) {
            Spacer(Modifier.height(12.dp))
            TraceIdChip(traceId = error.traceId)
        }
    }
}

@Composable
private fun DialogFrame(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    onDismiss: () -> Unit,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape     = RoundedCornerShape(24.dp),
            colors    = CardDefaults.cardColors(containerColor = RelaxWhite),
            elevation = CardDefaults.cardElevation(8.dp),
        ) {
            Column(
                modifier            = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier         = Modifier.size(64.dp).clip(CircleShape).background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(32.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text(title, style = MaterialTheme.typography.titleLarge, color = RelaxTextPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                content()
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick  = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = RelaxDark),
                ) {
                    Text(stringResource(Res.string.error_dialog_ok), color = RelaxWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun TraceIdChip(traceId: String) {
    val clipboard = LocalClipboardManager.current
    var justCopied by remember { mutableStateOf(false) }

    LaunchedEffect(justCopied) {
        if (justCopied) {
            delay(1500L)
            justCopied = false
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RelaxSurfaceAlt)
            .clickable {
                clipboard.setText(AnnotatedString(traceId))
                justCopied = true
            }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(Res.string.error_trace_id_label),
                style = MaterialTheme.typography.labelSmall,
                color = RelaxTextSecondary,
            )
            Text(
                if (justCopied) stringResource(Res.string.error_trace_id_copied) else traceId,
                style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                color = RelaxTextPrimary,
            )
        }
        Spacer(Modifier.width(8.dp))
        Icon(
            if (justCopied) Icons.Rounded.Check else Icons.Rounded.ContentCopy,
            contentDescription = stringResource(Res.string.error_trace_id_copied),
            tint = RelaxTextSecondary,
            modifier = Modifier.height(18.dp).width(18.dp),
        )
    }
}
