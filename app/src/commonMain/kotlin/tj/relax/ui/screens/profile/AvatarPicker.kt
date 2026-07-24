package tj.relax.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Camera/gallery are wired very differently per platform: Android launches the system picker/
 * camera app via an Activity contract and returns here; iOS's picker library instead needs a
 * full-screen composable mounted in-tree while the camera is active, hence [overlay] — Android's
 * actual leaves it empty since it has nothing to render in-app.
 */
class AvatarPickerLaunchers(
    val pickFromGallery: () -> Unit,
    val pickFromCamera: () -> Unit,
    val overlay: @Composable () -> Unit = {},
)

@Composable
expect fun rememberAvatarPickerLaunchers(onPicked: (ImageBitmap) -> Unit): AvatarPickerLaunchers
