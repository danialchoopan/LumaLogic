package ir.danialchoopan.lumalogic.domain.model

import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.Direction
import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.data.model.Position

enum class BeamAction {
    MOVE,
    REFLECT,
    SPLIT,
    FILTER_PASS,
    FILTER_BLOCK,
    GATE_INPUT,
    GATE_OUTPUT,
    TARGET_ACTIVATE,
    LOOP_STOP,
    OUT_OF_BOUNDS
}

/**
 * Diagnostic event emitted during light trace simulation steps.
 */
data class BeamEvent(
    val step: Int,
    val position: Position,
    val direction: Direction,
    val color: LightColor,
    val componentType: CellType?,
    val action: BeamAction
)
