package ir.danialchoopan.lumalogic.di

import ir.danialchoopan.lumalogic.data.repository.FakeLevelRepository
import ir.danialchoopan.lumalogic.data.repository.LevelRepository
import ir.danialchoopan.lumalogic.domain.engine.GameEngine
import ir.danialchoopan.lumalogic.domain.usecase.GetLevelUseCase
import ir.danialchoopan.lumalogic.domain.usecase.ResetLevelUseCase
import ir.danialchoopan.lumalogic.domain.usecase.RotateCellUseCase

import ir.danialchoopan.lumalogic.domain.usecase.MoveCellUseCase
import ir.danialchoopan.lumalogic.domain.usecase.UpdateSimulationUseCase

/**
 * Dependency Injection container providing application-wide singletons and use cases.
 */
object AppContainer {

    val levelRepository: LevelRepository by lazy {
        FakeLevelRepository()
    }

    val gameEngine: GameEngine by lazy {
        GameEngine()
    }

    val getLevelUseCase: GetLevelUseCase by lazy {
        GetLevelUseCase(levelRepository)
    }

    val rotateCellUseCase: RotateCellUseCase by lazy {
        RotateCellUseCase(gameEngine)
    }

    val moveCellUseCase: MoveCellUseCase by lazy {
        MoveCellUseCase(gameEngine)
    }

    val updateSimulationUseCase: UpdateSimulationUseCase by lazy {
        UpdateSimulationUseCase(gameEngine)
    }

    val resetLevelUseCase: ResetLevelUseCase by lazy {
        ResetLevelUseCase(gameEngine)
    }
}
