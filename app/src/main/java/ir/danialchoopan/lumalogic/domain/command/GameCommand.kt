package ir.danialchoopan.lumalogic.domain.command

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.domain.model.GameSnapshot

/**
 * Domain-level abstraction for game commands following the Command Pattern.
 */
interface GameCommand {
    val beforeState: GameSnapshot?
    val afterState: GameSnapshot?

    fun execute(cells: List<Cell>): List<Cell>
    fun undo(cells: List<Cell>): List<Cell>
}
