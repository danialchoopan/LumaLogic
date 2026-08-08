package ir.danialchoopan.lumalogic.domain.usecase

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.domain.engine.GameEngine

/**
 * Use case to rotate a cell on the grid.
 */
class RotateCellUseCase(private val gameEngine: GameEngine) {
    operator fun invoke(cellId: String): List<Cell> {
        return gameEngine.rotateCell(cellId)
    }

    operator fun invoke(position: ir.danialchoopan.lumalogic.data.model.Position): List<Cell> {
        return gameEngine.rotateCellAt(position)
    }
}
