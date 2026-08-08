package ir.danialchoopan.lumalogic.domain.engine

import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.domain.model.BeamEvent
import ir.danialchoopan.lumalogic.domain.model.BeamSegment
import ir.danialchoopan.lumalogic.domain.model.BeamState
import ir.danialchoopan.lumalogic.domain.model.GateState

/**
 * Result data structure returned by the Light Trace Engine after simulation.
 */
data class LightTraceResult(
    val path: List<Position>,
    val visitedCells: Set<Position>,
    val activatedTargets: Set<Position>,
    val success: Boolean,
    val energyUsed: Int,
    val stoppedReason: StopReason,
    val beamSegments: List<BeamSegment> = emptyList(),
    val litCells: Set<Position> = visitedCells,
    val terminatedBeams: List<BeamState> = emptyList(),
    val gateStates: Map<Position, GateState> = emptyMap(),
    val filterEvents: List<BeamEvent> = emptyList(),
    val splitEvents: List<BeamEvent> = emptyList(),
    val beamEvents: List<BeamEvent> = emptyList()
)
