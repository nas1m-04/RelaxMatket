package tj.dastras.ui.screens.profile

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tj.dastras.R
import tj.dastras.core.util.LocaleManager
import tj.dastras.ui.theme.RelaxDark
import tj.dastras.ui.theme.RelaxTextPrimary
import tj.dastras.ui.theme.RelaxWhite

private data class LanguageOption(val code: String, val labelRes: Int)

private val languageOptions = listOf(
    LanguageOption(LocaleManager.RUSSIAN, R.string.language_russian),
    LanguageOption(LocaleManager.TAJIK,   R.string.language_tajik),
    LanguageOption(LocaleManager.ENGLISH, R.string.language_english),
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
                stringResource(R.string.language_sheet_title),
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
