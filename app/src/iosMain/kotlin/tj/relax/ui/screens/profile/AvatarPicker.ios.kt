package tj.relax.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState

/**
 * Android launches the system gallery/camera app and returns via an Activity contract. iOS has
 * no equivalent for the camera — peekaboo's [PeekabooCamera] is an in-app full-screen preview
 * composable instead, so it's mounted through [AvatarPickerLaunchers.overlay] rather than
 * "launched" the way the gallery picker (a real PHPickerViewController sheet) is.
 */
@Composable
actual fun rememberAvatarPickerLaunchers(onPicked: (ImageBitmap) -> Unit): AvatarPickerLaunchers {
    val scope = rememberCoroutineScope()
    var showCamera by remember { mutableStateOf(false) }

    val galleryLauncher = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { images -> images.firstOrNull()?.let { onPicked(it.toImageBitmap()) } },
    )

    val cameraState = rememberPeekabooCameraState(
        onCapture = { bytes ->
            showCamera = false
            bytes?.let { onPicked(it.toImageBitmap()) }
        },
    )

    return AvatarPickerLaunchers(
        pickFromGallery = { galleryLauncher.launch() },
        pickFromCamera = { showCamera = true },
        overlay = {
            if (showCamera) {
                Dialog(
                    onDismissRequest = { showCamera = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false),
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                        PeekabooCamera(
                            state = cameraState,
                            modifier = Modifier.fillMaxSize(),
                            permissionDeniedContent = {
                                Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                                    Text("Нет доступа к камере", color = Color.White)
                                }
                            },
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(32.dp)
                                .size(64.dp)
                                .background(Color.White, CircleShape)
                                .border(2.dp, Color.Gray, CircleShape)
                                .clickable { cameraState.capture() },
                        )
                        Text(
                            text = "✕",
                            color = Color.White,
                            modifier = Modifier.align(Alignment.TopEnd).padding(24.dp).clickable { showCamera = false },
                        )
                    }
                }
            }
        },
    )
}
