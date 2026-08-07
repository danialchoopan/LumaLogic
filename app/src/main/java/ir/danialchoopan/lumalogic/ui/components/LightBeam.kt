package ir.danialchoopan.lumalogic.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import ir.danialchoopan.lumalogic.data.model.Position
import kotlin.math.hypot

/**
 * Helper class and renderer for multi-layered neon light beam and animated particle flows.
 */
object LightBeam {

    /**
     * Converts a Grid Position to Canvas Pixel Center Coordinates.
     */
    fun positionToCenter(pos: Position, origin: Offset, cellSize: Float): Offset {
        return Offset(
            x = origin.x + (pos.column + 0.5f) * cellSize,
            y = origin.y + (pos.row + 0.5f) * cellSize
        )
    }

    /**
     * Draws multi-layered neon beam path with glow and particles based on animation progress (0.0f to 1.0f).
     */
    fun drawAnimatedBeam(
        drawScope: DrawScope,
        path: List<Position>,
        origin: Offset,
        cellSize: Float,
        progress: Float,
        beamColor: Color = GameColors.LaserYellow
    ) {
        if (path.size < 2 || progress <= 0f) return

        val centers = path.map { positionToCenter(it, origin, cellSize) }

        // Calculate total path distance
        val segmentLengths = mutableListOf<Float>()
        var totalDistance = 0f
        for (i in 0 until centers.size - 1) {
            val d = distance(centers[i], centers[i + 1])
            segmentLengths.add(d)
            totalDistance += d
        }

        if (totalDistance <= 0f) return

        val currentTargetDistance = totalDistance * progress.coerceIn(0f, 1f)
        var accumulatedDistance = 0f

        val activeSegments = mutableListOf<Pair<Offset, Offset>>()

        for (i in segmentLengths.indices) {
            val segLen = segmentLengths[i]
            val pStart = centers[i]
            val pEnd = centers[i + 1]

            if (accumulatedDistance + segLen <= currentTargetDistance) {
                // Entire segment is covered
                activeSegments.add(Pair(pStart, pEnd))
                accumulatedDistance += segLen
            } else {
                // Partial segment
                val remainingDist = currentTargetDistance - accumulatedDistance
                if (remainingDist > 0f) {
                    val fraction = remainingDist / segLen
                    val partialEnd = Offset(
                        x = pStart.x + (pEnd.x - pStart.x) * fraction,
                        y = pStart.y + (pEnd.y - pStart.y) * fraction
                    )
                    activeSegments.add(Pair(pStart, partialEnd))
                }
                break
            }
        }

        with(drawScope) {
            // Draw multi-layer neon light beam for all active segments
            for ((start, end) in activeSegments) {
                // Layer 1: Outer glow
                drawLine(
                    color = beamColor.copy(alpha = 0.15f),
                    start = start,
                    end = end,
                    strokeWidth = cellSize * 0.35f,
                    cap = StrokeCap.Round
                )
                // Layer 2: Soft glow
                drawLine(
                    color = beamColor.copy(alpha = 0.35f),
                    start = start,
                    end = end,
                    strokeWidth = cellSize * 0.20f,
                    cap = StrokeCap.Round
                )
                // Layer 3: Main core beam
                drawLine(
                    color = Color.White,
                    start = start,
                    end = end,
                    strokeWidth = cellSize * 0.08f,
                    cap = StrokeCap.Round
                )
            }

            // Draw glowing head tip particle at the front of the beam
            if (activeSegments.isNotEmpty()) {
                val tipPos = activeSegments.last().second
                drawCircle(
                    color = Color.White,
                    radius = cellSize * 0.1f,
                    center = tipPos
                )
                drawCircle(
                    color = beamColor,
                    radius = cellSize * 0.18f,
                    center = tipPos
                )
            }

            // Draw flowing particles along the active path segments
            drawParticles(this, activeSegments, cellSize, progress, beamColor)
        }
    }

    private fun drawParticles(
        drawScope: DrawScope,
        segments: List<Pair<Offset, Offset>>,
        cellSize: Float,
        progress: Float,
        beamColor: Color
    ) {
        val particleCount = 6
        var segmentIdx = 0

        for (i in 0 until particleCount) {
            val particleOffset = (progress * 2.5f + i * (1f / particleCount)) % 1f
            if (segments.isEmpty()) break

            // Distribute particles across segments
            val seg = segments[(i + (progress * 10).toInt()) % segments.size]
            val pStart = seg.first
            val pEnd = seg.second

            val pX = pStart.x + (pEnd.x - pStart.x) * particleOffset
            val pY = pStart.y + (pEnd.y - pStart.y) * particleOffset
            val particleCenter = Offset(pX, pY)

            val alpha = (0.3f + 0.7f * kotlin.math.sin(particleOffset * Math.PI).toFloat()).coerceIn(0f, 1f)

            drawScope.drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = cellSize * 0.04f,
                center = particleCenter
            )
            drawScope.drawCircle(
                color = beamColor.copy(alpha = alpha * 0.5f),
                radius = cellSize * 0.08f,
                center = particleCenter
            )
        }
    }

    private fun distance(p1: Offset, p2: Offset): Float {
        return hypot((p2.x - p1.x).toDouble(), (p2.y - p1.y).toDouble()).toFloat()
    }
}
