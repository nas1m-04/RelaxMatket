package tj.relax.ui.screens.profile

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import tj.relax.R
import tj.relax.ui.theme.RelaxTextOnDarkSub
import tj.relax.ui.theme.RelaxWhite
import java.io.ByteArrayOutputStream
import kotlin.math.min
import kotlin.math.roundToInt

private val CropBoxSize = 280.dp
private const val MaxZoom = 4f
private const val OutputSize = 512

/**
 * Full-screen circular crop step shown after picking/taking a photo, before it's uploaded — lets
 * the user pan/pinch-zoom to choose exactly which part of the photo becomes their avatar, instead
 * of uploading whatever the picker/camera happened to frame.
 */
@Composable
fun AvatarCropDialog(
    bitmap: Bitmap,
    onConfirm: (ByteArray) -> Unit,
    onDismiss: () -> Unit,
) {
    val density = LocalDensity.current
    val boxSizePx = with(density) { CropBoxSize.toPx() }
    val baseScale = remember(bitmap, boxSizePx) {
        maxOf(boxSizePx / bitmap.width.toFloat(), boxSizePx / bitmap.height.toFloat())
    }

    var zoom by remember(bitmap) { mutableFloatStateOf(1f) }
    var offset by remember(bitmap) {
        val dw = bitmap.width * baseScale
        val dh = bitmap.height * baseScale
        mutableStateOf(Offset((boxSizePx - dw) / 2f, (boxSizePx - dh) / 2f))
    }

    fun clampOffset(currentZoom: Float, candidate: Offset): Offset {
        val displayScale = baseScale * currentZoom
        val dw = bitmap.width * displayScale
        val dh = bitmap.height * displayScale
        val minX = boxSizePx - dw
        val minY = boxSizePx - dh
        return Offset(
            x = candidate.x.coerceIn(min(minX, 0f), 0f),
            y = candidate.y.coerceIn(min(minY, 0f), 0f),
        )
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.avatar_crop_title),
                color = RelaxWhite,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 32.dp, bottom = 4.dp),
            )
            Text(
                text = stringResource(R.string.avatar_crop_hint),
                color = RelaxTextOnDarkSub,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp),
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                val displayScale = baseScale * zoom
                val imageWidthDp = with(density) { (bitmap.width * displayScale).toDp() }
                val imageHeightDp = with(density) { (bitmap.height * displayScale).toDp() }

                Box(
                    modifier = Modifier
                        .size(CropBoxSize)
                        .clipToBounds()
                        .pointerInput(bitmap) {
                            detectTransformGestures { _, pan, gestureZoom, _ ->
                                val newZoom = (zoom * gestureZoom).coerceIn(1f, MaxZoom)
                                offset = clampOffset(newZoom, offset + pan)
                                zoom = newZoom
                            }
                        },
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(imageWidthDp, imageHeightDp)
                            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) },
                    )

                    Canvas(modifier = Modifier.size(CropBoxSize)) {
                        val outer = Path().apply { addRect(Rect(Offset.Zero, Size(size.width, size.height))) }
                        val circleRadius = size.minDimension / 2f
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val circle = Path().apply {
                            addOval(Rect(center = center, radius = circleRadius))
                        }
                        val scrim = Path.combine(PathOperation.Difference, outer, circle)
                        drawPath(scrim, color = Color.Black.copy(alpha = 0.6f))
                        drawCircle(color = RelaxWhite, radius = circleRadius, center = center, style = Stroke(width = 2.dp.toPx()))
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth().padding(24.dp),
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RelaxWhite),
                ) {
                    Text(stringResource(R.string.avatar_crop_cancel))
                }
                Button(
                    onClick = {
                        val cropped = cropToOutput(bitmap, boxSizePx, baseScale, zoom, offset)
                        val stream = ByteArrayOutputStream()
                        cropped.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                        onConfirm(stream.toByteArray())
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.avatar_crop_confirm))
                }
            }
        }
    }
}

private fun cropToOutput(source: Bitmap, boxSizePx: Float, baseScale: Float, zoom: Float, offset: Offset): Bitmap {
    val displayScale = baseScale * zoom
    val srcLeft = (-offset.x / displayScale).roundToInt().coerceIn(0, source.width - 1)
    val srcTop = (-offset.y / displayScale).roundToInt().coerceIn(0, source.height - 1)
    val rawSize = (boxSizePx / displayScale).roundToInt().coerceAtLeast(1)
    val srcSize = minOf(rawSize, source.width - srcLeft, source.height - srcTop)

    val cropped = Bitmap.createBitmap(source, srcLeft, srcTop, srcSize, srcSize)
    return Bitmap.createScaledBitmap(cropped, OutputSize, OutputSize, true)
}
