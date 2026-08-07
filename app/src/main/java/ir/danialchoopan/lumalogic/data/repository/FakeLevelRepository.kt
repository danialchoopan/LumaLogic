package ir.danialchoopan.lumalogic.data.repository

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.Level
import ir.danialchoopan.lumalogic.data.model.Rotation

/**
 * Fake level repository providing demo puzzle levels.
 */
class FakeLevelRepository : LevelRepository {

    override fun getDemoLevel(): Level {
        val rows = 7
        val columns = 7
        val cells = mutableListOf<Cell>()

        for (r in 0 until rows) {
            for (c in 0 until columns) {
                val id = "cell_${r}_${c}"
                val cell = when {
                    r == 3 && c == 0 -> Cell(
                        id = id,
                        row = r,
                        column = c,
                        type = CellType.SOURCE,
                        rotation = Rotation.NINETY, // Emission direction RIGHT
                        isLocked = true,
                        isLit = true
                    )
                    r == 3 && c == 3 -> Cell(
                        id = id,
                        row = r,
                        column = c,
                        type = CellType.MIRROR,
                        rotation = Rotation.ZERO, // MIRROR_FORWARD (/) reflects incoming RIGHT to UP
                        isLocked = false,
                        isLit = false
                    )
                    r == 1 && c == 3 -> Cell(
                        id = id,
                        row = r,
                        column = c,
                        type = CellType.TARGET,
                        rotation = Rotation.ZERO,
                        isLocked = true,
                        isLit = false
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
            levelId = "demo_101",
            name = "The Spark",
            rows = rows,
            columns = columns,
            cells = cells,
            difficulty = "Demo Level"
        )
    }

    override fun getLevels(): List<Level> {
        return listOf(getDemoLevel())
    }

    override fun getLevelById(id: String): Level? {
        return if (id == "demo_101") getDemoLevel() else null
    }
}

