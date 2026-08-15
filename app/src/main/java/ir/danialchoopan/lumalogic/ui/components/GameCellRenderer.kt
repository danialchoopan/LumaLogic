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
import ir.danialchoopan.lumalogic.data.model.GateType
import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.ui.components.GameColors.toComposeColor

/**
 * GameCellRenderer responsible for drawing individual cells onto Jetpack Compose Canvas DrawScope.
 */
object GameCellRenderer {

    private fun isCellMovable(cell: Cell): Boolean {
        if (cell.isLocked) return false
        return when (cell.type) {
            CellType.MIRROR, CellType.WIRE, CellType.SPLITTER, CellType.FILTER -> true
            else -> false
        }
    }

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
        val movable = isCellMovable(cell)

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

            // Cell border: highlight movable components with distinct accent border
            val borderTint = cell.lightColor.toComposeColor()
            val borderColor = when {
                cell.isLit -> borderTint.copy(alpha = 0.6f)
                movable -> Color(0xFF00E5FF).copy(alpha = 0.45f) // Cyan accent for movable
                cell.isLocked && cell.type != CellType.EMPTY && cell.type != CellType.BLOCK -> Color(0xFFFF5252).copy(alpha = 0.35f)
                else -> GameColors.GridBorder
            }
            val borderWidth = if (cell.isLit || movable) 2.5f else 1.5f

            drawRoundRect(
                color = borderColor,
                topLeft = Offset(topLeft.x + padding, topLeft.y + padding),
                size = Size(tileSize, tileSize),
                cornerRadius = cornerRadius,
                style = Stroke(width = borderWidth)
            )

            // Draw specific component contents with rotation transform
            withTransform({
                rotate(cell.rotation.degrees, center)
            }) {
                when (cell.type) {
                    CellType.EMPTY -> drawEmptyTile(this, center, cellSize, cell.isLit)
                    CellType.SOURCE -> drawSourceTile(this, center, cellSize, cell.isLit, cell.lightColor)
                    CellType.TARGET -> drawTargetTile(this, center, cellSize, isActivated, targetPulseProgress, cell.requiredColor)
                    CellType.MIRROR -> drawMirrorTile(this, center, cellSize, cell.isLit)
                    CellType.BLOCK -> drawBlockTile(this, topLeft, padding, tileSize, cornerRadius)
                    CellType.WIRE -> drawWireTile(this, center, cellSize, cell.isLit)
                    CellType.SPLITTER -> drawSplitterTile(this, center, cellSize, cell.isLit)
                    CellType.FILTER -> drawFilterTile(this, center, cellSize, cell.isLit, cell.acceptedColor)
                    CellType.GATE -> drawGateTile(this, center, cellSize, cell.isLit, cell.gateType, cell.lightColor)
                }
            }

            // Draw movable or locked indicator in corner
            if (movable) {
                drawMovableIndicator(this, topLeft, padding, tileSize, cellSize)
            } else if (cell.isLocked && cell.type != CellType.EMPTY && cell.type != CellType.BLOCK && cell.type != CellType.SOURCE && cell.type != CellType.TARGET) {
                drawLockedIndicator(this, topLeft, padding, tileSize, cellSize)
            }
        }
    }

    private fun drawMovableIndicator(
        drawScope: DrawScope,
        topLeft: Offset,
        padding: Float,
        tileSize: Float,
        cellSize: Float
    ) {
        val badgeRadius = cellSize * 0.08f
        val badgeCenter = Offset(topLeft.x + padding + tileSize - badgeRadius - 2f, topLeft.y + padding + badgeRadius + 2f)

        with(drawScope) {
            // Cyan badge glow and background
            drawCircle(
                color = Color(0xFF00E5FF).copy(alpha = 0.25f),
                radius = badgeRadius * 1.3f,
                center = badgeCenter
            )
            drawCircle(
                color = Color(0xFF00838F),
                radius = badgeRadius,
                center = badgeCenter
            )
            drawCircle(
                color = Color(0xFF00E5FF),
                radius = badgeRadius,
                center = badgeCenter,
                style = Stroke(width = 1.5f)
            )
            // 4-way move cross / diamond dot
            val markSize = badgeRadius * 0.45f
            drawLine(
                color = Color.White,
                start = Offset(badgeCenter.x - markSize, badgeCenter.y),
                end = Offset(badgeCenter.x + markSize, badgeCenter.y),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White,
                start = Offset(badgeCenter.x, badgeCenter.y - markSize),
                end = Offset(badgeCenter.x, badgeCenter.y + markSize),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }
    }

    private fun drawLockedIndicator(
        drawScope: DrawScope,
        topLeft: Offset,
        padding: Float,
        tileSize: Float,
        cellSize: Float
    ) {
        val badgeRadius = cellSize * 0.08f
        val badgeCenter = Offset(topLeft.x + padding + tileSize - badgeRadius - 2f, topLeft.y + padding + badgeRadius + 2f)

        with(drawScope) {
            // Red padlock badge
            drawCircle(
                color = Color(0xFFFF5252).copy(alpha = 0.2f),
                radius = badgeRadius * 1.2f,
                center = badgeCenter
            )
            drawCircle(
                color = Color(0xFF4A1010),
                radius = badgeRadius,
                center = badgeCenter
            )
            drawCircle(
                color = Color(0xFFFF5252),
                radius = badgeRadius,
                center = badgeCenter,
                style = Stroke(width = 1.5f)
            )
            // Padlock dot/shackle
            drawCircle(
                color = Color(0xFFFF8A80),
                radius = badgeRadius * 0.35f,
                center = badgeCenter
            )
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

    private fun drawSourceTile(
        drawScope: DrawScope,
        center: Offset,
        cellSize: Float,
        isLit: Boolean,
        lightColor: LightColor?
    ) {
        val radius = cellSize * 0.28f
        val colorTint = lightColor.toComposeColor()

        with(drawScope) {
            // Outer glow ring
            drawCircle(
                color = colorTint.copy(alpha = 0.35f),
                radius = radius * 1.3f,
                center = center
            )
            // Core source circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, colorTint),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )
            // Emission nozzle pointing UP (before rotation)
            val nozzlePath = Path().apply {
                moveTo(center.x - radius * 0.4f, center.y - radius * 0.5f)
                lineTo(center.x + radius * 0.4f, center.y - radius * 0.5f)
                lineTo(center.x, center.y - radius * 1.1f)
                close()
            }
            drawPath(
                path = nozzlePath,
                color = colorTint
            )
        }
    }

    private fun drawTargetTile(
        drawScope: DrawScope,
        center: Offset,
        cellSize: Float,
        isActivated: Boolean,
        pulseProgress: Float,
        requiredColor: LightColor?
    ) {
        val baseRadius = cellSize * 0.32f
        val pulseScale = if (isActivated) 1f + (pulseProgress * 0.15f) else 1f
        val baseColor = requiredColor.toComposeColor()
        val targetColor = if (isActivated) GameColors.TargetGreen else baseColor.copy(alpha = 0.85f)

        with(drawScope) {
            if (isActivated) {
                // Halo ring
                drawCircle(
                    color = GameColors.TargetGreen.copy(alpha = 0.35f * (1f - pulseProgress)),
                    radius = baseRadius * (1.2f + pulseProgress * 0.5f),
                    center = center
                )
            }

            // Outer ring
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

            // Target core
            drawCircle(
                color = if (isActivated) Color.White else targetColor,
                radius = baseRadius * 0.25f * pulseScale,
                center = center
            )
        }
    }

    private fun drawMirrorTile(drawScope: DrawScope, center: Offset, cellSize: Float, isLit: Boolean) {
        val mirrorLength = cellSize * 0.65f
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
            drawRoundRect(
                color = GameColors.BlockDark,
                topLeft = Offset(topLeft.x + padding, topLeft.y + padding),
                size = Size(tileSize, tileSize),
                cornerRadius = cornerRadius
            )
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
        val r = cellSize * 0.28f
        val glowColor = Color(0xFF00E5FF)

        with(drawScope) {
            // Splitter central prism
            drawCircle(
                color = glowColor.copy(alpha = if (isLit) 0.35f else 0.15f),
                radius = r * 1.2f,
                center = center
            )
            drawCircle(
                color = glowColor,
                radius = r,
                center = center,
                style = Stroke(width = 3f)
            )

            // Two glowing output indicators (arrows UP and DOWN before rotation)
            val arrowUp = Path().apply {
                moveTo(center.x - r * 0.4f, center.y - r * 0.7f)
                lineTo(center.x + r * 0.4f, center.y - r * 0.7f)
                lineTo(center.x, center.y - r * 1.3f)
                close()
            }
            val arrowDown = Path().apply {
                moveTo(center.x - r * 0.4f, center.y + r * 0.7f)
                lineTo(center.x + r * 0.4f, center.y + r * 0.7f)
                lineTo(center.x, center.y + r * 1.3f)
                close()
            }

            drawPath(path = arrowUp, color = glowColor)
            drawPath(path = arrowDown, color = glowColor)
        }
    }

    private fun drawFilterTile(
        drawScope: DrawScope,
        center: Offset,
        cellSize: Float,
        isLit: Boolean,
        acceptedColor: LightColor?
    ) {
        val filterColor = acceptedColor.toComposeColor()
        val width = cellSize * 0.55f
        val height = cellSize * 0.20f

        with(drawScope) {
            // Glass filter plate centered
            val rectTopLeft = Offset(center.x - width / 2f, center.y - height / 2f)
            val rectSize = Size(width, height)

            drawRoundRect(
                color = filterColor.copy(alpha = if (isLit) 0.6f else 0.35f),
                topLeft = rectTopLeft,
                size = rectSize,
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawRoundRect(
                color = filterColor,
                topLeft = rectTopLeft,
                size = rectSize,
                cornerRadius = CornerRadius(8f, 8f),
                style = Stroke(width = 3.5f)
            )

            // Glass sheen reflection
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = Offset(center.x - width * 0.3f, center.y - height * 0.2f),
                end = Offset(center.x + width * 0.3f, center.y - height * 0.2f),
                strokeWidth = 2f
            )
        }
    }

    private fun drawGateTile(
        drawScope: DrawScope,
        center: Offset,
        cellSize: Float,
        isLit: Boolean,
        gateType: GateType?,
        lightColor: LightColor?
    ) {
        val gType = gateType ?: GateType.AND
        val accentColor = lightColor.toComposeColor()
        val r = cellSize * 0.28f

        with(drawScope) {
            // Gate housing shape
            drawCircle(
                color = accentColor.copy(alpha = if (isLit) 0.35f else 0.15f),
                radius = r * 1.2f,
                center = center
            )
            drawCircle(
                color = accentColor,
                radius = r,
                center = center,
                style = Stroke(width = 3.5f)
            )

            // Input ports visualization
            when (gType) {
                GateType.AND, GateType.OR -> {
                    // Two input ports (LEFT and RIGHT before rotation)
                    drawCircle(color = accentColor, radius = r * 0.25f, center = Offset(center.x - r * 1.2f, center.y))
                    drawCircle(color = accentColor, radius = r * 0.25f, center = Offset(center.x + r * 1.2f, center.y))
                }
                GateType.NOT -> {
                    // One input port (BOTTOM before rotation)
                    drawCircle(color = accentColor, radius = r * 0.25f, center = Offset(center.x, center.y + r * 1.2f))
                }
            }

            // Output port (TOP before rotation)
            drawCircle(color = Color.White, radius = r * 0.3f, center = Offset(center.x, center.y - r * 1.2f))
        }
    }
}
