package ir.danialchoopan.lumalogic

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.data.model.Rotation
import ir.danialchoopan.lumalogic.domain.command.CommandHistory
import ir.danialchoopan.lumalogic.domain.command.MoveComponentCommand
import ir.danialchoopan.lumalogic.domain.command.RotateComponentCommand
import ir.danialchoopan.lumalogic.domain.engine.GridEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CommandAndHistoryTest {

    private lateinit var history: CommandHistory
    private lateinit var gridEngine: GridEngine

    @Before
    fun setUp() {
        history = CommandHistory()
        gridEngine = GridEngine()
    }

    @Test
    fun `test 1 rotate mirror, undo, redo`() {
        val cells = listOf(
            Cell(id = "m", row = 1, column = 1, type = CellType.MIRROR, rotation = Rotation.ZERO)
        )

        val cmd = RotateComponentCommand(
            position = Position(1, 1),
            previousRotation = Rotation.ZERO,
            newRotation = Rotation.NINETY
        )

        // Execute rotation
        var updated = cmd.execute(cells)
        history.execute(cmd)

        assertEquals(Rotation.NINETY, updated[0].rotation)
        assertTrue(history.canUndo())
        assertFalse(history.canRedo())

        // Undo rotation
        val popped = history.undo()
        updated = popped!!.undo(updated)

        assertEquals(Rotation.ZERO, updated[0].rotation)
        assertFalse(history.canUndo())
        assertTrue(history.canRedo())

        // Redo rotation
        val redone = history.redo()
        updated = redone!!.execute(updated)

        assertEquals(Rotation.NINETY, updated[0].rotation)
        assertTrue(history.canUndo())
        assertFalse(history.canRedo())
    }

    @Test
    fun `test 2 multiple undo and redo operations`() {
        val cells = listOf(
            Cell(id = "m", row = 1, column = 1, type = CellType.MIRROR, rotation = Rotation.ZERO)
        )

        var current = cells
        val cmd1 = RotateComponentCommand(Position(1, 1), Rotation.ZERO, Rotation.NINETY)
        current = cmd1.execute(current)
        history.execute(cmd1)

        val cmd2 = RotateComponentCommand(Position(1, 1), Rotation.NINETY, Rotation.ONE_EIGHTY)
        current = cmd2.execute(current)
        history.execute(cmd2)

        assertEquals(Rotation.ONE_EIGHTY, current[0].rotation)
        assertEquals(2, history.undoCount())

        // Undo twice
        current = history.undo()!!.undo(current)
        assertEquals(Rotation.NINETY, current[0].rotation)

        current = history.undo()!!.undo(current)
        assertEquals(Rotation.ZERO, current[0].rotation)

        // Redo twice
        current = history.redo()!!.execute(current)
        assertEquals(Rotation.NINETY, current[0].rotation)

        current = history.redo()!!.execute(current)
        assertEquals(Rotation.ONE_EIGHTY, current[0].rotation)
    }

    @Test
    fun `test 3 new action clears redo history`() {
        val cells = listOf(
            Cell(id = "m", row = 1, column = 1, type = CellType.MIRROR, rotation = Rotation.ZERO)
        )

        val cmd1 = RotateComponentCommand(Position(1, 1), Rotation.ZERO, Rotation.NINETY)
        history.execute(cmd1)

        history.undo()
        assertTrue(history.canRedo())

        val cmd2 = RotateComponentCommand(Position(1, 1), Rotation.ZERO, Rotation.TWO_SEVENTY)
        history.execute(cmd2)

        assertFalse("New action must clear redo history stack", history.canRedo())
    }

    @Test
    fun `test 4 move component, undo move, redo move`() {
        val sourceCell = Cell(id = "m", row = 0, column = 0, type = CellType.MIRROR, rotation = Rotation.NINETY)
        val targetCell = Cell(id = "e", row = 0, column = 1, type = CellType.EMPTY, rotation = Rotation.ZERO)

        val initialCells = listOf(sourceCell, targetCell)

        val cmd = MoveComponentCommand(
            fromPosition = Position(0, 0),
            toPosition = Position(0, 1),
            sourceCellBefore = sourceCell,
            targetCellBefore = targetCell
        )

        var updated = cmd.execute(initialCells)
        history.execute(cmd)

        // Mirror should be at (0,1)
        val movedMirror = updated.find { it.row == 0 && it.column == 1 }
        assertEquals(CellType.MIRROR, movedMirror?.type)

        // Undo move
        updated = history.undo()!!.undo(updated)
        val undoneMirror = updated.find { it.row == 0 && it.column == 0 }
        assertEquals(CellType.MIRROR, undoneMirror?.type)

        // Redo move
        updated = history.redo()!!.execute(updated)
        val redoneMirror = updated.find { it.row == 0 && it.column == 1 }
        assertEquals(CellType.MIRROR, redoneMirror?.type)
    }

    @Test
    fun `test 5 undo recalculates beam trace result correctly`() {
        val source = Cell(id = "s", row = 2, column = 0, type = CellType.SOURCE, rotation = Rotation.NINETY)
        val mirror = Cell(id = "m", row = 2, column = 2, type = CellType.MIRROR, rotation = Rotation.ZERO)
        val target = Cell(id = "t", row = 0, column = 2, type = CellType.TARGET, rotation = Rotation.ZERO)

        var cells = listOf(source, mirror, target)

        // Initial trace (Mirror at ZERO reflects RIGHT -> UP)
        val initialTrace = gridEngine.traceLight(5, 5, cells)
        assertTrue(initialTrace.success)

        // Rotate mirror to NINETY (Reflects RIGHT -> DOWN, missing target at (0,2))
        val cmd = RotateComponentCommand(Position(2, 2), Rotation.ZERO, Rotation.NINETY)
        cells = cmd.execute(cells)
        history.execute(cmd)

        val traceAfterRotation = gridEngine.traceLight(5, 5, cells)
        assertFalse(traceAfterRotation.success)

        // Undo rotation
        cells = history.undo()!!.undo(cells)
        val traceAfterUndo = gridEngine.traceLight(5, 5, cells)
        assertTrue("Beam trace result must be re-solved after undoing mirror rotation", traceAfterUndo.success)
    }
}
