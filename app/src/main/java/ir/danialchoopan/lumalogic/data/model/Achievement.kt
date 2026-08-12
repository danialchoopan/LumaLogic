package ir.danialchoopan.lumalogic.data.model

import com.squareup.moshi.JsonClass

/**
 * Model representing an achievement in LumaLogic.
 */
@JsonClass(generateAdapter = true)
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String = "EmojiEvents",
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L,
    val progress: Float = 0f, // 0.0f to 1.0f
    val targetCount: Int = 1,
    val currentCount: Int = 0
)
