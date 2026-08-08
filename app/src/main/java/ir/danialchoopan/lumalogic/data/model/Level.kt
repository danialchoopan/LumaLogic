package ir.danialchoopan.lumalogic.data.model

import com.squareup.moshi.JsonClass

/**
 * Level model representing a puzzle grid state and metadata.
 */
@JsonClass(generateAdapter = true)
data class Level(
    val levelId: String,
    val name: String,
    val rows: Int,
    val columns: Int,
    val cells: List<Cell>,
    val difficulty: String = "Beginner",
    val targetRequirements: List<TargetRequirement> = emptyList()
)
