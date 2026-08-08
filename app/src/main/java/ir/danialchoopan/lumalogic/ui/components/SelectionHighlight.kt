package ir.danialchoopan.lumalogic.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import ir.danialchoopan.lumalogic.data.model.Position

/**
 * Utility renderer for drawing cell selection highlights and drag target indicators on Canvas.
 */
object SelectionHighlight {

    fun drawSelection(
        drawScope: DrawScope,
        selectedPosition: Position?,
        dragHoverPosition: Position?,
        gridOrigin: Offset,
        cellSize: Float,
        pulseScale: Float = 1f
    ) {
        with(drawScope) {
            // Draw Selected Cell Highlight (Glowing outline)
            selectedPosition?.let { pos ->
                val topLeft = Offset(
                    x = gridOrigin.x + pos.column * cellSize,
                    y = gridOrigin.y + pos.row * cellSize
                )
                val padding = cellSize * 0.04f
                val size = cellSize - (padding * 2f)

                // Outer glow
                drawRoundRect(
                    color = GameColors.LaserYellow.copy(alpha = 0.25f * pulseScale),
                    topLeft = Offset(topLeft.x + padding, topLeft.y + padding),
                    size = Size(size, size),
                    cornerRadius = CornerRadius(cellSize * 0.15f, cellSize * 0.15f)
                )

                // Selection Border
                drawRoundRect(
                    color = GameColors.LaserYellow,
                    topLeft = Offset(topLeft.x + padding, topLeft.y + padding),
                    size = Size(size, size),
                    cornerRadius = CornerRadius(cellSize * 0.15f, cellSize * 0.15f),
                    style = Stroke(width = 4f * pulseScale)
                )
            }

            // Draw Drag Hover Target Highlight (Dashed Green target feedback)
            dragHoverPosition?.let { pos ->
                if (pos != selectedPosition) {
                    val topLeft = Offset(
                        x = gridOrigin.x + pos.column * cellSize,
                        y = gridOrigin.y + pos.row * cellSize
                    )
                    val padding = cellSize * 0.04f
                    val size = cellSize - (padding * 2f)

                    drawRoundRect(
                        color = GameColors.TargetGreen.copy(alpha = 0.2f),
                        topLeft = Offset(topLeft.x + padding, topLeft.y + padding),
                        size = Size(size, size),
                        cornerRadius = CornerRadius(cellSize * 0.15f, cellSize * 0.15f)
                    )

                    drawRoundRect(
                        color = GameColors.TargetGreen,
                        topLeft = Offset(topLeft.x + padding, topLeft.y + padding),
                        size = Size(size, size),
                        cornerRadius = CornerRadius(cellSize * 0.15f, cellSize * 0.15f),
                        style = Stroke(
                            width = 3.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    )
                }
            }
        }
    }
}
