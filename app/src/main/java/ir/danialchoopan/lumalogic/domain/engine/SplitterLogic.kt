package ir.danialchoopan.lumalogic.domain.engine

import ir.danialchoopan.lumalogic.data.model.Direction
import ir.danialchoopan.lumalogic.data.model.Rotation

/**
 * Splitter logic module handling beam division into dual perpendicular branches.
 */
object SplitterLogic {

    /**
     * Splits an incoming beam direction into two outgoing directions.
     * ZERO (0°) rotation:
     * - RIGHT -> UP, DOWN
     * - LEFT -> UP, DOWN
     * - UP -> LEFT, RIGHT
     * - DOWN -> LEFT, RIGHT
     *
     * Rotated splitters offset these outputs accordingly.
     */
    fun splitBeam(incomingDirection: Direction, rotation: Rotation): Pair<Direction, Direction> {
        val baseSplit = when (incomingDirection) {
            Direction.RIGHT, Direction.LEFT -> Pair(Direction.UP, Direction.DOWN)
            Direction.UP, Direction.DOWN -> Pair(Direction.LEFT, Direction.RIGHT)
        }

        return when (rotation) {
            Rotation.ZERO -> baseSplit
            Rotation.NINETY -> Pair(rotateDirection(baseSplit.first, 1), rotateDirection(baseSplit.second, 1))
            Rotation.ONE_EIGHTY -> Pair(rotateDirection(baseSplit.first, 2), rotateDirection(baseSplit.second, 2))
            Rotation.TWO_SEVENTY -> Pair(rotateDirection(baseSplit.first, 3), rotateDirection(baseSplit.second, 3))
        }
    }

    private fun rotateDirection(direction: Direction, steps: Int): Direction {
        var curr = direction
        repeat(steps) {
            curr = when (curr) {
                Direction.UP -> Direction.RIGHT
                Direction.RIGHT -> Direction.DOWN
                Direction.DOWN -> Direction.LEFT
                Direction.LEFT -> Direction.UP
            }
        }
        return curr
    }
}
