package ir.danialchoopan.lumalogic

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.data.model.Rotation
import ir.danialchoopan.lumalogic.domain.debug.DebugSimulationController
import ir.danialchoopan.lumalogic.domain.engine.GridEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DebugSimulationTest {

    private lateinit var controller: DebugSimulationController
    private lateinit var gridEngine: GridEngine

    @Before
    fun setUp() {
        controller = DebugSimulationController()
        gridEngine = GridEngine()
    }

    @Test
    fun `test 1 debug simulation starts, steps forward, and resets`() {
        val cells = listOf(
            Cell(id = "s", row = 2, column = 0, type = CellType.SOURCE, rotation = Rotation.NINETY),
            Cell(id = "t", row = 2, column = 3, type = CellType.TARGET, rotation = Rotation.ZERO)
        )

        val traceResult = gridEngine.traceLight(rows = 5, columns = 5, cells = cells)
        controller.loadTraceResult(traceResult)

        val startState = controller.start()
        assertTrue("Debug should be running when started", startState.isRunning)
        assertEquals(1, startState.currentStep)
        assertTrue(startState.totalSteps > 0)

        // Step Forward
        val step2State = controller.stepForward()
        assertEquals(2, step2State.currentStep)
        assertNotNull(step2State.currentEvent)

        // Reset
        val resetState = controller.reset()
        assertEquals(1, resetState.currentStep)
        assertFalse(resetState.isRunning)
    }

    @Test
    fun `test 2 pause and step backward works`() {
        val cells = listOf(
            Cell(id = "s", row = 2, column = 0, type = CellType.SOURCE, rotation = Rotation.NINETY),
            Cell(id = "t", row = 2, column = 3, type = CellType.TARGET, rotation = Rotation.ZERO)
        )

        val traceResult = gridEngine.traceLight(rows = 5, columns = 5, cells = cells)
        controller.loadTraceResult(traceResult)

        controller.start()
        controller.stepForward()

        val pausedState = controller.pause()
        assertTrue(pausedState.isPaused)
        assertFalse(pausedState.isRunning)

        val backState = controller.stepBackward()
        assertEquals(1, backState.currentStep)
    }

    @Test
    fun `test 3 loop detection stops debug simulation safely`() {
        val cells = listOf(
            Cell(id = "s", row = 1, column = 1, type = CellType.SOURCE, rotation = Rotation.ZERO),
            Cell(id = "m1", row = 0, column = 1, type = CellType.MIRROR, rotation = Rotation.NINETY),
            Cell(id = "m2", row = 0, column = 0, type = CellType.MIRROR, rotation = Rotation.ZERO),
            Cell(id = "m3", row = 1, column = 0, type = CellType.MIRROR, rotation = Rotation.NINETY)
        )

        val traceResult = gridEngine.traceLight(rows = 5, columns = 5, cells = cells)
        controller.loadTraceResult(traceResult)

        val finishState = controller.finish()
        assertTrue("Loop event or end of trace should stop simulation", finishState.isFinished)
    }
}
