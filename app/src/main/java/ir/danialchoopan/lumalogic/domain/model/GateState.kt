package ir.danialchoopan.lumalogic.domain.model

import com.squareup.moshi.JsonClass
import ir.danialchoopan.lumalogic.data.model.GateType

/**
 * State of a logic gate component during grid simulation.
 */
@JsonClass(generateAdapter = true)
data class GateState(
    val gateType: GateType,
    val inputA: Boolean = false,
    val inputB: Boolean = false,
    val output: Boolean = false
)
