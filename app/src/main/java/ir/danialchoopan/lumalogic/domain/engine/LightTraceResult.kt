package ir.danialchoopan.lumalogic.domain.engine

import ir.danialchoopan.lumalogic.data.model.Position

/**
 * Result data structure returned by the Light Trace Engine after simulation.
 */
data class LightTraceResult(
    val path: List<Position>,
    val visitedCells: Set<Position>,
    val activatedTargets: Set<Position>,
    val success: Boolean,
    val energyUsed: Int,
    val stoppedReason: StopReason
)
