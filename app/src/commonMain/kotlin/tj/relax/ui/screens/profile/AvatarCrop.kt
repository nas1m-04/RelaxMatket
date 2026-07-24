package tj.relax.ui.screens.profile

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import kotlin.math.roundToInt

/** Crops [source] to the square region currently framed by the pan/zoom crop box, scales it to
 * [outputSize] x [outputSize] and encodes it as a JPEG. Native pixel manipulation (Bitmap on
 * Android, Skia on iOS) has no portable API, so this one step stays expect/actual — everything
 * else about the crop UI (gestures, the crop-rect math below) is common code. */
expect fun cropAndEncodeAvatar(source: ImageBitmap, cropRect: Rect, outputSize: Int): ByteArray

/** Converts the crop box's current pan/zoom state into a source-image-pixel-space rectangle. */
internal fun computeCropRect(
    source: ImageBitmap,
    boxSizePx: Float,
    baseScale: Float,
    zoom: Float,
    offset: Offset,
): Rect {
    val displayScale = baseScale * zoom
    val srcLeft = (-offset.x / displayScale).roundToInt().coerceIn(0, source.width - 1)
    val srcTop = (-offset.y / displayScale).roundToInt().coerceIn(0, source.height - 1)
    val rawSize = (boxSizePx / displayScale).roundToInt().coerceAtLeast(1)
    val srcSize = minOf(rawSize, source.width - srcLeft, source.height - srcTop)
    return Rect(srcLeft.toFloat(), srcTop.toFloat(), (srcLeft + srcSize).toFloat(), (srcTop + srcSize).toFloat())
}
