package ir.danialchoopan.lumalogic.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.ui.theme.BlockDark
import ir.danialchoopan.lumalogic.ui.theme.FilterPurple
import ir.danialchoopan.lumalogic.ui.theme.GateOrange
import ir.danialchoopan.lumalogic.ui.theme.MirrorBlue
import ir.danialchoopan.lumalogic.ui.theme.SourceYellow
import ir.danialchoopan.lumalogic.ui.theme.SplitterCyan
import ir.danialchoopan.lumalogic.ui.theme.TargetGreen
import ir.danialchoopan.lumalogic.ui.theme.WireGray

@Composable
fun CellComposable(
    cell: Cell,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotationDegrees by animateFloatAsState(
        targetValue = cell.rotation.degrees,
        animationSpec = spring(stiffness = 300f),
        label = "cell_rotation"
    )

    val shape = RoundedCornerShape(8.dp)
    val isLockedColor = if (cell.isLocked) Color.White.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.05f)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .testTag("cell_${cell.row}_${cell.column}")
            .border(
                width = 1.dp,
                color = if (cell.isLit) SourceYellow else isLockedColor,
                shape = shape
            )
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = shape
            )
            .clickable(enabled = !cell.isLocked, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (cell.isLit) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = if (cell.type == CellType.TARGET) TargetGreen.copy(alpha = 0.25f) else SourceYellow.copy(alpha = 0.15f),
                        shape = shape
                    )
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .rotate(rotationDegrees)
        ) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f, height / 2f)

            when (cell.type) {
                CellType.EMPTY -> {
                    // Dark empty square
                    drawRoundRect(
                        color = if (cell.isLit) SourceYellow.copy(alpha = 0.3f) else BlockDark.copy(alpha = 0.4f),
                        size = Size(width, height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                    )
                    if (cell.isLit) {
                        drawCircle(
                            color = SourceYellow.copy(alpha = 0.6f),
                            radius = width / 6f,
                            center = center
                        )
                    }
                }

                CellType.SOURCE -> {
                    // Yellow circle (Source)
                    drawCircle(
                        color = SourceYellow.copy(alpha = 0.3f),
                        radius = width / 2f,
                        center = center
                    )
                    drawCircle(
                        color = SourceYellow,
                        radius = width / 3f,
                        center = center
                    )
                    // Light beam emitter nozzle pointing UP
                    drawLine(
                        color = Color.White,
                        start = center,
                        end = Offset(width / 2f, 0f),
                        strokeWidth = 6f
                    )
                }

                CellType.TARGET -> {
                    val targetColor = if (cell.isLit) TargetGreen else TargetGreen.copy(alpha = 0.6f)
                    // Green circle (Target)
                    drawCircle(
                        color = targetColor.copy(alpha = if (cell.isLit) 0.5f else 0.2f),
                        radius = width / 2f,
                        center = center
                    )
                    drawCircle(
                        color = targetColor,
                        radius = width / 3f,
                        center = center,
                        style = Stroke(width = 6f)
                    )
                    drawCircle(
                        color = targetColor,
                        radius = width / 6f,
                        center = center
                    )
                }


                CellType.WIRE -> {
                    // Gray square / Wire line
                    drawRoundRect(
                        color = WireGray.copy(alpha = 0.2f),
                        size = Size(width, height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                    )
                    // Wire straight line through center
                    drawLine(
                        color = WireGray,
                        start = Offset(width / 2f, 0f),
                        end = Offset(width / 2f, height),
                        strokeWidth = 8f
                    )
                }

                CellType.MIRROR -> {
                    // Blue square (Mirror)
                    drawRoundRect(
                        color = MirrorBlue.copy(alpha = 0.25f),
                        size = Size(width, height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                    )
                    // Diagonal mirror line
                    drawLine(
                        color = MirrorBlue,
                        start = Offset(0f, height),
                        end = Offset(width, 0f),
                        strokeWidth = 10f
                    )
                }

                CellType.SPLITTER -> {
                    // Splitter cyan component
                    drawRoundRect(
                        color = SplitterCyan.copy(alpha = 0.25f),
                        size = Size(width, height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                    )
                    val path = Path().apply {
                        moveTo(width / 2f, height)
                        lineTo(width / 2f, height / 2f)
                        lineTo(0f, 0f)
                        moveTo(width / 2f, height / 2f)
                        lineTo(width, 0f)
                    }
                    drawPath(
                        path = path,
                        color = SplitterCyan,
                        style = Stroke(width = 8f)
                    )
                }

                CellType.FILTER -> {
                    // Purple filter block
                    drawRoundRect(
                        color = FilterPurple.copy(alpha = 0.3f),
                        size = Size(width, height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                    )
                    drawCircle(
                        color = FilterPurple,
                        radius = width / 4f,
                        center = center
                    )
                }

                CellType.GATE -> {
                    // Orange logic gate
                    drawRoundRect(
                        color = GateOrange.copy(alpha = 0.3f),
                        size = Size(width, height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                    )
                    drawLine(
                        color = GateOrange,
                        start = Offset(width / 4f, height / 2f),
                        end = Offset(3f * width / 4f, height / 2f),
                        strokeWidth = 8f
                    )
                }

                CellType.BLOCK -> {
                    // Solid dark obstacle block
                    drawRoundRect(
                        color = BlockDark,
                        size = Size(width, height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                    )
                    drawLine(
                        color = Color.Red.copy(alpha = 0.4f),
                        start = Offset(0f, 0f),
                        end = Offset(width, height),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = Color.Red.copy(alpha = 0.4f),
                        start = Offset(width, 0f),
                        end = Offset(0f, height),
                        strokeWidth = 4f
                    )
                }
            }
        }
    }
}
