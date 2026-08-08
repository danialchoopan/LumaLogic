package ir.danialchoopan.lumalogic.domain.model

import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.data.model.Position

/**
 * Represents a single straight segment of a light beam path from start to end position.
 */
data class BeamSegment(
    val start: Position,
    val end: Position,
    val color: LightColor = LightColor.WHITE,
    val branchId: String = "branch_0"
)
