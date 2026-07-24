package tj.relax.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tj.relax.generated.resources.*
import tj.relax.core.util.LocaleManager
import tj.relax.ui.theme.RelaxDark
import tj.relax.ui.theme.RelaxTextPrimary
import tj.relax.ui.theme.RelaxWhite

private data class LanguageOption(val code: String, val labelRes: org.jetbrains.compose.resources.StringResource)

private val languageOptions = listOf(
    LanguageOption(LocaleManager.RUSSIAN, Res.string.language_russian),
    LanguageOption(LocaleManager.TAJIK,   Res.string.language_tajik),
    LanguageOption(LocaleManager.ENGLISH, Res.string.language_english),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSheet(
    currentLanguage: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = RelaxWhite) {
        Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 24.dp)) {
            Text(
                stringResource(Res.string.language_sheet_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = RelaxTextPrimary,
            )
            Spacer(Modifier.height(8.dp))

            languageOptions.forEach { option ->
                val selected = option.code == currentLanguage
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSelect(option.code) }
                        .padding(horizontal = 4.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(option.labelRes),
                        style = MaterialTheme.typography.titleSmall,
                        color = RelaxTextPrimary,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f),
                    )
                    if (selected) {
                        Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(50)).background(RelaxDark), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Check, null, tint = RelaxWhite, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}
