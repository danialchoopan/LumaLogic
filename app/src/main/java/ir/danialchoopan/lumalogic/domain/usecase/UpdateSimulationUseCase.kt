package ir.danialchoopan.lumalogic.domain.usecase

import ir.danialchoopan.lumalogic.domain.engine.GameEngine
import ir.danialchoopan.lumalogic.domain.engine.LightTraceResult

/**
 * Use case to trigger light trace simulation update on the grid engine.
 */
class UpdateSimulationUseCase(private val gameEngine: GameEngine) {
    operator fun invoke(): LightTraceResult {
        return gameEngine.simulate()
    }
}
