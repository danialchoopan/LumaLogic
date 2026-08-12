package ir.danialchoopan.lumalogic.di

import ir.danialchoopan.lumalogic.data.repository.FakeLevelRepository
import ir.danialchoopan.lumalogic.data.repository.LevelRepository
import ir.danialchoopan.lumalogic.data.repository.LocalProgressRepository
import ir.danialchoopan.lumalogic.data.repository.LocalUserLevelRepository
import ir.danialchoopan.lumalogic.data.repository.ProgressRepository
import ir.danialchoopan.lumalogic.data.repository.UserLevelRepository
import ir.danialchoopan.lumalogic.data.serialization.LevelJsonSerializer
import ir.danialchoopan.lumalogic.domain.engine.GameEngine
import ir.danialchoopan.lumalogic.domain.level.LevelManager
import ir.danialchoopan.lumalogic.domain.level.LevelProgressManager
import ir.danialchoopan.lumalogic.domain.level.LevelValidator
import ir.danialchoopan.lumalogic.domain.usecase.GetLevelUseCase
import ir.danialchoopan.lumalogic.domain.usecase.MoveCellUseCase
import ir.danialchoopan.lumalogic.domain.usecase.ResetLevelUseCase
import ir.danialchoopan.lumalogic.domain.usecase.RotateCellUseCase
import ir.danialchoopan.lumalogic.domain.usecase.UpdateSimulationUseCase

/**
 * Dependency Injection container providing application-wide singletons and repositories.
 */
object AppContainer {

    val userLevelRepository: UserLevelRepository by lazy {
        LocalUserLevelRepository()
    }

    val progressRepository: ProgressRepository by lazy {
        LocalProgressRepository()
    }

    val levelRepository: LevelRepository by lazy {
        FakeLevelRepository(userLevelRepository)
    }

    val levelValidator: LevelValidator by lazy {
        LevelValidator()
    }

    val levelJsonSerializer: LevelJsonSerializer by lazy {
        LevelJsonSerializer(levelValidator)
    }

    val levelManager: LevelManager by lazy {
        LevelManager(levelRepository, levelValidator, levelJsonSerializer)
    }

    val levelProgressManager: LevelProgressManager by lazy {
        LevelProgressManager(progressRepository)
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
