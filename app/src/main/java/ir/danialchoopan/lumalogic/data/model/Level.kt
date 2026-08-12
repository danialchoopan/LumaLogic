package ir.danialchoopan.lumalogic.data.model

import com.squareup.moshi.JsonClass

/**
 * Expanded Level model representing a puzzle grid state and metadata.
 */
@JsonClass(generateAdapter = true)
data class Level(
    val levelId: String,
    val name: String,
    val description: String = "",
    val author: String = "LumaLogic",
    val version: Int = 1,
    val schemaVersion: Int = 1,
    val rows: Int = 9,
    val columns: Int = 9,
    val cells: List<Cell> = emptyList(),
    val difficulty: String = "Medium",
    val requiredEnergy: Int = 0,
    val tags: List<String> = emptyList(),
    val isUserCreated: Boolean = false,
    val targetRequirements: List<TargetRequirement> = emptyList()
) {
    val id: String get() = levelId
}
