package tj.relax.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import tj.relax.generated.resources.*
import tj.relax.ui.theme.RelaxRed
import tj.relax.ui.theme.RelaxTextPrimary


// ── Section Header ─────────────────────────────────────────────
@Composable
fun SectionHeader(
    title: String,
    onSeeAll: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier            = modifier.fillMaxWidth(),
        verticalAlignment   = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text  = title,
            style = MaterialTheme.typography.headlineSmall,
            color = RelaxTextPrimary,
        )
        if (onSeeAll != null) {
            TextButton(onClick = onSeeAll) {
                Text(
                    text  = stringResource(Res.string.relax_see_all),
                    style = MaterialTheme.typography.labelLarge,
                    color = RelaxRed,
                )
            }
        }
    }
}
