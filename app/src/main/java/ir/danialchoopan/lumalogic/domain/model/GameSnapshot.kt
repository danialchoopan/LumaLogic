package ir.danialchoopan.lumalogic.domain.model

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.domain.engine.LightTraceResult

/**
 * Immutable snapshot representing complete game state at a specific point in time.
 */
data class GameSnapshot(
    val cells: List<Cell>,
    val rows: Int,
    val columns: Int,
    val lightTraceResult: LightTraceResult? = null,
    val activatedTargets: Set<Position> = emptySet(),
    val gateStates: Map<Position, GateState> = emptyMap()
)
