package ir.danialchoopan.lumalogic.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.Position

/**
 * GameCanvas component rendering the interactive LumaLogic puzzle grid, cells, and light beam animations.
 */
@Composable
fun GameCanvas(
    rows: Int,
    columns: Int,
    cells: List<Cell>,
    beamPath: List<Position>,
    activatedTargets: Set<Position>,
    onCellClick: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    val cellMap = remember(cells) {
        cells.associateBy { Position(it.row, it.column) }
    }

    // Grid entrance fade-in animation
    val gridAlpha = remember { Animatable(0f) }
    LaunchedEffect(rows, columns) {
        gridAlpha.snapTo(0f)
        gridAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
    }

    // Light beam travel animation (1000 ms duration)
    val beamAnimatable = remember { Animatable(0f) }
    LaunchedEffect(beamPath) {
        if (beamPath.isNotEmpty()) {
            beamAnimatable.snapTo(0f)
            beamAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
            )
        } else {
            beamAnimatable.snapTo(0f)
        }
    }

    // Target activated pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "target_pulse")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "target_pulse_float"
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()

        val padding = 24f
        val availWidth = (width - padding * 2f).coerceAtLeast(100f)
        val availHeight = (height - padding * 2f).coerceAtLeast(100f)

        val maxCellWidth = availWidth / columns.coerceAtLeast(1)
        val maxCellHeight = availHeight / rows.coerceAtLeast(1)
        val cellSize = minOf(maxCellWidth, maxCellHeight)

        val gridWidth = cellSize * columns
        val gridHeight = cellSize * rows

        val originX = (width - gridWidth) / 2f
        val originY = (height - gridHeight) / 2f
        val gridOrigin = Offset(originX, originY)

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("game_grid_canvas")
                .pointerInput(rows, columns, gridOrigin, cellSize) {
                    detectTapGestures { tapOffset ->
                        val localX = tapOffset.x - gridOrigin.x
                        val localY = tapOffset.y - gridOrigin.y

                        if (localX >= 0 && localX < gridWidth && localY >= 0 && localY < gridHeight) {
                            val col = (localX / cellSize).toInt().coerceIn(0, columns - 1)
                            val row = (localY / cellSize).toInt().coerceIn(0, rows - 1)
                            onCellClick(Position(row, col))
                        }
                    }
                }
        ) {
            val alpha = gridAlpha.value
            if (alpha <= 0f) return@Canvas

            // 1. Draw outer grid container boundary
            drawRoundRect(
                color = GameColors.GridGray.copy(alpha = 0.6f * alpha),
                topLeft = gridOrigin,
                size = Size(gridWidth, gridHeight),
                cornerRadius = CornerRadius(16f, 16f)
            )

            drawRoundRect(
                color = GameColors.GridBorder.copy(alpha = alpha),
                topLeft = gridOrigin,
                size = Size(gridWidth, gridHeight),
                cornerRadius = CornerRadius(16f, 16f),
                style = Stroke(width = 3f)
            )

            // 2. Render individual cells
            for (r in 0 until rows) {
                for (c in 0 until columns) {
                    val pos = Position(r, c)
                    val cell = cellMap[pos] ?: Cell(
                        id = "empty_${r}_${c}",
                        row = r,
                        column = c,
                        type = ir.danialchoopan.lumalogic.data.model.CellType.EMPTY
                    )

                    val topLeft = Offset(
                        x = gridOrigin.x + c * cellSize,
                        y = gridOrigin.y + r * cellSize
                    )

                    val isTargetActivated = activatedTargets.contains(pos)

                    GameCellRenderer.drawCell(
                        drawScope = this,
                        cell = cell,
                        topLeft = topLeft,
                        cellSize = cellSize,
                        targetPulseProgress = pulseProgress,
                        isActivated = isTargetActivated
                    )
                }
            }

            // 3. Render animated light beam on top of cells
            LightBeam.drawAnimatedBeam(
                drawScope = this,
                path = beamPath,
                origin = gridOrigin,
                cellSize = cellSize,
                progress = beamAnimatable.value,
                beamColor = GameColors.LaserYellow
            )
        }
    }
}
