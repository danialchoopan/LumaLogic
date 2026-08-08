package ir.danialchoopan.lumalogic.domain.model

import ir.danialchoopan.lumalogic.data.model.Direction
import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.data.model.Position

/**
 * Immutable state of an active light beam branch.
 */
data class BeamState(
    val position: Position,
    val direction: Direction,
    val color: LightColor = LightColor.WHITE,
    val energy: Int = 100,
    val sourceId: String = "",
    val branchId: String = "branch_0"
)
