package tj.relax.ui.screens.profile

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.Bitmap as SkiaBitmap
import org.jetbrains.skia.Canvas as SkiaCanvas
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image as SkiaImage
import org.jetbrains.skia.Rect as SkiaRect

actual fun cropAndEncodeAvatar(source: ImageBitmap, cropRect: Rect, outputSize: Int): ByteArray {
    val sourceImage = SkiaImage.makeFromBitmap(source.asSkiaBitmap())

    val outBitmap = SkiaBitmap()
    outBitmap.allocN32Pixels(outputSize, outputSize)
    SkiaCanvas(outBitmap).drawImageRect(
        image = sourceImage,
        src = SkiaRect.makeLTRB(cropRect.left, cropRect.top, cropRect.right, cropRect.bottom),
        dst = SkiaRect.makeWH(outputSize.toFloat(), outputSize.toFloat()),
    )

    val data = SkiaImage.makeFromBitmap(outBitmap).encodeToData(EncodedImageFormat.JPEG, 90)
        ?: error("Failed to encode avatar JPEG")
    return data.bytes
}
