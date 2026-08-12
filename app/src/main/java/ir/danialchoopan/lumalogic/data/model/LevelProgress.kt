package ir.danialchoopan.lumalogic.data.model

import com.squareup.moshi.JsonClass

/**
 * Persisted progress state for a level.
 */
@JsonClass(generateAdapter = true)
data class LevelProgress(
    val levelId: String,
    val completed: Boolean = false,
    val bestScore: Int = 0,
    val bestTimeSeconds: Long = 0L,
    val hintsUsed: Int = 0,
    val attempts: Int = 0,
    val completedAt: Long = 0L
)
