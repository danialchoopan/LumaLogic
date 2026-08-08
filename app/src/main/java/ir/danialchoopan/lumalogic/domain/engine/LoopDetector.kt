package ir.danialchoopan.lumalogic.domain.engine

import ir.danialchoopan.lumalogic.data.model.Direction
import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.data.model.Position

private data class VisitedKey(
    val position: Position,
    val direction: Direction,
    val color: LightColor,
    val branchId: String
)

/**
 * Loop detector module avoiding infinite recursion by tracking visited position, direction, color, and branch context.
 */
class LoopDetector {
    private val visitedKeys = mutableSetOf<VisitedKey>()

    fun isLoop(position: Position, direction: Direction, color: LightColor, branchId: String): Boolean {
        val key = VisitedKey(position, direction, color, branchId)
        if (visitedKeys.contains(key)) {
            return true
        }
        visitedKeys.add(key)
        return false
    }

    fun clear() {
        visitedKeys.clear()
    }
}
