package ir.danialchoopan.lumalogic.domain.hint

import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.GateType
import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.data.model.Rotation

/**
 * Data class representing an actionable hint recommendation.
 */
data class Hint(
    val type: HintType,
    val position: Position? = null,
    val targetPosition: Position? = null,
    val cellType: CellType? = null,
    val targetRotation: Rotation? = null,
    val color: LightColor? = null,
    val gateType: GateType? = null,
    val message: String,
    val priority: Int = 1,
    val suggestedAction: String? = null
)

