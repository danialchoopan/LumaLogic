package ir.danialchoopan.lumalogic.domain.hint

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.Direction
import ir.danialchoopan.lumalogic.data.model.GateType
import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.data.model.Rotation
import ir.danialchoopan.lumalogic.domain.engine.GridEngine
import ir.danialchoopan.lumalogic.domain.engine.StopReason
import kotlin.math.abs

/**
 * Intelligent HintEngine analyzing puzzle state and generating actionable, accurate hints.
 */
class HintEngine(
    private val gridEngine: GridEngine = GridEngine()
) {

    /**
     * Evaluates current cells and generates an actionable hint with structured metadata.
     */
    fun analyzeLevel(rows: Int, columns: Int, cells: List<Cell>): Hint {
        val currentTrace = gridEngine.traceLight(rows, columns, cells)

        if (currentTrace.success) {
            return Hint(
                type = HintType.GENERAL,
                message = "The puzzle is already solved! All required targets are activated.",
                priority = 100,
                suggestedAction = "Proceed to next level"
            )
        }

        // 1. Check for specific failure reasons first (e.g., Filter blocked)
        if (currentTrace.stoppedReason == StopReason.FILTER_BLOCKED && currentTrace.filterEvents.isNotEmpty()) {
            val blockedEvent = currentTrace.filterEvents.lastOrNull { it.action == ir.danialchoopan.lumalogic.domain.model.BeamAction.FILTER_BLOCK }
            if (blockedEvent != null) {
                val filterCell = cells.find { it.row == blockedEvent.position.row && it.column == blockedEvent.position.column }
                return Hint(
                    type = HintType.COLOR,
                    position = blockedEvent.position,
                    cellType = CellType.FILTER,
                    color = filterCell?.acceptedColor ?: blockedEvent.color,
                    message = "A filter at Row ${blockedEvent.position.row + 1}, Column ${blockedEvent.position.column + 1} is blocking the ${blockedEvent.color.name.lowercase()} beam. Guide a matching beam color to pass through.",
                    priority = 900,
                    suggestedAction = "Redirect matching color to filter"
                )
            }
        }

        val movableCells = cells.filter { 
            !it.isLocked && it.type != CellType.EMPTY && it.type != CellType.BLOCK && it.type != CellType.SOURCE && it.type != CellType.TARGET 
        }

        var bestHint: Hint? = null
        var maxScore = -1

        // 2. 1-Move Rotation Search
        for (cell in movableCells) {
            val pos = Position(cell.row, cell.column)
            var nextRot = cell.rotation.next()

            repeat(3) {
                val candidateCells = cells.map { c ->
                    if (c.row == cell.row && c.column == cell.column) c.copy(rotation = nextRot) else c
                }

                val candTrace = gridEngine.traceLight(rows, columns, candidateCells)
                var score = 0

                if (candTrace.success) {
                    score = 5000
                } else {
                    val newlyActivated = candTrace.activatedTargets.size - currentTrace.activatedTargets.size
                    val newVisitedCount = candTrace.visitedCells.size - currentTrace.visitedCells.size
                    val newlyPoweredGates = candTrace.gateStates.values.count { it.output } - currentTrace.gateStates.values.count { it.output }
                    score = newlyActivated * 600 + newlyPoweredGates * 250 + newVisitedCount * 10
                }

                if (score > maxScore && score > 0) {
                    maxScore = score
                    val componentName = cell.type.name.lowercase().replaceFirstChar { it.uppercase() }
                    val msg = if (candTrace.success) {
                        "Rotate the $componentName at Row ${pos.row + 1}, Column ${pos.column + 1} to complete the puzzle!"
                    } else {
                        "Rotate the $componentName at Row ${pos.row + 1}, Column ${pos.column + 1} to advance the light beam."
                    }

                    bestHint = Hint(
                        type = HintType.ROTATE,
                        position = pos,
                        cellType = cell.type,
                        targetRotation = nextRot,
                        message = msg,
                        priority = score,
                        suggestedAction = "Rotate $componentName at (${pos.row + 1}, ${pos.column + 1})"
                    )
                }

                nextRot = nextRot.next()
            }
        }

        // If a 1-move win or target-hit was found, return immediately
        if (bestHint != null && maxScore >= 500) {
            return bestHint
        }

        // 3. 2-Move Rotation Search (Lookahead for multi-step solution)
        if (movableCells.size >= 2) {
            for (i in 0 until movableCells.size) {
                val cellA = movableCells[i]
                for (j in i + 1 until movableCells.size) {
                    val cellB = movableCells[j]

                    var rotA = cellA.rotation.next()
                    repeat(3) {
                        var rotB = cellB.rotation.next()
                        repeat(3) {
                            val candidateCells = cells.map { c ->
                                when {
                                    c.row == cellA.row && c.column == cellA.column -> c.copy(rotation = rotA)
                                    c.row == cellB.row && c.column == cellB.column -> c.copy(rotation = rotB)
                                    else -> c
                                }
                            }

                            val candTrace = gridEngine.traceLight(rows, columns, candidateCells)
                            if (candTrace.success) {
                                val compNameA = cellA.type.name.lowercase().replaceFirstChar { it.uppercase() }
                                return Hint(
                                    type = HintType.ROTATE,
                                    position = Position(cellA.row, cellA.column),
                                    cellType = cellA.type,
                                    targetRotation = rotA,
                                    message = "Step 1: Rotate the $compNameA at Row ${cellA.row + 1}, Column ${cellA.column + 1} towards solving the puzzle.",
                                    priority = 2000,
                                    suggestedAction = "Rotate $compNameA at (${cellA.row + 1}, ${cellA.column + 1})"
                                )
                            }
                            rotB = rotB.next()
                        }
                        rotA = rotA.next()
                    }
                }
            }
        }

        // 4. Candidate Moves (moving movable components to empty cells)
        val emptyPositions = cells.filter { it.type == CellType.EMPTY }.map { Position(it.row, it.column) }
        for (cell in movableCells) {
            if (cell.type == CellType.MIRROR || cell.type == CellType.SPLITTER || cell.type == CellType.FILTER || cell.type == CellType.WIRE) {
                val fromPos = Position(cell.row, cell.column)

                for (toPos in emptyPositions) {
                    if (abs(fromPos.row - toPos.row) + abs(fromPos.column - toPos.column) <= 2) {
                        val candidateCells = cells.map { c ->
                            when {
                                c.row == fromPos.row && c.column == fromPos.column -> c.copy(type = CellType.EMPTY)
                                c.row == toPos.row && c.column == toPos.column -> cell.copy(row = toPos.row, column = toPos.column)
                                else -> c
                            }
                        }

                        val candTrace = gridEngine.traceLight(rows, columns, candidateCells)
                        var score = 0

                        if (candTrace.success) {
                            score = 3000
                        } else {
                            val newlyActivated = candTrace.activatedTargets.size - currentTrace.activatedTargets.size
                            score = newlyActivated * 500
                        }

                        if (score > maxScore && score > 0) {
                            maxScore = score
                            val compName = cell.type.name.lowercase().replaceFirstChar { it.uppercase() }
                            val msg = "Move the $compName from Row ${fromPos.row + 1}, Column ${fromPos.column + 1} to Row ${toPos.row + 1}, Column ${toPos.column + 1}."
                            bestHint = Hint(
                                type = HintType.MOVE,
                                position = fromPos,
                                targetPosition = toPos,
                                cellType = cell.type,
                                message = msg,
                                priority = score,
                                suggestedAction = "Move $compName to (${toPos.row + 1}, ${toPos.column + 1})"
                            )
                        }
                    }
                }
            }
        }

        if (bestHint != null) {
            return bestHint
        }

        // 5. Logic Gates Inactivity Diagnosis
        val gateCells = cells.filter { it.type == CellType.GATE }
        for (gate in gateCells) {
            val gatePos = Position(gate.row, gate.column)
            val state = currentTrace.gateStates[gatePos]
            val gType = gate.gateType ?: GateType.AND
            if (state != null && !state.output) {
                val inputDesc = when (gType) {
                    GateType.AND -> "both input ports"
                    GateType.OR -> "at least one input port"
                    GateType.NOT -> "control input"
                }
                return Hint(
                    type = HintType.GATE,
                    position = gatePos,
                    cellType = CellType.GATE,
                    gateType = gType,
                    message = "The ${gType.name} Gate at Row ${gatePos.row + 1}, Column ${gatePos.column + 1} requires light on $inputDesc to emit output.",
                    priority = 100,
                    suggestedAction = "Supply light to ${gType.name} gate inputs"
                )
            }
        }

        // 6. Proximity to Unlit Targets Diagnosis
        val unlitTargets = cells.filter { it.type == CellType.TARGET && !currentTrace.activatedTargets.contains(Position(it.row, it.column)) }
        if (unlitTargets.isNotEmpty()) {
            val firstUnlit = unlitTargets.first()
            val targetPos = Position(firstUnlit.row, firstUnlit.column)
            return Hint(
                type = HintType.GENERAL,
                position = targetPos,
                cellType = CellType.TARGET,
                color = firstUnlit.requiredColor,
                message = "The target at Row ${targetPos.row + 1}, Column ${targetPos.column + 1} is not receiving light. Route a beam to this position.",
                priority = 50,
                suggestedAction = "Direct beam towards Row ${targetPos.row + 1}, Column ${targetPos.column + 1}"
            )
        }

        // Fallback General Hint
        return Hint(
            type = HintType.GENERAL,
            message = "Rotate or adjust mirrors and splitters to redirect active light beams towards targets.",
            priority = 10,
            suggestedAction = "Adjust optical components"
        )
    }
}
