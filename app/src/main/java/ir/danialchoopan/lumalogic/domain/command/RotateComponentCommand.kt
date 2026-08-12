package ir.danialchoopan.lumalogic.domain.command

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.data.model.Rotation
import ir.danialchoopan.lumalogic.domain.model.GameSnapshot

/**
 * Command encapsulating component rotation on the grid.
 */
data class RotateComponentCommand(
    val position: Position,
    val previousRotation: Rotation,
    val newRotation: Rotation,
    override val beforeState: GameSnapshot? = null,
    override var afterState: GameSnapshot? = null
) : GameCommand {

    override fun execute(cells: List<Cell>): List<Cell> {
        return cells.map { cell ->
            if (cell.row == position.row && cell.column == position.column) {
                cell.copy(rotation = newRotation)
            } else {
                cell
            }
        }
    }

    override fun undo(cells: List<Cell>): List<Cell> {
        return cells.map { cell ->
            if (cell.row == position.row && cell.column == position.column) {
                cell.copy(rotation = previousRotation)
            } else {
                cell
            }
        }
    }
}
