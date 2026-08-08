package ir.danialchoopan.lumalogic.data.model

import com.squareup.moshi.JsonClass

/**
 * Enum representing the direction of light beams or components.
 */
enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

/**
 * Enum representing the rotation of components in 90-degree increments.
 */
enum class Rotation(val degrees: Float) {
    ZERO(0f),
    NINETY(90f),
    ONE_EIGHTY(180f),
    TWO_SEVENTY(270f);

    fun next(): Rotation = when (this) {
        ZERO -> NINETY
        NINETY -> ONE_EIGHTY
        ONE_EIGHTY -> TWO_SEVENTY
        TWO_SEVENTY -> ZERO
    }
}

/**
 * Enum representing the types of grid components available in LumaLogic.
 */
enum class CellType {
    EMPTY,
    SOURCE,
    TARGET,
    WIRE,
    MIRROR,
    SPLITTER,
    FILTER,
    GATE,
    BLOCK
}

/**
 * Represents a position on the grid.
 */
@JsonClass(generateAdapter = true)
data class Position(
    val row: Int,
    val column: Int
)

/**
 * Enum representing light colors.
 */
enum class LightColor {
    WHITE,
    RED,
    BLUE,
    GREEN,
    YELLOW
}

/**
 * Enum representing logic gate types.
 */
enum class GateType {
    AND,
    OR,
    NOT
}

/**
 * Immutable representation of a grid cell.
 */
@JsonClass(generateAdapter = true)
data class Cell(
    val id: String,
    val row: Int,
    val column: Int,
    val type: CellType,
    val rotation: Rotation = Rotation.ZERO,
    val isLocked: Boolean = false,
    val isLit: Boolean = false,
    val lightColor: LightColor? = null,
    val acceptedColor: LightColor? = null,
    val requiredColor: LightColor? = null,
    val gateType: GateType? = null,
    val isOptionalTarget: Boolean = false
)
