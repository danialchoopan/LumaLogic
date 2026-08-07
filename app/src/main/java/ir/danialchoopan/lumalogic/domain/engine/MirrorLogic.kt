package ir.danialchoopan.lumalogic.domain.engine

import ir.danialchoopan.lumalogic.data.model.Direction
import ir.danialchoopan.lumalogic.data.model.Rotation

/**
 * Mirror type representation for forward / and backward \ mirrors.
 */
enum class MirrorType {
    MIRROR_FORWARD,  // /
    MIRROR_BACKWARD  // \
}

/**
 * Dedicated Mirror Reflection Logic keeping reflection rules modular and decoupled from GridEngine.
 */
object MirrorLogic {

    /**
     * Calculates the reflected direction for an incoming light beam given a mirror type.
     *
     * MIRROR_FORWARD (/):
     * - UP -> RIGHT
     * - DOWN -> LEFT
     * - LEFT -> DOWN
     * - RIGHT -> UP
     *
     * MIRROR_BACKWARD (\):
     * - UP -> LEFT
     * - DOWN -> RIGHT
     * - LEFT -> UP
     * - RIGHT -> DOWN
     */
    fun reflect(incomingDirection: Direction, mirrorType: MirrorType): Direction {
        return when (mirrorType) {
            MirrorType.MIRROR_FORWARD -> when (incomingDirection) {
                Direction.UP -> Direction.RIGHT
                Direction.DOWN -> Direction.LEFT
                Direction.LEFT -> Direction.DOWN
                Direction.RIGHT -> Direction.UP
            }
            MirrorType.MIRROR_BACKWARD -> when (incomingDirection) {
                Direction.UP -> Direction.LEFT
                Direction.DOWN -> Direction.RIGHT
                Direction.LEFT -> Direction.UP
                Direction.RIGHT -> Direction.DOWN
            }
        }
    }

    /**
     * Determines the MirrorType from the Cell's Rotation.
     * ZERO (0°) and ONE_EIGHTY (180°) represent MIRROR_FORWARD (/)
     * NINETY (90°) and TWO_SEVENTY (270°) represent MIRROR_BACKWARD (\)
     */
    fun getMirrorType(rotation: Rotation): MirrorType {
        return when (rotation) {
            Rotation.ZERO, Rotation.ONE_EIGHTY -> MirrorType.MIRROR_FORWARD
            Rotation.NINETY, Rotation.TWO_SEVENTY -> MirrorType.MIRROR_BACKWARD
        }
    }
}
