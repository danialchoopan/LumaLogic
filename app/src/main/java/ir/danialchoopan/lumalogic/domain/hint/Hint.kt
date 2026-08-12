package ir.danialchoopan.lumalogic.domain.hint

import ir.danialchoopan.lumalogic.data.model.Position

/**
 * Data class representing an actionable hint recommendation.
 */
data class Hint(
    val type: HintType,
    val position: Position? = null,
    val targetPosition: Position? = null,
    val message: String,
    val priority: Int = 1,
    val suggestedAction: String? = null
)
