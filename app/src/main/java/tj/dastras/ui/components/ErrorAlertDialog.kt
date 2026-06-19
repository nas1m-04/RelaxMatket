package tj.dastras.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.ReportProblem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import tj.dastras.BuildConfig
import tj.dastras.R
import tj.dastras.core.api.ApiException
import tj.dastras.core.api.ErrorPresenter
import tj.dastras.ui.theme.RelaxError
import tj.dastras.ui.theme.RelaxSurfaceAlt
import tj.dastras.ui.theme.RelaxTextPrimary
import tj.dastras.ui.theme.RelaxTextSecondary
import tj.dastras.ui.theme.RelaxWarning

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
        AlertDialog(
            onDismissRequest = onDismiss,
            icon  = { Icon(Icons.Rounded.CloudSync, contentDescription = null, tint = RelaxWarning) },
            title = { Text(stringResource(R.string.error_server_updating_title)) },
            text  = { Text(stringResource(R.string.error_server_updating_message), style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.error_dialog_ok)) }
            },
        )
        return
    }

    if (error.isValidationError) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon  = { Icon(Icons.Rounded.ReportProblem, contentDescription = null, tint = RelaxWarning) },
            title = { Text(stringResource(R.string.error_validation_title)) },
            text  = {
                Text(
                    error.message ?: stringResource(R.string.error_generic_message),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.error_dialog_ok)) }
            },
        )
        return
    }

    val mainMessage = if (error.isInternalError) {
        stringResource(R.string.error_generic_message)
    } else {
        error.message ?: stringResource(R.string.error_generic_message)
    }

    val showDetailsToggle = error.isInternalError &&
        BuildConfig.DEBUG &&
        (error.exceptionType != null || error.exceptionMessage != null)

    AlertDialog(
        onDismissRequest = onDismiss,
        icon  = { Icon(Icons.Rounded.ErrorOutline, contentDescription = null, tint = RelaxError) },
        title = { Text(stringResource(R.string.error_dialog_title)) },
        text  = {
            Column {
                Text(mainMessage, style = MaterialTheme.typography.bodyMedium)

                if (showDetailsToggle) {
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { detailsExpanded = !detailsExpanded }) {
                        Text(
                            stringResource(
                                if (detailsExpanded) R.string.error_details_hide else R.string.error_details_show
                            )
                        )
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
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.error_dialog_ok)) }
        },
    )
}

@Composable
private fun TraceIdChip(traceId: String) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val copiedMessage = stringResource(R.string.error_trace_id_copied)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RelaxSurfaceAlt)
            .clickable {
                clipboard.setText(AnnotatedString(traceId))
                Toast.makeText(context, copiedMessage, Toast.LENGTH_SHORT).show()
            }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(R.string.error_trace_id_label),
                style = MaterialTheme.typography.labelSmall,
                color = RelaxTextSecondary,
            )
            Text(
                traceId,
                style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                color = RelaxTextPrimary,
            )
        }
        Spacer(Modifier.width(8.dp))
        Icon(
            Icons.Rounded.ContentCopy,
            contentDescription = stringResource(R.string.error_trace_id_copied),
            tint = RelaxTextSecondary,
            modifier = Modifier.height(18.dp).width(18.dp),
        )
    }
}
