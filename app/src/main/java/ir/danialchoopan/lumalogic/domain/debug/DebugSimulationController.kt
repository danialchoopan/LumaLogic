package ir.danialchoopan.lumalogic.domain.debug

import ir.danialchoopan.lumalogic.domain.engine.LightTraceResult
import ir.danialchoopan.lumalogic.domain.model.BeamAction
import ir.danialchoopan.lumalogic.domain.model.BeamEvent

/**
 * Controller managing step-by-step debug visualization of Light Trace Engine BeamEvents.
 */
class DebugSimulationController {
    private var events: List<BeamEvent> = emptyList()
    private var currentIndex: Int = -1

    fun loadTraceResult(traceResult: LightTraceResult) {
        this.events = traceResult.beamEvents
        this.currentIndex = if (events.isNotEmpty()) 0 else -1
    }

    fun start(): DebugSimulationState {
        currentIndex = if (events.isNotEmpty()) 0 else -1
        return buildState(isRunning = events.isNotEmpty(), isPaused = false)
    }

    fun stepForward(): DebugSimulationState {
        if (events.isEmpty()) return buildState()
        if (currentIndex < events.size - 1) {
            currentIndex++
        }
        val isEnd = currentIndex >= events.size - 1
        return buildState(isRunning = !isEnd, isPaused = isEnd, isFinished = isEnd)
    }

    fun stepBackward(): DebugSimulationState {
        if (events.isEmpty()) return buildState()
        if (currentIndex > 0) {
            currentIndex--
        }
        return buildState(isRunning = false, isPaused = true, isFinished = false)
    }

    fun pause(): DebugSimulationState {
        return buildState(isRunning = false, isPaused = true)
    }

    fun resume(): DebugSimulationState {
        return buildState(isRunning = true, isPaused = false)
    }

    fun reset(): DebugSimulationState {
        currentIndex = if (events.isNotEmpty()) 0 else -1
        return buildState(isRunning = false, isPaused = true, isFinished = false)
    }

    fun finish(): DebugSimulationState {
        if (events.isNotEmpty()) {
            currentIndex = events.size - 1
        }
        return buildState(isRunning = false, isPaused = true, isFinished = true)
    }

    fun getCurrentState(): DebugSimulationState {
        return buildState()
    }

    private fun buildState(
        isRunning: Boolean = false,
        isPaused: Boolean = true,
        isFinished: Boolean = currentIndex >= events.size - 1 && events.isNotEmpty()
    ): DebugSimulationState {
        if (events.isEmpty() || currentIndex < 0 || currentIndex >= events.size) {
            return DebugSimulationState(
                totalSteps = events.size,
                events = events,
                isRunning = false,
                isPaused = true,
                isFinished = events.isEmpty()
            )
        }

        val evt = events[currentIndex]
        val visited = events.take(currentIndex + 1).map { it.position }.toSet()

        val stopActions = setOf(BeamAction.LOOP_STOP, BeamAction.OUT_OF_BOUNDS, BeamAction.FILTER_BLOCK)
        val isStopped = evt.action in stopActions || currentIndex == events.size - 1

        return DebugSimulationState(
            currentStep = currentIndex + 1,
            totalSteps = events.size,
            events = events,
            currentEvent = evt,
            isRunning = isRunning && !isStopped,
            isPaused = isPaused || isStopped,
            isFinished = isStopped,
            currentBeamPosition = evt.position,
            currentDirection = evt.direction,
            currentColor = evt.color,
            visitedPositions = visited
        )
    }
}
