package ir.danialchoopan.lumalogic.domain.usecase

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.domain.engine.GameEngine

/**
 * Result sealed class for move operations.
 */
sealed class MoveResult {
    data class Success(val cells: List<Cell>) : MoveResult()
    data class Failure(val reason: String) : MoveResult()
}

/**
 * Use case to move a movable component between grid cells.
 */
class MoveCellUseCase(private val gameEngine: GameEngine) {
    operator fun invoke(from: Position, to: Position): MoveResult {
        return gameEngine.moveCell(from, to)
    }
}
