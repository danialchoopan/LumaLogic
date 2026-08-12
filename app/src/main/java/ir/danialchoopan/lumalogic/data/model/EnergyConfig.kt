package ir.danialchoopan.lumalogic.data.model

import com.squareup.moshi.JsonClass

/**
 * Configuration parameters for level energy consumption and costs.
 */
@JsonClass(generateAdapter = true)
data class EnergyConfig(
    val maxEnergy: Int = 50,
    val cellTraversalCost: Int = 1,
    val mirrorCost: Int = 1,
    val splitterCost: Int = 2,
    val filterCost: Int = 1,
    val gateCost: Int = 2
)
