package ir.danialchoopan.lumalogic.data.model

import com.squareup.moshi.JsonClass

/**
 * Model representing target requirement criteria for level completion.
 */
@JsonClass(generateAdapter = true)
data class TargetRequirement(
    val position: Position,
    val requiredColor: LightColor? = null,
    val isOptional: Boolean = false
)
