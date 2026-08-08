package ir.danialchoopan.lumalogic.domain.engine

import ir.danialchoopan.lumalogic.data.model.Rotation

/**
 * Utility object providing reusable component rotation calculations.
 */
object RotationUtils {

    /**
     * Rotates a component clockwise by 90 degrees.
     * ZERO (0°) -> NINETY (90°) -> ONE_EIGHTY (180°) -> TWO_SEVENTY (270°) -> ZERO (0°)
     */
    fun rotateClockwise(current: Rotation): Rotation {
        return current.next()
    }

    /**
     * Rotates a component counter-clockwise by 90 degrees.
     */
    fun rotateCounterClockwise(current: Rotation): Rotation {
        return when (current) {
            Rotation.ZERO -> Rotation.TWO_SEVENTY
            Rotation.NINETY -> Rotation.ZERO
            Rotation.ONE_EIGHTY -> Rotation.NINETY
            Rotation.TWO_SEVENTY -> Rotation.ONE_EIGHTY
        }
    }
}
