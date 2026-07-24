package tj.relax.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tj.relax.generated.resources.*
import tj.relax.ui.theme.RelaxError
import tj.relax.ui.theme.RelaxSurfaceAlt
import tj.relax.ui.theme.RelaxTextPrimary
import tj.relax.ui.theme.RelaxTextSecondary
import tj.relax.ui.theme.RelaxWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarPickerSheet(
    hasAvatar: Boolean,
    isUploading: Boolean,
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = RelaxWhite) {
        Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 24.dp)) {
            Text(stringResource(Res.string.avatar_sheet_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = RelaxTextPrimary)
            Spacer(Modifier.height(8.dp))

            AvatarSheetOption(icon = Icons.Rounded.PhotoCamera, label = stringResource(Res.string.avatar_take_selfie), enabled = !isUploading, onClick = onCamera)
            AvatarSheetOption(icon = Icons.Rounded.PhotoLibrary, label = stringResource(Res.string.avatar_choose_gallery), enabled = !isUploading, onClick = onGallery)
            if (hasAvatar) {
                AvatarSheetOption(icon = Icons.Rounded.DeleteOutline, label = stringResource(Res.string.avatar_delete_photo), enabled = !isUploading, color = RelaxError, onClick = onDelete)
            }

            if (isUploading) {
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Text(stringResource(Res.string.avatar_uploading), style = MaterialTheme.typography.bodyMedium, color = RelaxTextSecondary)
                }
            }
        }
    }
}

@Composable
private fun AvatarSheetOption(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    color: Color = RelaxTextPrimary,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(RelaxSurfaceAlt), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.titleSmall, color = color, fontSize = 15.sp)
    }
}
