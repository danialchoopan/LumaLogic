package ir.danialchoopan.lumalogic.domain.usecase

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.domain.engine.GameEngine

/**
 * Use case to reset the current level.
 */
class ResetLevelUseCase(private val gameEngine: GameEngine) {
    operator fun invoke(): List<Cell> {
        gameEngine.reset()
        return gameEngine.getGrid()
    }
}
