package ir.danialchoopan.lumalogic.data.model

import com.squareup.moshi.JsonClass

/**
 * Metadata for a level chapter in LumaLogic.
 */
@JsonClass(generateAdapter = true)
data class Chapter(
    val id: String,
    val number: Int,
    val name: String,
    val subtitle: String,
    val description: String,
    val difficulty: String,
    val requiredStarsToUnlock: Int = 0,
    val levelCount: Int = 16,
    val iconName: String = "Lightbulb",
    val accentColorHex: String = "#FFC107"
)
