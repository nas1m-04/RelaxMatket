package tj.dastras.ui.screens.catalog.presentation


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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tj.dastras.ui.theme.*

private val SORT_OPTIONS = listOf(
    null         to "По умолчанию",
    "popular"    to "Популярные",
    "new"        to "Новинки",
    "price_asc"  to "Цена: по возрастанию",
    "price_desc" to "Цена: по убыванию",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogSortSheet(
    current:   String?,
    onDismiss: () -> Unit,
    onSelect: (String?) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = RelaxWhite,
        sheetState       = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text       = "Сортировка",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                color      = RelaxTextPrimary,
                modifier   = Modifier.padding(bottom = 12.dp)
            )

            SORT_OPTIONS.forEach { (key, label) ->
                val isSelected = current == key
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) RelaxSurfaceAlt else RelaxWhite)
                        .clickable { onSelect(key); onDismiss() }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Text(
                        text       = label,
                        fontSize   = 15.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color      = if (isSelected) RelaxDark else RelaxTextPrimary,
                    )
                    if (isSelected) {
                        Icon(
                            imageVector        = Icons.Rounded.Check,
                            contentDescription = null,
                            tint               = RelaxDark,
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}