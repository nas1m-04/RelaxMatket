package tj.relax.ui.screens.catalog.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tj.relax.ui.screens.catalog.presentation.data.CatalogUiState
import tj.relax.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogFilterSheet(
    state: CatalogUiState,
    onDismiss: () -> Unit,
    onApply: (newOnly: Boolean, priceFrom: Int?, priceTo: Int?) -> Unit,
) {
    var newOnly    by remember { mutableStateOf(state.showNewOnly) }
    var priceFrom  by remember { mutableStateOf(state.priceFrom?.toString() ?: "") }
    var priceTo    by remember { mutableStateOf(state.priceTo?.toString() ?: "") }

    ModalBottomSheet(
        onDismissRequest    = onDismiss,
        containerColor      = RelaxWhite,
        sheetState          = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text       = "Фильтры",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                color      = RelaxTextPrimary
            )

            // ── Новинки ────────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Только новинки", color = RelaxTextPrimary, fontSize = 15.sp)
                Switch(
                    checked         = newOnly,
                    onCheckedChange = { newOnly = it },
                    colors          = SwitchDefaults.colors(
                        checkedThumbColor  = RelaxWhite,
                        checkedTrackColor  = RelaxDark,
                        uncheckedTrackColor = RelaxInputBg,
                    )
                )
            }

            HorizontalDivider(color = RelaxDivider)

            // ── Цена ───────────────────────────────────────────────────────
            Text("Цена (сомони)", color = RelaxTextSecondary, fontSize = 13.sp)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                PriceInput(
                    value       = priceFrom,
                    placeholder = "От",
                    onChange    = { priceFrom = it },
                    modifier    = Modifier.weight(1f)
                )
                Text("—", color = RelaxTextSecondary)
                PriceInput(
                    value       = priceTo,
                    placeholder = "До",
                    onChange    = { priceTo = it },
                    modifier    = Modifier.weight(1f)
                )
            }

            HorizontalDivider(color = RelaxDivider)

            // ── Кнопки ─────────────────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Сбросить
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(RelaxInputBg)
                        .clickable {
                            newOnly   = false
                            priceFrom = ""
                            priceTo   = ""
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Сбросить", color = RelaxTextPrimary, fontWeight = FontWeight.Medium)
                }

                // Применить
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(RelaxDark)
                        .clickable {
                            onApply(
                                newOnly,
                                priceFrom.toIntOrNull(),
                                priceTo.toIntOrNull()
                            )
                            onDismiss()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Применить", color = RelaxWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PriceInput(
    value: String,
    placeholder: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value         = value,
        onValueChange = { if (it.all { c -> c.isDigit() }) onChange(it) },
        placeholder   = { Text(placeholder, color = RelaxTextHint) },
        singleLine    = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape         = RoundedCornerShape(12.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = RelaxDark,
            focusedTextColor     = RelaxTextPrimary,
            unfocusedTextColor   = RelaxTextPrimary,
        ),
        modifier = modifier.height(52.dp)
    )
}