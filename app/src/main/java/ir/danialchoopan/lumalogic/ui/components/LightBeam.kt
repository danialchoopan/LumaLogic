package ir.danialchoopan.lumalogic.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.domain.model.BeamSegment
import ir.danialchoopan.lumalogic.ui.components.GameColors.toComposeColor
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
     * Draws multi-layered neon beam segments with color and animation progress.
     */
    fun drawAnimatedSegments(
        drawScope: DrawScope,
        segments: List<BeamSegment>,
        origin: Offset,
        cellSize: Float,
        progress: Float
    ) {
        if (segments.isEmpty() || progress <= 0f) return

        val segmentCount = segments.size
        val visibleCount = (segmentCount * progress).toInt().coerceAtLeast(1).coerceAtMost(segmentCount)

        with(drawScope) {
            for (i in 0 until visibleCount) {
                val seg = segments[i]
                val start = positionToCenter(seg.start, origin, cellSize)
                val end = positionToCenter(seg.end, origin, cellSize)
                val beamColor = seg.color.toComposeColor()

                // Layer 1: Outer glow
                drawLine(
                    color = beamColor.copy(alpha = 0.25f),
                    start = start,
                    end = end,
                    strokeWidth = cellSize * 0.35f,
                    cap = StrokeCap.Round
                )
                // Layer 2: Soft glow
                drawLine(
                    color = beamColor.copy(alpha = 0.50f),
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

                // Tip particle
                if (i == visibleCount - 1) {
                    drawCircle(
                        color = Color.White,
                        radius = cellSize * 0.1f,
                        center = end
                    )
                    drawCircle(
                        color = beamColor,
                        radius = cellSize * 0.18f,
                        center = end
                    )
                }
            }
        }
    }

    /**
     * Legacy single-path fallback for backward compatibility.
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

        val segments = mutableListOf<BeamSegment>()
        for (i in 0 until path.size - 1) {
            segments.add(
                BeamSegment(
                    start = path[i],
                    end = path[i + 1]
                )
            )
        }
        drawAnimatedSegments(drawScope, segments, origin, cellSize, progress)
    }
}
