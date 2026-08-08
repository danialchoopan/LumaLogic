package ir.danialchoopan.lumalogic.data.repository

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.GateType
import ir.danialchoopan.lumalogic.data.model.Level
import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.data.model.Rotation
import ir.danialchoopan.lumalogic.data.model.TargetRequirement
import ir.danialchoopan.lumalogic.data.model.Position

/**
 * Fake level repository providing demo puzzle levels.
 */
class FakeLevelRepository : LevelRepository {

    override fun getDemoLevel(): Level {
        return getAdvancedDemoLevel()
    }

    fun getAdvancedDemoLevel(): Level {
        val rows = 9
        val columns = 9
        val cells = mutableListOf<Cell>()

        for (r in 0 until rows) {
            for (c in 0 until columns) {
                val id = "cell_${r}_${c}"
                val cell = when {
                    // Source emitting RED light RIGHT
                    r == 4 && c == 0 -> Cell(
                        id = id,
                        row = r,
                        column = c,
                        type = CellType.SOURCE,
                        rotation = Rotation.NINETY,
                        isLocked = true,
                        isLit = true,
                        lightColor = LightColor.RED
                    )

                    // Splitter at (4,2)
                    r == 4 && c == 2 -> Cell(
                        id = id,
                        row = r,
                        column = c,
                        type = CellType.SPLITTER,
                        rotation = Rotation.ZERO,
                        isLocked = false
                    )

                    // Red Filter at (2,2)
                    r == 2 && c == 2 -> Cell(
                        id = id,
                        row = r,
                        column = c,
                        type = CellType.FILTER,
                        acceptedColor = LightColor.RED,
                        rotation = Rotation.ZERO,
                        isLocked = false
                    )

                    // Mirror at (1,2) reflecting UP to RIGHT
                    r == 1 && c == 2 -> Cell(
                        id = id,
                        row = r,
                        column = c,
                        type = CellType.MIRROR,
                        rotation = Rotation.ONE_EIGHTY,
                        isLocked = false
                    )

                    // Red Target at (1,6)
                    r == 1 && c == 6 -> Cell(
                        id = id,
                        row = r,
                        column = c,
                        type = CellType.TARGET,
                        requiredColor = LightColor.RED,
                        rotation = Rotation.ZERO,
                        isLocked = true
                    )

                    // Mirror at (6,2) reflecting DOWN to RIGHT
                    r == 6 && c == 2 -> Cell(
                        id = id,
                        row = r,
                        column = c,
                        type = CellType.MIRROR,
                        rotation = Rotation.NINETY,
                        isLocked = false
                    )

                    // Logic Gate (OR) at (6,4) emitting BLUE light
                    r == 6 && c == 4 -> Cell(
                        id = id,
                        row = r,
                        column = c,
                        type = CellType.GATE,
                        gateType = GateType.OR,
                        rotation = Rotation.NINETY,
                        lightColor = LightColor.BLUE,
                        isLocked = true
                    )

                    // Blue Target at (6,7)
                    r == 6 && c == 7 -> Cell(
                        id = id,
                        row = r,
                        column = c,
                        type = CellType.TARGET,
                        requiredColor = LightColor.BLUE,
                        rotation = Rotation.ZERO,
                        isLocked = true
                    )

                    else -> Cell(
                        id = id,
                        row = r,
                        column = c,
                        type = CellType.EMPTY,
                        rotation = Rotation.ZERO,
                        isLocked = false,
                        isLit = false
                    )
                }
                cells.add(cell)
            }
        }

        return Level(
            levelId = "demo_advanced_909",
            name = "Spectrum Nexus",
            rows = rows,
            columns = columns,
            cells = cells,
            difficulty = "Advanced",
            targetRequirements = listOf(
                TargetRequirement(position = Position(1, 6), requiredColor = LightColor.RED),
                TargetRequirement(position = Position(6, 7), requiredColor = LightColor.BLUE)
            )
        )
    }

    override fun getLevels(): List<Level> {
        return listOf(getAdvancedDemoLevel())
    }

    override fun getLevelById(id: String): Level? {
        return if (id == "demo_advanced_909" || id == "demo_101") getAdvancedDemoLevel() else null
    }
}
