package ir.danialchoopan.lumalogic

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.GateType
import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.data.model.Rotation
import ir.danialchoopan.lumalogic.domain.hint.HintEngine
import ir.danialchoopan.lumalogic.domain.hint.HintType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HintEngineTest {

    private lateinit var hintEngine: HintEngine

    @Before
    fun setUp() {
        hintEngine = HintEngine()
    }

    @Test
    fun `test 1 hint identifies useful rotation action`() {
        val cells = listOf(
            Cell(id = "s", row = 2, column = 0, type = CellType.SOURCE, rotation = Rotation.NINETY),
            // Mirror wrong orientation (NINETY)
            Cell(id = "m", row = 2, column = 2, type = CellType.MIRROR, rotation = Rotation.NINETY, isLocked = false),
            Cell(id = "t", row = 0, column = 2, type = CellType.TARGET, rotation = Rotation.ZERO)
        )

        val hint = hintEngine.analyzeLevel(rows = 5, columns = 5, cells = cells)

        assertNotNull(hint)
        assertEquals(HintType.ROTATE, hint.type)
        assertEquals(Position(2, 2), hint.position)
        assertTrue(hint.message.contains("Rotate the Mirror"))
    }

    @Test
    fun `test 2 hint identifies blocked filter`() {
        val cells = listOf(
            Cell(id = "s", row = 2, column = 0, type = CellType.SOURCE, rotation = Rotation.NINETY, lightColor = LightColor.BLUE),
            Cell(id = "f", row = 2, column = 2, type = CellType.FILTER, acceptedColor = LightColor.RED),
            Cell(id = "t", row = 2, column = 4, type = CellType.TARGET, requiredColor = LightColor.RED)
        )

        val hint = hintEngine.analyzeLevel(rows = 5, columns = 5, cells = cells)

        assertNotNull(hint)
        assertEquals(HintType.COLOR, hint.type)
        assertEquals(Position(2, 2), hint.position)
        assertTrue(hint.message.contains("blocking"))
    }

    @Test
    fun `test 3 hint identifies missing gate input`() {
        val cells = listOf(
            Cell(id = "s", row = 2, column = 0, type = CellType.SOURCE, rotation = Rotation.NINETY),
            Cell(id = "g", row = 2, column = 3, type = CellType.GATE, gateType = GateType.AND, rotation = Rotation.ZERO),
            Cell(id = "t", row = 2, column = 4, type = CellType.TARGET, rotation = Rotation.ZERO)
        )

        val hint = hintEngine.analyzeLevel(rows = 5, columns = 5, cells = cells)

        assertNotNull(hint)
        assertTrue(hint.type == HintType.GATE || hint.type == HintType.ROTATE || hint.type == HintType.GENERAL)
    }
}
