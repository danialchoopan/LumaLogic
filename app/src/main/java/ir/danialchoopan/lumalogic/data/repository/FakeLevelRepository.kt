package ir.danialchoopan.lumalogic.data.repository

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.GateType
import ir.danialchoopan.lumalogic.data.model.Level
import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.data.model.Rotation
import ir.danialchoopan.lumalogic.data.model.TargetRequirement

/**
 * Repository providing 5 valid built-in puzzle levels and integrating with user levels.
 */
class FakeLevelRepository(
    private val userLevelRepository: UserLevelRepository = LocalUserLevelRepository()
) : LevelRepository {

    private val builtInLevelsList: List<Level> by lazy {
        listOf(
            createLevel001(),
            createLevel002(),
            createLevel003(),
            createLevel004(),
            createLevel005()
        )
    }

    override fun getDemoLevel(): Level {
        return createLevel001()
    }

    override fun getBuiltInLevels(): List<Level> {
        return builtInLevelsList
    }

    override fun getUserLevels(): List<Level> {
        return userLevelRepository.getUserLevels()
    }

    override fun getLevels(): List<Level> {
        return getBuiltInLevels() + getUserLevels()
    }

    override fun getLevelById(id: String): Level? {
        return getLevels().find { it.levelId == id || it.id == id }
    }

    override fun saveUserLevel(level: Level) {
        userLevelRepository.saveUserLevel(level)
    }

    override fun deleteUserLevel(id: String): Boolean {
        if (isBuiltInLevel(id)) {
            return false // Cannot delete built-in levels!
        }
        return userLevelRepository.deleteUserLevel(id)
    }

    override fun isBuiltInLevel(id: String): Boolean {
        return builtInLevelsList.any { it.levelId == id }
    }

    // 1. Easy: First Beam
    private fun createLevel001(): Level {
        val rows = 5
        val columns = 5
        val cells = mutableListOf<Cell>()

        for (r in 0 until rows) {
            for (c in 0 until columns) {
                val id = "c_${r}_${c}"
                val cell = when {
                    r == 2 && c == 0 -> Cell(
                        id = id, row = r, column = c, type = CellType.SOURCE,
                        rotation = Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.WHITE
                    )
                    r == 2 && c == 2 -> Cell(
                        id = id, row = r, column = c, type = CellType.MIRROR,
                        rotation = Rotation.ZERO, isLocked = false
                    )
                    r == 0 && c == 2 -> Cell(
                        id = id, row = r, column = c, type = CellType.TARGET,
                        rotation = Rotation.ZERO, isLocked = true, requiredColor = LightColor.WHITE
                    )
                    else -> Cell(id = id, row = r, column = c, type = CellType.EMPTY)
                }
                cells.add(cell)
            }
        }

        return Level(
            levelId = "lumalogic_001",
            name = "First Beam",
            description = "Reflect light onto the target using a mirror.",
            author = "LumaLogic",
            difficulty = "Easy",
            rows = rows,
            columns = columns,
            cells = cells,
            targetRequirements = listOf(TargetRequirement(position = Position(0, 2), requiredColor = LightColor.WHITE))
        )
    }

    // 2. Easy: Split Spectrum
    private fun createLevel002(): Level {
        val rows = 5
        val columns = 5
        val cells = mutableListOf<Cell>()

        for (r in 0 until rows) {
            for (c in 0 until columns) {
                val id = "c_${r}_${c}"
                val cell = when {
                    r == 2 && c == 0 -> Cell(
                        id = id, row = r, column = c, type = CellType.SOURCE,
                        rotation = Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.WHITE
                    )
                    r == 2 && c == 2 -> Cell(
                        id = id, row = r, column = c, type = CellType.SPLITTER,
                        rotation = Rotation.ZERO, isLocked = false
                    )
                    r == 0 && c == 2 -> Cell(
                        id = id, row = r, column = c, type = CellType.TARGET,
                        rotation = Rotation.ZERO, isLocked = true, requiredColor = LightColor.WHITE
                    )
                    r == 4 && c == 2 -> Cell(
                        id = id, row = r, column = c, type = CellType.TARGET,
                        rotation = Rotation.ZERO, isLocked = true, requiredColor = LightColor.WHITE
                    )
                    else -> Cell(id = id, row = r, column = c, type = CellType.EMPTY)
                }
                cells.add(cell)
            }
        }

        return Level(
            levelId = "lumalogic_002",
            name = "Split Spectrum",
            description = "Use a beam splitter to activate multiple targets at once.",
            author = "LumaLogic",
            difficulty = "Easy",
            rows = rows,
            columns = columns,
            cells = cells,
            targetRequirements = listOf(
                TargetRequirement(position = Position(0, 2), requiredColor = LightColor.WHITE),
                TargetRequirement(position = Position(4, 2), requiredColor = LightColor.WHITE)
            )
        )
    }

    // 3. Medium: Prism Gate
    private fun createLevel003(): Level {
        val rows = 7
        val columns = 7
        val cells = mutableListOf<Cell>()

        for (r in 0 until rows) {
            for (c in 0 until columns) {
                val id = "c_${r}_${c}"
                val cell = when {
                    r == 3 && c == 0 -> Cell(
                        id = id, row = r, column = c, type = CellType.SOURCE,
                        rotation = Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.RED
                    )
                    r == 3 && c == 2 -> Cell(
                        id = id, row = r, column = c, type = CellType.FILTER,
                        rotation = Rotation.ZERO, acceptedColor = LightColor.RED, isLocked = false
                    )
                    r == 3 && c == 4 -> Cell(
                        id = id, row = r, column = c, type = CellType.MIRROR,
                        rotation = Rotation.ONE_EIGHTY, isLocked = false
                    )
                    r == 1 && c == 4 -> Cell(
                        id = id, row = r, column = c, type = CellType.TARGET,
                        rotation = Rotation.ZERO, isLocked = true, requiredColor = LightColor.RED
                    )
                    else -> Cell(id = id, row = r, column = c, type = CellType.EMPTY)
                }
                cells.add(cell)
            }
        }

        return Level(
            levelId = "lumalogic_003",
            name = "Prism Gate",
            description = "Route colored beams through color filters.",
            author = "LumaLogic",
            difficulty = "Medium",
            rows = rows,
            columns = columns,
            cells = cells,
            targetRequirements = listOf(TargetRequirement(position = Position(1, 4), requiredColor = LightColor.RED))
        )
    }

    // 4. Medium: Logic Conjunction
    private fun createLevel004(): Level {
        val rows = 7
        val columns = 7
        val cells = mutableListOf<Cell>()

        for (r in 0 until rows) {
            for (c in 0 until columns) {
                val id = "c_${r}_${c}"
                val cell = when {
                    r == 3 && c == 0 -> Cell(
                        id = id, row = r, column = c, type = CellType.SOURCE,
                        rotation = Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.WHITE
                    )
                    r == 3 && c == 2 -> Cell(
                        id = id, row = r, column = c, type = CellType.SPLITTER,
                        rotation = Rotation.ZERO, isLocked = false
                    )
                    r == 3 && c == 4 -> Cell(
                        id = id, row = r, column = c, type = CellType.GATE,
                        gateType = GateType.OR, rotation = Rotation.NINETY, lightColor = LightColor.BLUE, isLocked = false
                    )
                    r == 3 && c == 6 -> Cell(
                        id = id, row = r, column = c, type = CellType.TARGET,
                        rotation = Rotation.ZERO, isLocked = true, requiredColor = LightColor.BLUE
                    )
                    else -> Cell(id = id, row = r, column = c, type = CellType.EMPTY)
                }
                cells.add(cell)
            }
        }

        return Level(
            levelId = "lumalogic_004",
            name = "Logic Conjunction",
            description = "Combine optical signals through logic gates.",
            author = "LumaLogic",
            difficulty = "Medium",
            rows = rows,
            columns = columns,
            cells = cells,
            targetRequirements = listOf(TargetRequirement(position = Position(3, 6), requiredColor = LightColor.BLUE))
        )
    }

    // 5. Hard: Spectrum Nexus
    private fun createLevel005(): Level {
        val rows = 9
        val columns = 9
        val cells = mutableListOf<Cell>()

        for (r in 0 until rows) {
            for (c in 0 until columns) {
                val id = "c_${r}_${c}"
                val cell = when {
                    r == 4 && c == 0 -> Cell(
                        id = id, row = r, column = c, type = CellType.SOURCE,
                        rotation = Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.RED
                    )
                    r == 4 && c == 2 -> Cell(
                        id = id, row = r, column = c, type = CellType.SPLITTER,
                        rotation = Rotation.ZERO, isLocked = false
                    )
                    r == 2 && c == 2 -> Cell(
                        id = id, row = r, column = c, type = CellType.FILTER,
                        acceptedColor = LightColor.RED, rotation = Rotation.ZERO, isLocked = false
                    )
                    r == 1 && c == 2 -> Cell(
                        id = id, row = r, column = c, type = CellType.MIRROR,
                        rotation = Rotation.ONE_EIGHTY, isLocked = false
                    )
                    r == 1 && c == 6 -> Cell(
                        id = id, row = r, column = c, type = CellType.TARGET,
                        requiredColor = LightColor.RED, rotation = Rotation.ZERO, isLocked = true
                    )
                    r == 6 && c == 2 -> Cell(
                        id = id, row = r, column = c, type = CellType.MIRROR,
                        rotation = Rotation.NINETY, isLocked = false
                    )
                    r == 6 && c == 4 -> Cell(
                        id = id, row = r, column = c, type = CellType.GATE,
                        gateType = GateType.OR, rotation = Rotation.NINETY, lightColor = LightColor.BLUE, isLocked = true
                    )
                    r == 6 && c == 7 -> Cell(
                        id = id, row = r, column = c, type = CellType.TARGET,
                        requiredColor = LightColor.BLUE, rotation = Rotation.ZERO, isLocked = true
                    )
                    else -> Cell(id = id, row = r, column = c, type = CellType.EMPTY)
                }
                cells.add(cell)
            }
        }

        return Level(
            levelId = "lumalogic_005",
            name = "Spectrum Nexus",
            description = "Advanced multi-frequency light routing grid.",
            author = "LumaLogic",
            difficulty = "Hard",
            rows = rows,
            columns = columns,
            cells = cells,
            targetRequirements = listOf(
                TargetRequirement(position = Position(1, 6), requiredColor = LightColor.RED),
                TargetRequirement(position = Position(6, 7), requiredColor = LightColor.BLUE)
            )
        )
    }
}
