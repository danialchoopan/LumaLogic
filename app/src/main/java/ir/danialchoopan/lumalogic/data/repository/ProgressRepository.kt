package ir.danialchoopan.lumalogic.data.repository

import ir.danialchoopan.lumalogic.data.model.LevelProgress

/**
 * Interface for persisting level progress records.
 */
interface ProgressRepository {
    fun getProgress(levelId: String): LevelProgress?
    fun getAllProgress(): List<LevelProgress>
    fun saveProgress(progress: LevelProgress)
    fun clearProgress()
}
