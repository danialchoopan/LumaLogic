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
    val maximumEnergy: Int = 50,
    val energyConfig: EnergyConfig = EnergyConfig(maxEnergy = maximumEnergy),
    val threeStarThreshold: Int = 1200,
    val twoStarThreshold: Int = 800,
    val expectedMoves: Int = 10,
    val tags: List<String> = emptyList(),
    val isUserCreated: Boolean = false,
    val isLocked: Boolean = false,
    val targetRequirements: List<TargetRequirement> = emptyList()
) {
    val id: String get() = levelId
}
