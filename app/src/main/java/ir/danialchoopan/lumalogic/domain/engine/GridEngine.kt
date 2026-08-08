package ir.danialchoopan.lumalogic.domain.engine

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.Direction
import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.data.model.Rotation
import ir.danialchoopan.lumalogic.domain.model.BeamAction
import ir.danialchoopan.lumalogic.domain.model.BeamEvent
import ir.danialchoopan.lumalogic.domain.model.BeamSegment
import ir.danialchoopan.lumalogic.domain.model.BeamState
import ir.danialchoopan.lumalogic.domain.model.GateState

/**
 * GridEngine responsible for multi-beam light movement simulation across a grid.
 * Fully decoupled from Jetpack Compose / Android UI layer.
 */
class GridEngine {

    companion object {
        const val MAX_STEPS = 1000
    }

    /**
     * Converts a Cell's Rotation to its initial emission Direction for SOURCE cells.
     */
    fun getSourceDirection(rotation: Rotation): Direction {
        return when (rotation) {
            Rotation.ZERO -> Direction.UP
            Rotation.NINETY -> Direction.RIGHT
            Rotation.ONE_EIGHTY -> Direction.DOWN
            Rotation.TWO_SEVENTY -> Direction.LEFT
        }
    }

    /**
     * Traces light starting from all Source cells and active Gates across the grid.
     */
    fun traceLight(rows: Int, columns: Int, cells: List<Cell>): LightTraceResult {
        val cellMap = cells.associateBy { Position(it.row, it.column) }
        val sources = cells.filter { it.type == CellType.SOURCE }
        val gates = cells.filter { it.type == CellType.GATE }

        if (sources.isEmpty() && gates.none { it.gateType == ir.danialchoopan.lumalogic.data.model.GateType.NOT }) {
            return LightTraceResult(
                path = emptyList(),
                visitedCells = emptySet(),
                activatedTargets = emptySet(),
                success = false,
                energyUsed = 0,
                stoppedReason = StopReason.NO_SOURCE
            )
        }

        val requiredTargets = cells
            .filter { it.type == CellType.TARGET && !it.isOptionalTarget }
            .map { Position(it.row, it.column) }
            .toSet()

        val allTargets = cells
            .filter { it.type == CellType.TARGET }
            .map { Position(it.row, it.column) }
            .toSet()

        val targetMap = cells
            .filter { it.type == CellType.TARGET }
            .associateBy { Position(it.row, it.column) }

        val queue = ArrayDeque<BeamState>()
        val loopDetector = LoopDetector()

        val visitedCells = mutableSetOf<Position>()
        val activatedTargets = mutableSetOf<Position>()
        val fullPath = mutableListOf<Position>()
        val beamSegments = mutableListOf<BeamSegment>()
        val terminatedBeams = mutableListOf<BeamState>()
        val beamEvents = mutableListOf<BeamEvent>()
        val filterEvents = mutableListOf<BeamEvent>()
        val splitEvents = mutableListOf<BeamEvent>()
        val gateStatesMap = mutableMapOf<Position, GateState>()

        // Initialize logic gate initial states
        for (gateCell in gates) {
            val pos = Position(gateCell.row, gateCell.column)
            val gType = gateCell.gateType ?: ir.danialchoopan.lumalogic.data.model.GateType.AND
            gateStatesMap[pos] = GateLogic.createInitialState(gType)
        }

        // Add initial beams from sources
        for ((idx, source) in sources.withIndex()) {
            val startPos = Position(source.row, source.column)
            val dir = getSourceDirection(source.rotation)
            val color = source.lightColor ?: LightColor.WHITE
            val branchId = "source_${idx}"

            queue.addLast(
                BeamState(
                    position = startPos,
                    direction = dir,
                    color = color,
                    sourceId = source.id,
                    branchId = branchId
                )
            )
            visitedCells.add(startPos)
            fullPath.add(startPos)
        }

        // Add initial beams from NOT gates (NOT gate outputs TRUE when no input)
        for (gateCell in gates) {
            val pos = Position(gateCell.row, gateCell.column)
            val state = gateStatesMap[pos]
            if (state?.output == true) {
                val outDir = GateLogic.getOutputDirection(state.gateType, gateCell.rotation)
                val color = gateCell.lightColor ?: LightColor.WHITE
                val branchId = "gate_init_${pos.row}_${pos.column}"

                queue.addLast(
                    BeamState(
                        position = pos,
                        direction = outDir,
                        color = color,
                        sourceId = gateCell.id,
                        branchId = branchId
                    )
                )
                visitedCells.add(pos)
                fullPath.add(pos)
            }
        }

        var stepCounter = 0
        var finalStopReason = StopReason.OUT_OF_BOUNDS

        while (queue.isNotEmpty() && stepCounter < MAX_STEPS) {
            val currentBeam = queue.removeFirst()
            stepCounter++

            val currPos = currentBeam.position
            val currDir = currentBeam.direction
            val currColor = currentBeam.color
            val branchId = currentBeam.branchId

            val moveEvent = BeamEvent(
                step = stepCounter,
                position = currPos,
                direction = currDir,
                color = currColor,
                componentType = cellMap[currPos]?.type,
                action = BeamAction.MOVE
            )
            beamEvents.add(moveEvent)

            val nextPos = when (currDir) {
                Direction.UP -> Position(currPos.row - 1, currPos.column)
                Direction.DOWN -> Position(currPos.row + 1, currPos.column)
                Direction.LEFT -> Position(currPos.row, currPos.column - 1)
                Direction.RIGHT -> Position(currPos.row, currPos.column + 1)
            }

            // Check grid boundary
            if (nextPos.row !in 0 until rows || nextPos.column !in 0 until columns) {
                beamEvents.add(
                    BeamEvent(
                        step = stepCounter,
                        position = nextPos,
                        direction = currDir,
                        color = currColor,
                        componentType = null,
                        action = BeamAction.OUT_OF_BOUNDS
                    )
                )
                terminatedBeams.add(currentBeam)
                finalStopReason = StopReason.OUT_OF_BOUNDS
                continue
            }

            // Check loop detection
            if (loopDetector.isLoop(nextPos, currDir, currColor, branchId)) {
                beamEvents.add(
                    BeamEvent(
                        step = stepCounter,
                        position = nextPos,
                        direction = currDir,
                        color = currColor,
                        componentType = cellMap[nextPos]?.type,
                        action = BeamAction.LOOP_STOP
                    )
                )
                terminatedBeams.add(currentBeam)
                finalStopReason = StopReason.LOOP_DETECTED
                continue
            }

            visitedCells.add(nextPos)
            fullPath.add(nextPos)
            val segment = BeamSegment(start = currPos, end = nextPos, color = currColor, branchId = branchId)
            beamSegments.add(segment)

            val targetCell = cellMap[nextPos]
            if (targetCell == null || targetCell.type == CellType.EMPTY || targetCell.type == CellType.WIRE || targetCell.type == CellType.SOURCE) {
                // Continue straight
                queue.addLast(
                    currentBeam.copy(
                        position = nextPos,
                        energy = currentBeam.energy - 1
                    )
                )
                continue
            }

            when (targetCell.type) {
                CellType.BLOCK -> {
                    terminatedBeams.add(currentBeam)
                    finalStopReason = StopReason.BLOCKED
                }

                CellType.TARGET -> {
                    val reqColor = targetCell.requiredColor
                    if (FilterLogic.shouldPass(currColor, reqColor)) {
                        activatedTargets.add(nextPos)
                        beamEvents.add(
                            BeamEvent(
                                step = stepCounter,
                                position = nextPos,
                                direction = currDir,
                                color = currColor,
                                componentType = CellType.TARGET,
                                action = BeamAction.TARGET_ACTIVATE
                            )
                        )
                    }
                    terminatedBeams.add(currentBeam)
                }

                CellType.MIRROR -> {
                    val mirrorType = MirrorLogic.getMirrorType(targetCell.rotation)
                    val newDir = MirrorLogic.reflect(currDir, mirrorType)

                    beamEvents.add(
                        BeamEvent(
                            step = stepCounter,
                            position = nextPos,
                            direction = newDir,
                            color = currColor,
                            componentType = CellType.MIRROR,
                            action = BeamAction.REFLECT
                        )
                    )

                    queue.addLast(
                        currentBeam.copy(
                            position = nextPos,
                            direction = newDir,
                            energy = currentBeam.energy - 1
                        )
                    )
                }

                CellType.SPLITTER -> {
                    val (dir1, dir2) = SplitterLogic.splitBeam(currDir, targetCell.rotation)
                    val splitEvt = BeamEvent(
                        step = stepCounter,
                        position = nextPos,
                        direction = currDir,
                        color = currColor,
                        componentType = CellType.SPLITTER,
                        action = BeamAction.SPLIT
                    )
                    beamEvents.add(splitEvt)
                    splitEvents.add(splitEvt)

                    val bA = "${branchId}_A_${stepCounter}"
                    val bB = "${branchId}_B_${stepCounter}"

                    queue.addLast(
                        currentBeam.copy(
                            position = nextPos,
                            direction = dir1,
                            branchId = bA,
                            energy = currentBeam.energy - 1
                        )
                    )
                    queue.addLast(
                        currentBeam.copy(
                            position = nextPos,
                            direction = dir2,
                            branchId = bB,
                            energy = currentBeam.energy - 1
                        )
                    )
                }

                CellType.FILTER -> {
                    val accColor = targetCell.acceptedColor
                    if (FilterLogic.shouldPass(currColor, accColor)) {
                        val passEvt = BeamEvent(
                            step = stepCounter,
                            position = nextPos,
                            direction = currDir,
                            color = currColor,
                            componentType = CellType.FILTER,
                            action = BeamAction.FILTER_PASS
                        )
                        beamEvents.add(passEvt)
                        filterEvents.add(passEvt)

                        queue.addLast(
                            currentBeam.copy(
                                position = nextPos,
                                energy = currentBeam.energy - 1
                            )
                        )
                    } else {
                        val blockEvt = BeamEvent(
                            step = stepCounter,
                            position = nextPos,
                            direction = currDir,
                            color = currColor,
                            componentType = CellType.FILTER,
                            action = BeamAction.FILTER_BLOCK
                        )
                        beamEvents.add(blockEvt)
                        filterEvents.add(blockEvt)
                        terminatedBeams.add(currentBeam)
                        finalStopReason = StopReason.FILTER_BLOCKED
                    }
                }

                CellType.GATE -> {
                    val gType = targetCell.gateType ?: ir.danialchoopan.lumalogic.data.model.GateType.AND
                    val prevState = gateStatesMap[nextPos] ?: GateLogic.createInitialState(gType)
                    val newState = GateLogic.processInput(prevState, currDir, targetCell.rotation)
                    gateStatesMap[nextPos] = newState

                    beamEvents.add(
                        BeamEvent(
                            step = stepCounter,
                            position = nextPos,
                            direction = currDir,
                            color = currColor,
                            componentType = CellType.GATE,
                            action = BeamAction.GATE_INPUT
                        )
                    )

                    if (newState.output && !prevState.output) {
                        val outDir = GateLogic.getOutputDirection(newState.gateType, targetCell.rotation)
                        val gateBranch = "gate_${nextPos.row}_${nextPos.column}_${stepCounter}"
                        val outColor = targetCell.lightColor ?: currColor

                        beamEvents.add(
                            BeamEvent(
                                step = stepCounter,
                                position = nextPos,
                                direction = outDir,
                                color = outColor,
                                componentType = CellType.GATE,
                                action = BeamAction.GATE_OUTPUT
                            )
                        )

                        queue.addLast(
                            BeamState(
                                position = nextPos,
                                direction = outDir,
                                color = outColor,
                                sourceId = targetCell.id,
                                branchId = gateBranch
                            )
                        )
                    } else {
                        terminatedBeams.add(currentBeam)
                    }
                }

                else -> {
                    queue.addLast(
                        currentBeam.copy(
                            position = nextPos,
                            energy = currentBeam.energy - 1
                        )
                    )
                }
            }
        }

        if (stepCounter >= MAX_STEPS) {
            finalStopReason = StopReason.MAX_STEPS_REACHED
        }

        val effectiveTargets = if (requiredTargets.isNotEmpty()) requiredTargets else allTargets
        val success = effectiveTargets.isNotEmpty() && activatedTargets.containsAll(effectiveTargets)

        if (success) {
            finalStopReason = StopReason.TARGET_REACHED
        }

        return LightTraceResult(
            path = fullPath,
            visitedCells = visitedCells,
            activatedTargets = activatedTargets,
            success = success,
            energyUsed = beamSegments.size,
            stoppedReason = finalStopReason,
            beamSegments = beamSegments,
            litCells = visitedCells,
            terminatedBeams = terminatedBeams,
            gateStates = gateStatesMap,
            filterEvents = filterEvents,
            splitEvents = splitEvents,
            beamEvents = beamEvents
        )
    }

    /**
     * Updates the cell list, marking cells as lit if visited by the light trace.
     */
    fun updateGridWithTrace(cells: List<Cell>, traceResult: LightTraceResult): List<Cell> {
        return cells.map { cell ->
            val pos = Position(cell.row, cell.column)
            val isLit = pos in traceResult.visitedCells
            cell.copy(isLit = isLit)
        }
    }
}
