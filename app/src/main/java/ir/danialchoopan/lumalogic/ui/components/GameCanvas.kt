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
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.domain.model.BeamSegment

/**
 * GameCanvas component rendering the interactive LumaLogic puzzle grid, cells, selection highlights, and light beam animations.
 */
@Composable
fun GameCanvas(
    rows: Int,
    columns: Int,
    cells: List<Cell>,
    beamPath: List<Position>,
    beamSegments: List<BeamSegment> = emptyList(),
    activatedTargets: Set<Position>,
    selectedPosition: Position? = null,
    hintPosition: Position? = null,
    onCellClick: (Position) -> Unit,
    onMoveCell: (Position, Position) -> Unit = { _, _ -> },
    onInvalidMoveAttempt: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cellMap = remember(cells) {
        cells.associateBy { Position(it.row, it.column) }
    }

    var dragFromPosition by remember { mutableStateOf<Position?>(null) }
    var dragHoverPosition by remember { mutableStateOf<Position?>(null) }

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
    LaunchedEffect(beamPath, beamSegments) {
        if (beamPath.isNotEmpty() || beamSegments.isNotEmpty()) {
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
                .pointerInput(rows, columns, gridOrigin, cellSize, cellMap) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { startOffset ->
                            val localX = startOffset.x - gridOrigin.x
                            val localY = startOffset.y - gridOrigin.y

                            if (localX >= 0 && localX < gridWidth && localY >= 0 && localY < gridHeight) {
                                val col = (localX / cellSize).toInt().coerceIn(0, columns - 1)
                                val row = (localY / cellSize).toInt().coerceIn(0, rows - 1)
                                val pos = Position(row, col)
                                val cell = cellMap[pos]

                                if (cell != null && isCellMovable(cell)) {
                                    dragFromPosition = pos
                                    dragHoverPosition = pos
                                } else {
                                    onInvalidMoveAttempt("Sources, Targets, and Blocks cannot be moved")
                                }
                            }
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val dragFrom = dragFromPosition ?: return@detectDragGesturesAfterLongPress
                            val currentOffset = change.position
                            val localX = currentOffset.x - gridOrigin.x
                            val localY = currentOffset.y - gridOrigin.y

                            if (localX >= 0 && localX < gridWidth && localY >= 0 && localY < gridHeight) {
                                val col = (localX / cellSize).toInt().coerceIn(0, columns - 1)
                                val row = (localY / cellSize).toInt().coerceIn(0, rows - 1)
                                dragHoverPosition = Position(row, col)
                            } else {
                                dragHoverPosition = null
                            }
                        },
                        onDragEnd = {
                            val from = dragFromPosition
                            val to = dragHoverPosition

                            if (from != null && to != null && from != to) {
                                onMoveCell(from, to)
                            }
                            dragFromPosition = null
                            dragHoverPosition = null
                        },
                        onDragCancel = {
                            dragFromPosition = null
                            dragHoverPosition = null
                        }
                    )
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
                        type = CellType.EMPTY
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

            // 3. Render selection and drag highlights
            SelectionHighlight.drawSelection(
                drawScope = this,
                selectedPosition = selectedPosition,
                dragHoverPosition = dragHoverPosition,
                hintPosition = hintPosition,
                gridOrigin = gridOrigin,
                cellSize = cellSize,
                pulseScale = 1f + pulseProgress * 0.1f
            )

            // 4. Render animated light beam on top of cells
            if (beamSegments.isNotEmpty()) {
                LightBeam.drawAnimatedSegments(
                    drawScope = this,
                    segments = beamSegments,
                    origin = gridOrigin,
                    cellSize = cellSize,
                    progress = beamAnimatable.value
                )
            } else {
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
}

private fun isCellMovable(cell: Cell): Boolean {
    if (cell.isLocked) return false
    return when (cell.type) {
        CellType.MIRROR, CellType.WIRE, CellType.SPLITTER, CellType.FILTER -> true
        else -> false
    }
}
