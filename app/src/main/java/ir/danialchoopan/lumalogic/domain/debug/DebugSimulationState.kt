package ir.danialchoopan.lumalogic.domain.debug

import ir.danialchoopan.lumalogic.data.model.Direction
import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.domain.model.BeamEvent

/**
 * State snapshot for Debug step-by-step Light Trace Engine simulation mode.
 */
data class DebugSimulationState(
    val currentStep: Int = 0,
    val totalSteps: Int = 0,
    val events: List<BeamEvent> = emptyList(),
    val currentEvent: BeamEvent? = null,
    val isRunning: Boolean = false,
    val isPaused: Boolean = true,
    val isFinished: Boolean = false,
    val currentBeamPosition: Position? = null,
    val currentDirection: Direction? = null,
    val currentColor: LightColor? = null,
    val visitedPositions: Set<Position> = emptySet()
)
