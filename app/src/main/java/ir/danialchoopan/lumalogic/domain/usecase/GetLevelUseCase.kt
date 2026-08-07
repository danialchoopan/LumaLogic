package ir.danialchoopan.lumalogic.domain.usecase

import ir.danialchoopan.lumalogic.data.model.Level
import ir.danialchoopan.lumalogic.data.repository.LevelRepository

/**
 * Use case to load levels from repository.
 */
class GetLevelUseCase(private val levelRepository: LevelRepository) {
    operator fun invoke(levelId: String? = null): Level {
        return if (levelId != null) {
            levelRepository.getLevelById(levelId) ?: levelRepository.getDemoLevel()
        } else {
            levelRepository.getDemoLevel()
        }
    }
}
