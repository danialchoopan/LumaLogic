package ir.danialchoopan.lumalogic.data.model

import com.squareup.moshi.JsonClass

/**
 * Summary of player achievements, progress, and performance statistics.
 */
@JsonClass(generateAdapter = true)
data class PlayerStats(
    val totalLevelsCompleted: Int = 0,
    val totalLevels: Int = 256,
    val totalStars: Int = 0,
    val maxStars: Int = 768,
    val totalScore: Long = 0L,
    val bestScore: Int = 0,
    val totalPlayTimeSeconds: Long = 0L,
    val hintsUsed: Int = 0,
    val chaptersCompleted: Int = 0,
    val favoriteCount: Int = 0,
    val achievementsUnlocked: Int = 0,
    val totalAchievements: Int = 11,
    val completionPercentage: Float = 0f
)
