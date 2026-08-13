package ir.danialchoopan.lumalogic

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.Direction
import ir.danialchoopan.lumalogic.data.model.GateType
import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.data.model.Rotation
import ir.danialchoopan.lumalogic.domain.engine.FilterLogic
import ir.danialchoopan.lumalogic.domain.engine.GateLogic
import ir.danialchoopan.lumalogic.domain.engine.GridEngine
import ir.danialchoopan.lumalogic.domain.engine.MirrorLogic
import ir.danialchoopan.lumalogic.domain.engine.MirrorType
import ir.danialchoopan.lumalogic.domain.engine.SplitterLogic
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
        val reflectedDirection = MirrorLogic.reflect(Direction.RIGHT, MirrorType.MIRROR_FORWARD)
        assertEquals(Direction.UP, reflectedDirection)

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
        val cells = listOf(
            Cell(id = "s", row = 1, column = 1, type = CellType.SOURCE, rotation = Rotation.ZERO),
            Cell(id = "m1", row = 0, column = 1, type = CellType.MIRROR, rotation = Rotation.NINETY),
            Cell(id = "m2", row = 0, column = 0, type = CellType.MIRROR, rotation = Rotation.ZERO),
            Cell(id = "m3", row = 1, column = 0, type = CellType.MIRROR, rotation = Rotation.NINETY),
            Cell(id = "m4", row = 1, column = 2, type = CellType.MIRROR, rotation = Rotation.NINETY),
            Cell(id = "m5", row = 2, column = 2, type = CellType.MIRROR, rotation = Rotation.ZERO),
            Cell(id = "m6", row = 2, column = 1, type = CellType.MIRROR, rotation = Rotation.NINETY)
        )

        val result = gridEngine.traceLight(rows = 5, columns = 5, cells = cells)

        assertFalse("Loop should be detected", result.success)
        assertEquals(StopReason.LOOP_DETECTED, result.stoppedReason)
    }

    @Test
    fun `test 5 splitter divides beam into two perpendicular branches`() {
        val (dir1, dir2) = SplitterLogic.splitBeam(Direction.RIGHT, Rotation.ZERO)
        assertEquals(Direction.UP, dir1)
        assertEquals(Direction.DOWN, dir2)

        val cells = listOf(
            Cell(id = "s", row = 2, column = 0, type = CellType.SOURCE, rotation = Rotation.NINETY),
            Cell(id = "spl", row = 2, column = 2, type = CellType.SPLITTER, rotation = Rotation.ZERO),
            Cell(id = "t1", row = 0, column = 2, type = CellType.TARGET, rotation = Rotation.ZERO),
            Cell(id = "t2", row = 4, column = 2, type = CellType.TARGET, rotation = Rotation.ZERO)
        )

        val result = gridEngine.traceLight(rows = 5, columns = 5, cells = cells)

        assertTrue("Splitter should split light and reach both targets", result.success)
        assertEquals(2, result.activatedTargets.size)
        assertTrue(result.activatedTargets.contains(Position(0, 2)))
        assertTrue(result.activatedTargets.contains(Position(4, 2)))
    }

    @Test
    fun `test 6 filter allows matching color and blocks non matching color`() {
        assertTrue("Red filter passes red light", FilterLogic.shouldPass(LightColor.RED, LightColor.RED))
        assertFalse("Red filter blocks blue light", FilterLogic.shouldPass(LightColor.BLUE, LightColor.RED))

        val passCells = listOf(
            Cell(id = "s", row = 2, column = 0, type = CellType.SOURCE, rotation = Rotation.NINETY, lightColor = LightColor.RED),
            Cell(id = "f", row = 2, column = 2, type = CellType.FILTER, acceptedColor = LightColor.RED),
            Cell(id = "t", row = 2, column = 4, type = CellType.TARGET, requiredColor = LightColor.RED)
        )
        val passResult = gridEngine.traceLight(rows = 5, columns = 5, cells = passCells)
        assertTrue("Matching color filter passes light", passResult.success)

        val failCells = listOf(
            Cell(id = "s", row = 2, column = 0, type = CellType.SOURCE, rotation = Rotation.NINETY, lightColor = LightColor.BLUE),
            Cell(id = "f", row = 2, column = 2, type = CellType.FILTER, acceptedColor = LightColor.RED),
            Cell(id = "t", row = 2, column = 4, type = CellType.TARGET, requiredColor = LightColor.RED)
        )
        val failResult = gridEngine.traceLight(rows = 5, columns = 5, cells = failCells)
        assertFalse("Wrong color filter blocks light", failResult.success)
        assertEquals(StopReason.FILTER_BLOCKED, failResult.stoppedReason)
    }

    @Test
    fun `test 7 logic gates AND OR NOT evaluate correctly`() {
        // AND Gate
        assertFalse("AND false/false", GateLogic.evaluate(GateType.AND, false, false))
        assertFalse("AND true/false", GateLogic.evaluate(GateType.AND, true, false))
        assertTrue("AND true/true", GateLogic.evaluate(GateType.AND, true, true))

        // OR Gate
        assertFalse("OR false/false", GateLogic.evaluate(GateType.OR, false, false))
        assertTrue("OR true/false", GateLogic.evaluate(GateType.OR, true, false))
        assertTrue("OR true/true", GateLogic.evaluate(GateType.OR, true, true))

        // NOT Gate
        assertTrue("NOT false", GateLogic.evaluate(GateType.NOT, false, false))
        assertFalse("NOT true", GateLogic.evaluate(GateType.NOT, true, false))
    }

    @Test
    fun `test 8 demo level simulation traces light successfully`() {
        val cells = listOf(
            Cell(id = "s", row = 1, column = 0, type = CellType.SOURCE, rotation = Rotation.NINETY),
            Cell(id = "m", row = 1, column = 2, type = CellType.MIRROR, rotation = Rotation.NINETY),
            Cell(id = "t", row = 3, column = 2, type = CellType.TARGET, rotation = Rotation.ZERO)
        )

        val result = gridEngine.traceLight(rows = 5, columns = 5, cells = cells)

        assertTrue("Demo level trace should reach target and succeed", result.success)
        assertEquals(StopReason.TARGET_REACHED, result.stoppedReason)
        assertTrue("Target at (3,2) should be activated", result.activatedTargets.contains(Position(3, 2)))
    }
}
