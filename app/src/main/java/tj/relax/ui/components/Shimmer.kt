package tj.relax.ui.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val shimmerLight  = Color(0xFFE8E8E8)
private val shimmerDark   = Color(0xFFF5F5F5)

@Composable
fun ShimmerBox(modifier: Modifier, shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp)) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset by transition.animateFloat(
        initialValue = -600f,
        targetValue  = 1200f,
        animationSpec = infiniteRepeatable(
            animation   = tween(1000, easing = FastOutLinearInEasing),
            repeatMode  = RepeatMode.Restart,
        ),
        label = "shimmer_offset",
    )
    val brush = Brush.linearGradient(
        colors = listOf(shimmerLight, shimmerDark, shimmerLight),
        start  = Offset(offset, 0f),
        end    = Offset(offset + 600f, 0f),
    )
    Box(modifier = modifier.clip(shape).background(brush))
}

@Composable
fun HomeScreenSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Banner skeleton
        ShimmerBox(
            modifier = Modifier.fillMaxWidth().height(190.dp),
            shape    = RoundedCornerShape(20.dp),
        )

        // Category icons skeleton
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(5) {
                Column(
                    horizontalAlignment   = Alignment.CenterHorizontally,
                    verticalArrangement   = Arrangement.spacedBy(8.dp),
                ) {
                    ShimmerBox(Modifier.size(56.dp), CircleShape)
                    ShimmerBox(Modifier.width(44.dp).height(9.dp), RoundedCornerShape(4.dp))
                }
            }
        }

        // Section title + horizontal cards
        SkeletonSection()

        // Section title + grid row
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ShimmerBox(Modifier.width(120.dp).height(14.dp), RoundedCornerShape(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ShimmerBox(Modifier.weight(1f).height(240.dp), RoundedCornerShape(18.dp))
                ShimmerBox(Modifier.weight(1f).height(240.dp), RoundedCornerShape(18.dp))
            }
        }

        // Another horizontal section
        SkeletonSection()

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun SkeletonSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ShimmerBox(Modifier.width(120.dp).height(14.dp), RoundedCornerShape(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(3) {
                ShimmerBox(Modifier.width(160.dp).height(210.dp), RoundedCornerShape(20.dp))
            }
        }
    }
}
