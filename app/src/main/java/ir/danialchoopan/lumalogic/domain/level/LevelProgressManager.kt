package ir.danialchoopan.lumalogic.domain.level

import ir.danialchoopan.lumalogic.data.level.LevelRegistry
import ir.danialchoopan.lumalogic.data.model.LevelProgress
import ir.danialchoopan.lumalogic.data.model.PlayerStats
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

    fun recordLevelCompletion(
        levelId: String,
        stars: Int,
        score: Int,
        timeSeconds: Long,
        moves: Int,
        hintsUsed: Int
    ) {
        val current = progressRepository.getProgress(levelId)
        val attempts = (current?.attempts ?: 0) + 1
        val bestScore = maxOf(current?.bestScore ?: 0, score)
        val bestStars = maxOf(current?.stars ?: 0, stars)
        val bestTime = if ((current?.bestTimeSeconds ?: 0L) == 0L) timeSeconds else minOf(current!!.bestTimeSeconds, timeSeconds)
        val bestMoves = if ((current?.bestMoves ?: 0) == 0) moves else minOf(current!!.bestMoves, moves)

        val newRecord = LevelProgress(
            levelId = levelId,
            completed = true,
            stars = bestStars,
            bestScore = bestScore,
            bestTimeSeconds = bestTime,
            bestMoves = bestMoves,
            hintsUsed = (current?.hintsUsed ?: 0) + hintsUsed,
            attempts = attempts,
            completedAt = System.currentTimeMillis()
        )
        progressRepository.saveProgress(newRecord)
    }

    fun recordAttempt(levelId: String) {
        val current = progressRepository.getProgress(levelId)
        val attempts = (current?.attempts ?: 0) + 1
        val updated = (current ?: LevelProgress(levelId = levelId)).copy(attempts = attempts)
        progressRepository.saveProgress(updated)
    }

    fun clearAllProgress() {
        progressRepository.clearProgress()
    }

    fun isLevelCompleted(levelId: String): Boolean {
        return progressRepository.getProgress(levelId)?.completed == true
    }

    fun getPlayerStats(achievementsUnlocked: Int = 0, totalAchievements: Int = 11, favoriteCount: Int = 0): PlayerStats {
        val allProgress = progressRepository.getAllProgress().filter { it.completed }
        val totalCompleted = allProgress.size
        val totalStars = allProgress.sumOf { it.stars }
        val totalScore = allProgress.sumOf { it.bestScore.toLong() }
        val maxScore = allProgress.maxOfOrNull { it.bestScore } ?: 0
        val totalTime = allProgress.sumOf { it.bestTimeSeconds }
        val totalHints = allProgress.sumOf { it.hintsUsed }

        // Calculate chapter completion
        var chaptersCompleted = 0
        for (ch in LevelRegistry.chapters) {
            val chLevels = LevelRegistry.getLevelsForChapter(ch.id)
            if (chLevels.all { lvl -> isLevelCompleted(lvl.levelId) }) {
                chaptersCompleted++
            }
        }

        val percentage = if (totalCompleted > 0) (totalCompleted.toFloat() / 256.0f) * 100.0f else 0f

        return PlayerStats(
            totalLevelsCompleted = totalCompleted,
            totalLevels = 256,
            totalStars = totalStars,
            maxStars = 768,
            totalScore = totalScore,
            bestScore = maxScore,
            totalPlayTimeSeconds = totalTime,
            hintsUsed = totalHints,
            chaptersCompleted = chaptersCompleted,
            favoriteCount = favoriteCount,
            achievementsUnlocked = achievementsUnlocked,
            totalAchievements = totalAchievements,
            completionPercentage = percentage
        )
    }
}

