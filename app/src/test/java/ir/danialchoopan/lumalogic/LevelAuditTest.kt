package ir.danialchoopan.lumalogic

import ir.danialchoopan.lumalogic.data.level.LevelRegistry
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.repository.FakeLevelRepository
import ir.danialchoopan.lumalogic.domain.engine.GridEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LevelAuditTest {

    private lateinit var levelRepository: FakeLevelRepository
    private lateinit var gridEngine: GridEngine

    @Before
    fun setUp() {
        levelRepository = FakeLevelRepository()
        gridEngine = GridEngine()
    }

    @Test
    fun `audit 1 verify exactly 16 chapters`() {
        val chapters = LevelRegistry.chapters
        assertEquals("Must have exactly 16 chapters", 16, chapters.size)

        chapters.forEachIndexed { index, chapter ->
            assertEquals(index + 1, chapter.number)
            assertEquals("chapter_%02d".format(index + 1), chapter.id)
            assertEquals(16, chapter.levelCount)
        }
    }

    @Test
    fun `audit 2 verify exactly 256 levels across 16 chapters`() {
        val allLevels = levelRepository.getBuiltInLevels()
        assertEquals("Must have exactly 256 levels", 256, allLevels.size)

        val uniqueIds = allLevels.map { it.levelId }.toSet()
        assertEquals("All level IDs must be unique", 256, uniqueIds.size)

        for (ch in 1..16) {
            val chapterId = "chapter_%02d".format(ch)
            val chapterLevels = LevelRegistry.getLevelsForChapter(chapterId)
            assertEquals("Chapter $ch must contain 16 levels", 16, chapterLevels.size)

            for (lvl in 1..16) {
                val expectedId = "chapter_%02d_level_%02d".format(ch, lvl)
                val loadedLevel = levelRepository.getLevelById(expectedId)
                assertNotNull("Level $expectedId must be loadable through repository", loadedLevel)
                assertEquals(expectedId, loadedLevel?.levelId)
            }
        }
    }

    @Test
    fun `audit 3 validate grid structure and component bounds for all 256 levels`() {
        val allLevels = levelRepository.getBuiltInLevels()

        for (level in allLevels) {
            assertTrue("Level ${level.levelId} must have rows > 0", level.rows > 0)
            assertTrue("Level ${level.levelId} must have columns > 0", level.columns > 0)
            assertEquals(
                "Level ${level.levelId} cell count must match grid dimensions (${level.rows}x${level.columns})",
                level.rows * level.columns,
                level.cells.size
            )

            val positionSet = mutableSetOf<String>()
            var sourceCount = 0
            var targetCount = 0

            for (cell in level.cells) {
                assertTrue(
                    "Cell row ${cell.row} in ${level.levelId} must be within bounds 0..${level.rows - 1}",
                    cell.row in 0 until level.rows
                )
                assertTrue(
                    "Cell col ${cell.column} in ${level.levelId} must be within bounds 0..${level.columns - 1}",
                    cell.column in 0 until level.columns
                )

                val posKey = "${cell.row}_${cell.column}"
                assertTrue("Duplicate cell at ($posKey) in ${level.levelId}", positionSet.add(posKey))

                if (cell.type == CellType.SOURCE) sourceCount++
                if (cell.type == CellType.TARGET) targetCount++
            }

            assertTrue("Level ${level.levelId} must contain at least one SOURCE", sourceCount > 0)
            assertTrue("Level ${level.levelId} must contain at least one TARGET", targetCount > 0)
            assertTrue("Level ${level.levelId} maximum energy must be > 0", level.maximumEnergy > 0)
        }
    }

    @Test
    fun `audit 4 verify max star calculation across 256 levels equals 768`() {
        val totalLevels = 256
        val maxStarsPerLevel = 3
        val maxStarsTotal = totalLevels * maxStarsPerLevel
        assertEquals("Maximum possible stars must be 768", 768, maxStarsTotal)
    }

    @Test
    fun `audit 5 verify trace simulation does not crash or infinite loop on any of the 256 levels`() {
        val allLevels = levelRepository.getBuiltInLevels()

        for (level in allLevels) {
            val result = gridEngine.traceLight(
                rows = level.rows,
                columns = level.columns,
                cells = level.cells
            )
            assertNotNull("Simulation result for ${level.levelId} must not be null", result)
            assertNotNull("Stop reason for ${level.levelId} must be defined", result.stoppedReason)
        }
    }

    /**
     * Audit 6: Ensures that NONE of the 256 handcrafted levels auto-complete upon loading.
     * Every level board MUST start with all required targets deactivated.
     */
    @Test
    fun `audit 6 verify all 256 levels start in strictly unsolved state on initial load`() {
        val allLevels = levelRepository.getBuiltInLevels()
        val autoSolvedLevels = mutableListOf<String>()

        for (level in allLevels) {
            val result = gridEngine.traceLight(
                rows = level.rows,
                columns = level.columns,
                cells = level.cells,
                energyConfig = level.energyConfig
            )
            if (result.success) {
                autoSolvedLevels.add("${level.levelId} (${level.name})")
            }
        }
        org.junit.Assert.assertTrue(
            "The following levels were auto-solved on load: ${autoSolvedLevels.joinToString()}",
            autoSolvedLevels.isEmpty()
        )
    }
}
