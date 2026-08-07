package ir.danialchoopan.lumalogic

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.Direction
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.data.model.Rotation
import ir.danialchoopan.lumalogic.domain.engine.GridEngine
import ir.danialchoopan.lumalogic.domain.engine.MirrorLogic
import ir.danialchoopan.lumalogic.domain.engine.MirrorType
import ir.danialchoopan.lumalogic.domain.engine.StopReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LightTraceEngineTest {

    private lateinit var gridEngine: GridEngine

    @Before
    fun setUp() {
        gridEngine = GridEngine()
    }

    @Test
    fun `test 1 straight line source to target reaches target successfully`() {
        // Grid 5x5: Source at (2,0) facing RIGHT, Target at (2,4)
        val cells = listOf(
            Cell(id = "s", row = 2, column = 0, type = CellType.SOURCE, rotation = Rotation.NINETY),
            Cell(id = "t", row = 2, column = 4, type = CellType.TARGET, rotation = Rotation.ZERO)
        )

        val result = gridEngine.traceLight(rows = 5, columns = 5, cells = cells)

        assertTrue("Light trace should succeed", result.success)
        assertEquals(StopReason.TARGET_REACHED, result.stoppedReason)
        assertTrue("Target position (2,4) should be activated", result.activatedTargets.contains(Position(2, 4)))
    }

    @Test
    fun `test 2 mirror reflection redirects light correctly`() {
        // MIRROR_FORWARD (/): RIGHT -> UP
        val reflectedDirection = MirrorLogic.reflect(Direction.RIGHT, MirrorType.MIRROR_FORWARD)
        assertEquals(Direction.UP, reflectedDirection)

        // Demo level layout: Source (3,0) RIGHT -> Mirror (3,3) / -> Target (1,3)
        val cells = listOf(
            Cell(id = "source", row = 3, column = 0, type = CellType.SOURCE, rotation = Rotation.NINETY),
            Cell(id = "mirror", row = 3, column = 3, type = CellType.MIRROR, rotation = Rotation.ZERO),
            Cell(id = "target", row = 1, column = 3, type = CellType.TARGET, rotation = Rotation.ZERO)
        )

        val result = gridEngine.traceLight(rows = 7, columns = 7, cells = cells)

        assertTrue("Light should reflect and activate target", result.success)
        assertEquals(StopReason.TARGET_REACHED, result.stoppedReason)
        assertTrue("Mirror cell (3,3) should be visited", result.visitedCells.contains(Position(3, 3)))
        assertTrue("Target (1,3) should be reached", result.activatedTargets.contains(Position(1, 3)))
    }

    @Test
    fun `test 3 blocked path stops light trace`() {
        // Source (2,0) RIGHT -> Block (2,2) -> Target (2,4)
        val cells = listOf(
            Cell(id = "s", row = 2, column = 0, type = CellType.SOURCE, rotation = Rotation.NINETY),
            Cell(id = "b", row = 2, column = 2, type = CellType.BLOCK, rotation = Rotation.ZERO),
            Cell(id = "t", row = 2, column = 4, type = CellType.TARGET, rotation = Rotation.ZERO)
        )

        val result = gridEngine.traceLight(rows = 5, columns = 5, cells = cells)

        assertFalse("Light trace should fail due to obstacle block", result.success)
        assertEquals(StopReason.BLOCKED, result.stoppedReason)
        assertFalse("Target should NOT be activated", result.activatedTargets.contains(Position(2, 4)))
    }

    @Test
    fun `test 4 loop detection prevents infinite execution`() {
        // Source at (1,1) emitting UP into a loop circuit of mirrors
        val cells = listOf(
            Cell(id = "s", row = 1, column = 1, type = CellType.SOURCE, rotation = Rotation.ZERO),
            Cell(id = "m1", row = 0, column = 1, type = CellType.MIRROR, rotation = Rotation.NINETY),  // \
            Cell(id = "m2", row = 0, column = 0, type = CellType.MIRROR, rotation = Rotation.ZERO),    // /
            Cell(id = "m3", row = 1, column = 0, type = CellType.MIRROR, rotation = Rotation.NINETY),  // \
            Cell(id = "m4", row = 1, column = 2, type = CellType.MIRROR, rotation = Rotation.NINETY),  // \
            Cell(id = "m5", row = 2, column = 2, type = CellType.MIRROR, rotation = Rotation.ZERO),    // /
            Cell(id = "m6", row = 2, column = 1, type = CellType.MIRROR, rotation = Rotation.NINETY)   // \
        )

        val result = gridEngine.traceLight(rows = 5, columns = 5, cells = cells)

        assertFalse("Loop should be detected", result.success)
        assertEquals(StopReason.LOOP_DETECTED, result.stoppedReason)
    }


    @Test
    fun `test 5 multiple targets must all be activated for level success`() {
        // Source (3,0) RIGHT -> Splitter/Mirror path hitting both Target 1 (1,3) and Target 2 (3,5)
        val cells = listOf(
            Cell(id = "s", row = 3, column = 0, type = CellType.SOURCE, rotation = Rotation.NINETY),
            Cell(id = "t1", row = 3, column = 2, type = CellType.TARGET, rotation = Rotation.ZERO),
            Cell(id = "t2", row = 3, column = 4, type = CellType.TARGET, rotation = Rotation.ZERO)
        )

        val result = gridEngine.traceLight(rows = 5, columns = 6, cells = cells)

        assertTrue("All targets on straight beam should be activated", result.success)
        assertEquals(2, result.activatedTargets.size)
        assertTrue(result.activatedTargets.contains(Position(3, 2)))
        assertTrue(result.activatedTargets.contains(Position(3, 4)))
    }
}
