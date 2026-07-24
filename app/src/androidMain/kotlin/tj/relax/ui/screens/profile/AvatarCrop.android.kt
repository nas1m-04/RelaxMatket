package tj.relax.ui.screens.profile

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

actual fun cropAndEncodeAvatar(source: ImageBitmap, cropRect: Rect, outputSize: Int): ByteArray {
    val androidBitmap = source.asAndroidBitmap()
    val cropped = Bitmap.createBitmap(
        androidBitmap,
        cropRect.left.toInt(),
        cropRect.top.toInt(),
        cropRect.width.toInt(),
        cropRect.height.toInt(),
    )
    val scaled = Bitmap.createScaledBitmap(cropped, outputSize, outputSize, true)
    val stream = ByteArrayOutputStream()
    scaled.compress(Bitmap.CompressFormat.JPEG, 90, stream)
    return stream.toByteArray()
}
