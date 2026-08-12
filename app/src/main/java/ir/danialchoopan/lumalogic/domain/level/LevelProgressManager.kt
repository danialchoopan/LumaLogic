package ir.danialchoopan.lumalogic.domain.level

import ir.danialchoopan.lumalogic.data.model.LevelProgress
import ir.danialchoopan.lumalogic.data.repository.ProgressRepository

/**
 * Domain service managing level completion records, scores, and statistics.
 */
class LevelProgressManager(
    private val progressRepository: ProgressRepository
) {

    fun getProgress(levelId: String): LevelProgress? {
        return progressRepository.getProgress(levelId)
    }

    fun getAllProgress(): List<LevelProgress> {
        return progressRepository.getAllProgress()
    }

    fun recordLevelCompletion(levelId: String, score: Int, timeSeconds: Long, hintsUsed: Int) {
        val current = progressRepository.getProgress(levelId)
        val attempts = (current?.attempts ?: 0) + 1
        val newRecord = LevelProgress(
            levelId = levelId,
            completed = true,
            bestScore = score,
            bestTimeSeconds = timeSeconds,
            hintsUsed = hintsUsed,
            attempts = attempts,
            completedAt = System.currentTimeMillis()
        )
        progressRepository.saveProgress(newRecord)
    }

    fun isLevelCompleted(levelId: String): Boolean {
        return progressRepository.getProgress(levelId)?.completed == true
    }
}
