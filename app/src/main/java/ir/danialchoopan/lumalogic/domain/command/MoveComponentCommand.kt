package ir.danialchoopan.lumalogic.domain.command

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.domain.model.GameSnapshot

/**
 * Command encapsulating moving/swapping movable components across grid positions.
 */
data class MoveComponentCommand(
    val fromPosition: Position,
    val toPosition: Position,
    val sourceCellBefore: Cell,
    val targetCellBefore: Cell,
    override val beforeState: GameSnapshot? = null,
    override var afterState: GameSnapshot? = null
) : GameCommand {

    override fun execute(cells: List<Cell>): List<Cell> {
        val fromCell = cells.find { it.row == fromPosition.row && it.column == fromPosition.column } ?: return cells
        val toCell = cells.find { it.row == toPosition.row && it.column == toPosition.column } ?: return cells

        return cells.map { cell ->
            when {
                cell.row == fromPosition.row && cell.column == fromPosition.column -> {
                    toCell.copy(
                        id = cell.id,
                        row = fromPosition.row,
                        column = fromPosition.column
                    )
                }
                cell.row == toPosition.row && cell.column == toPosition.column -> {
                    fromCell.copy(
                        id = cell.id,
                        row = toPosition.row,
                        column = toPosition.column
                    )
                }
                else -> cell
            }
        }
    }

    override fun undo(cells: List<Cell>): List<Cell> {
        return cells.map { cell ->
            when {
                cell.row == fromPosition.row && cell.column == fromPosition.column -> sourceCellBefore
                cell.row == toPosition.row && cell.column == toPosition.column -> targetCellBefore
                else -> cell
            }
        }
    }
}
