package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import kotlin.math.sin

@Composable
fun VisualizerView(
    amplitudes: List<Float>,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    height: Dp = 90.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val gradient = Brush.linearGradient(
        colors = listOf(NeonCyan, NeonMagenta, NeonAmber)
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val width = size.width
        val canvasHeight = size.height
        val centerY = canvasHeight / 2f
        val barCount = amplitudes.size.coerceAtLeast(1)
        val barSpacing = width / barCount
        val barWidth = (barSpacing * 0.65f).coerceAtLeast(3f)

        // Draw animated background equalizer bars
        for (i in 0 until barCount) {
            val amp = amplitudes.getOrElse(i) { 0.1f }
            val barH = (canvasHeight * 0.85f * amp).coerceIn(4f, canvasHeight * 0.95f)
            val x = i * barSpacing + (barSpacing - barWidth) / 2f
            val y = centerY - (barH / 2f)

            drawRoundRect(
                brush = gradient,
                topLeft = Offset(x, y),
                size = Size(barWidth, barH),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f),
                alpha = if (isPlaying) 0.85f else 0.35f
            )
        }

        // Draw flowing neon soundwave ribbon overlay
        if (isPlaying) {
            val wavePath = Path()
            val points = 60
            for (p in 0..points) {
                val px = (p.toFloat() / points) * width
                val waveOffset = sin((p.toFloat() / points * 4 * Math.PI + phase).toFloat()) * (canvasHeight * 0.35f)
                val py = centerY + waveOffset
                if (p == 0) wavePath.moveTo(px, py) else wavePath.lineTo(px, py)
            }
            drawPath(
                path = wavePath,
                color = Color.White.copy(alpha = 0.6f),
                style = Stroke(width = 2.5.dp.toPx())
            )
        }
    }
}
