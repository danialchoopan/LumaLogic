package ir.danialchoopan.lumalogic.di

import android.content.Context
import ir.danialchoopan.lumalogic.data.repository.AchievementRepository
import ir.danialchoopan.lumalogic.data.repository.ChapterRepository
import ir.danialchoopan.lumalogic.data.repository.DailyPuzzleRepository
import ir.danialchoopan.lumalogic.data.repository.DefaultChapterRepository
import ir.danialchoopan.lumalogic.data.repository.DefaultDailyPuzzleRepository
import ir.danialchoopan.lumalogic.data.repository.FakeLevelRepository
import ir.danialchoopan.lumalogic.data.repository.FavoriteLevelRepository
import ir.danialchoopan.lumalogic.data.repository.LevelRepository
import ir.danialchoopan.lumalogic.data.repository.LocalAchievementRepository
import ir.danialchoopan.lumalogic.data.repository.LocalFavoriteLevelRepository
import ir.danialchoopan.lumalogic.data.repository.LocalProgressRepository
import ir.danialchoopan.lumalogic.data.repository.LocalUserLevelRepository
import ir.danialchoopan.lumalogic.data.repository.ProgressRepository
import ir.danialchoopan.lumalogic.data.repository.SettingsRepository
import ir.danialchoopan.lumalogic.data.repository.UserLevelRepository
import ir.danialchoopan.lumalogic.data.serialization.LevelJsonSerializer
import ir.danialchoopan.lumalogic.domain.audio.GameAudioManager
import ir.danialchoopan.lumalogic.domain.engine.GameEngine
import ir.danialchoopan.lumalogic.domain.haptic.GameHapticManager
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

    private var appContext: Context? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    val settingsRepository: SettingsRepository by lazy {
        val context = appContext ?: error("AppContainer must be initialized with Context in MainActivity")
        SettingsRepository(context)
    }

    val audioManager: GameAudioManager by lazy {
        GameAudioManager().apply {
            isSoundEnabled = settingsRepository.settings.value.soundEnabled
        }
    }

    val hapticManager: GameHapticManager by lazy {
        val context = appContext ?: error("AppContainer must be initialized with Context in MainActivity")
        GameHapticManager(context).apply {
            isHapticsEnabled = settingsRepository.settings.value.hapticsEnabled
        }
    }

    val userLevelRepository: UserLevelRepository by lazy {
        LocalUserLevelRepository(appContext?.filesDir)
    }

    val progressRepository: ProgressRepository by lazy {
        LocalProgressRepository(appContext?.filesDir)
    }

    val chapterRepository: ChapterRepository by lazy {
        DefaultChapterRepository()
    }

    val favoriteLevelRepository: FavoriteLevelRepository by lazy {
        LocalFavoriteLevelRepository(appContext?.filesDir)
    }

    val achievementRepository: AchievementRepository by lazy {
        LocalAchievementRepository(appContext?.filesDir)
    }

    val dailyPuzzleRepository: DailyPuzzleRepository by lazy {
        DefaultDailyPuzzleRepository()
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

