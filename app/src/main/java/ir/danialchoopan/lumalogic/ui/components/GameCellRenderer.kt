package ir.danialchoopan.lumalogic.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType

/**
 * GameCellRenderer responsible for drawing individual cells onto Jetpack Compose Canvas DrawScope.
 */
object GameCellRenderer {

    fun drawCell(
        drawScope: DrawScope,
        cell: Cell,
        topLeft: Offset,
        cellSize: Float,
        targetPulseProgress: Float = 0f,
        isActivated: Boolean = false
    ) {
        val center = Offset(topLeft.x + cellSize / 2f, topLeft.y + cellSize / 2f)
        val padding = cellSize * 0.05f
        val tileSize = cellSize - (padding * 2f)
        val cornerRadius = CornerRadius(cellSize * 0.15f, cellSize * 0.15f)

        with(drawScope) {
            // Draw background tile container
            val tileColor = if (cell.isLit) {
                GameColors.CellDark.copy(alpha = 0.9f)
            } else {
                GameColors.CellDark
            }

            drawRoundRect(
                color = tileColor,
                topLeft = Offset(topLeft.x + padding, topLeft.y + padding),
                size = Size(tileSize, tileSize),
                cornerRadius = cornerRadius
            )

            // Cell border
            drawRoundRect(
                color = if (cell.isLit) GameColors.LaserYellow.copy(alpha = 0.4f) else GameColors.GridBorder,
                topLeft = Offset(topLeft.x + padding, topLeft.y + padding),
                size = Size(tileSize, tileSize),
                cornerRadius = cornerRadius,
                style = Stroke(width = if (cell.isLit) 3f else 1.5f)
            )

            // Draw specific component contents with rotation transform if needed
            withTransform({
                rotate(cell.rotation.degrees, center)
            }) {
                when (cell.type) {
                    CellType.EMPTY -> drawEmptyTile(this, center, cellSize, cell.isLit)
                    CellType.SOURCE -> drawSourceTile(this, center, cellSize, cell.isLit)
                    CellType.TARGET -> drawTargetTile(this, center, cellSize, isActivated, targetPulseProgress)
                    CellType.MIRROR -> drawMirrorTile(this, center, cellSize, cell.isLit)
                    CellType.BLOCK -> drawBlockTile(this, topLeft, padding, tileSize, cornerRadius)
                    CellType.WIRE -> drawWireTile(this, center, cellSize, cell.isLit)
                    CellType.SPLITTER -> drawSplitterTile(this, center, cellSize, cell.isLit)
                    CellType.FILTER -> drawFilterTile(this, center, cellSize, cell.isLit)
                    CellType.GATE -> drawGateTile(this, center, cellSize, cell.isLit)
                }
            }
        }
    }

    private fun drawEmptyTile(drawScope: DrawScope, center: Offset, cellSize: Float, isLit: Boolean) {
        if (isLit) {
            drawScope.drawCircle(
                color = GameColors.LaserYellow.copy(alpha = 0.25f),
                radius = cellSize * 0.12f,
                center = center
            )
        }
    }

    private fun drawSourceTile(drawScope: DrawScope, center: Offset, cellSize: Float, isLit: Boolean) {
        val radius = cellSize * 0.28f
        with(drawScope) {
            // Outer glow ring
            drawCircle(
                color = GameColors.LaserYellow.copy(alpha = 0.3f),
                radius = radius * 1.3f,
                center = center
            )
            // Core source circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, GameColors.LaserYellow),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )
            // Direction emission nozzle pointing UP (before rotation)
            val nozzlePath = Path().apply {
                moveTo(center.x - radius * 0.4f, center.y - radius * 0.5f)
                lineTo(center.x + radius * 0.4f, center.y - radius * 0.5f)
                lineTo(center.x, center.y - radius * 1.1f)
                close()
            }
            drawPath(
                path = nozzlePath,
                color = GameColors.LaserYellow
            )
        }
    }

    private fun drawTargetTile(
        drawScope: DrawScope,
        center: Offset,
        cellSize: Float,
        isActivated: Boolean,
        pulseProgress: Float
    ) {
        val baseRadius = cellSize * 0.32f
        val pulseScale = if (isActivated) 1f + (pulseProgress * 0.15f) else 1f
        val targetColor = if (isActivated) GameColors.TargetGreen else GameColors.TargetGreen.copy(alpha = 0.7f)

        with(drawScope) {
            if (isActivated) {
                // Pulse halo ring
                drawCircle(
                    color = GameColors.TargetGreen.copy(alpha = 0.3f * (1f - pulseProgress)),
                    radius = baseRadius * (1.2f + pulseProgress * 0.5f),
                    center = center
                )
            }

            // Outer target ring
            drawCircle(
                color = targetColor.copy(alpha = 0.25f),
                radius = baseRadius * pulseScale,
                center = center
            )
            drawCircle(
                color = targetColor,
                radius = baseRadius * pulseScale,
                center = center,
                style = Stroke(width = 4f)
            )

            // Inner target ring
            drawCircle(
                color = targetColor,
                radius = baseRadius * 0.55f * pulseScale,
                center = center,
                style = Stroke(width = 3f)
            )

            // Target bullseye core
            drawCircle(
                color = if (isActivated) Color.White else targetColor,
                radius = baseRadius * 0.25f * pulseScale,
                center = center
            )
        }
    }

    private fun drawMirrorTile(drawScope: DrawScope, center: Offset, cellSize: Float, isLit: Boolean) {
        val mirrorLength = cellSize * 0.65f
        // Default Rotation.ZERO represents forward mirror / (from bottom-left to top-right in standard math,
        // or top-right to bottom-left).
        val start = Offset(center.x - mirrorLength / 2f, center.y + mirrorLength / 2f)
        val end = Offset(center.x + mirrorLength / 2f, center.y - mirrorLength / 2f)

        with(drawScope) {
            // Mirror backing glow
            drawLine(
                color = GameColors.MirrorBlue.copy(alpha = if (isLit) 0.6f else 0.3f),
                start = start,
                end = end,
                strokeWidth = 12f,
                cap = StrokeCap.Round
            )
            // Glass mirror surface
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.White, GameColors.MirrorBlue, Color.White),
                    start = start,
                    end = end
                ),
                start = start,
                end = end,
                strokeWidth = 6f,
                cap = StrokeCap.Round
            )
        }
    }

    private fun drawBlockTile(
        drawScope: DrawScope,
        topLeft: Offset,
        padding: Float,
        tileSize: Float,
        cornerRadius: CornerRadius
    ) {
        with(drawScope) {
            // Dark solid obstacle
            drawRoundRect(
                color = GameColors.BlockDark,
                topLeft = Offset(topLeft.x + padding, topLeft.y + padding),
                size = Size(tileSize, tileSize),
                cornerRadius = cornerRadius
            )
            // Diagonal texture lines
            val lineCount = 3
            val step = tileSize / (lineCount + 1)
            for (i in 1..lineCount) {
                drawLine(
                    color = Color.White.copy(alpha = 0.08f),
                    start = Offset(topLeft.x + padding + (i * step), topLeft.y + padding),
                    end = Offset(topLeft.x + padding, topLeft.y + padding + (i * step)),
                    strokeWidth = 3f
                )
            }
        }
    }

    private fun drawWireTile(drawScope: DrawScope, center: Offset, cellSize: Float, isLit: Boolean) {
        val wireWidth = cellSize * 0.15f
        with(drawScope) {
            drawLine(
                color = if (isLit) GameColors.LaserYellow else GameColors.WireGray,
                start = Offset(center.x - cellSize * 0.35f, center.y),
                end = Offset(center.x + cellSize * 0.35f, center.y),
                strokeWidth = wireWidth,
                cap = StrokeCap.Round
            )
        }
    }

    private fun drawSplitterTile(drawScope: DrawScope, center: Offset, cellSize: Float, isLit: Boolean) {
        val r = cellSize * 0.25f
        with(drawScope) {
            drawCircle(
                color = GameColors.BeamGlow.copy(alpha = 0.4f),
                radius = r,
                center = center
            )
            drawCircle(
                color = Color.Cyan,
                radius = r,
                center = center,
                style = Stroke(width = 3f)
            )
        }
    }

    private fun drawFilterTile(drawScope: DrawScope, center: Offset, cellSize: Float, isLit: Boolean) {
        val r = cellSize * 0.22f
        with(drawScope) {
            drawCircle(
                color = Color(0xFF9C27B0),
                radius = r,
                center = center,
                style = Stroke(width = 4f)
            )
        }
    }

    private fun drawGateTile(drawScope: DrawScope, center: Offset, cellSize: Float, isLit: Boolean) {
        val r = cellSize * 0.22f
        with(drawScope) {
            drawCircle(
                color = Color(0xFFFF9800),
                radius = r,
                center = center,
                style = Stroke(width = 4f)
            )
        }
    }
}
