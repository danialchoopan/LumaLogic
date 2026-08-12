package ir.danialchoopan.lumalogic.domain.hint

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.data.model.Rotation
import ir.danialchoopan.lumalogic.domain.engine.GridEngine
import ir.danialchoopan.lumalogic.domain.engine.StopReason

/**
 * Independent HintEngine analyzing level state and generating actionable hints without Compose dependencies.
 */
class HintEngine(
    private val gridEngine: GridEngine = GridEngine()
) {

    /**
     * Evaluates current cells and generates an actionable hint.
     */
    fun analyzeLevel(rows: Int, columns: Int, cells: List<Cell>): Hint {
        val currentTrace = gridEngine.traceLight(rows, columns, cells)

        if (currentTrace.success) {
            return Hint(
                type = HintType.GENERAL,
                message = "The puzzle is already solved! All required targets are activated.",
                priority = 100
            )
        }

        // 1. Check for specific failure reasons first (e.g., Filter blocked)
        if (currentTrace.stoppedReason == StopReason.FILTER_BLOCKED && currentTrace.filterEvents.isNotEmpty()) {
            val blockedEvent = currentTrace.filterEvents.lastOrNull { it.action == ir.danialchoopan.lumalogic.domain.model.BeamAction.FILTER_BLOCK }
            if (blockedEvent != null) {
                return Hint(
                    type = HintType.COLOR,
                    position = blockedEvent.position,
                    message = "A filter at Row ${blockedEvent.position.row + 1}, Column ${blockedEvent.position.column + 1} is blocking the ${blockedEvent.color} beam. Redirect a matching beam color to pass through.",
                    priority = 90,
                    suggestedAction = "Change beam color or bypass filter"
                )
            }
        }

        // 2. Evaluate candidate rotations on movable/unlocked components
        val movableCells = cells.filter { !it.isLocked && it.type != CellType.EMPTY && it.type != CellType.BLOCK && it.type != CellType.SOURCE && it.type != CellType.TARGET }

        var bestHint: Hint? = null
        var maxScore = -1

        for (cell in movableCells) {
            val pos = Position(cell.row, cell.column)

            // Try 3 alternative rotations
            var nextRot = cell.rotation.next()
            repeat(3) {
                val candidateCells = cells.map { c ->
                    if (c.row == cell.row && c.column == cell.column) c.copy(rotation = nextRot) else c
                }

                val candTrace = gridEngine.traceLight(rows, columns, candidateCells)
                var score = 0

                if (candTrace.success) {
                    score = 1000
                } else {
                    val newlyActivated = candTrace.activatedTargets.size - currentTrace.activatedTargets.size
                    val newVisitedCount = candTrace.visitedCells.size - currentTrace.visitedCells.size
                    score += newlyActivated * 200 + newVisitedCount * 10
                }

                if (score > maxScore && score > 0) {
                    maxScore = score
                    val msg = "Rotate the ${cell.type.name.lowercase().replaceFirstChar { it.uppercase() }} at Row ${pos.row + 1}, Column ${pos.column + 1} to align the light beam."
                    bestHint = Hint(
                        type = HintType.ROTATE,
                        position = pos,
                        message = msg,
                        priority = score,
                        suggestedAction = "Rotate ${cell.type.name} at (${pos.row + 1}, ${pos.column + 1})"
                    )
                }

                nextRot = nextRot.next()
            }
        }

        // 3. Evaluate candidate moves (moving a component to an adjacent empty cell)
        val emptyPositions = cells.filter { it.type == CellType.EMPTY }.map { Position(it.row, it.column) }
        for (cell in movableCells) {
            if (cell.type == CellType.MIRROR || cell.type == CellType.SPLITTER || cell.type == CellType.FILTER || cell.type == CellType.WIRE) {
                val fromPos = Position(cell.row, cell.column)

                for (toPos in emptyPositions) {
                    // Check if adjacent for lightweight search limit
                    if (kotlin.math.abs(fromPos.row - toPos.row) + kotlin.math.abs(fromPos.column - toPos.column) <= 2) {
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
                            score = 1200
                        } else {
                            val newlyActivated = candTrace.activatedTargets.size - currentTrace.activatedTargets.size
                            score += newlyActivated * 200
                        }

                        if (score > maxScore && score > 0) {
                            maxScore = score
                            val msg = "Move the ${cell.type.name.lowercase().replaceFirstChar { it.uppercase() }} from Row ${fromPos.row + 1}, Column ${fromPos.column + 1} to Row ${toPos.row + 1}, Column ${toPos.column + 1}."
                            bestHint = Hint(
                                type = HintType.MOVE,
                                position = fromPos,
                                targetPosition = toPos,
                                message = msg,
                                priority = score,
                                suggestedAction = "Move ${cell.type.name} to (${toPos.row + 1}, ${toPos.column + 1})"
                            )
                        }
                    }
                }
            }
        }

        if (bestHint != null) {
            return bestHint!!
        }

        // 4. Check logic gates
        val gateCells = cells.filter { it.type == CellType.GATE }
        for (gate in gateCells) {
            val gatePos = Position(gate.row, gate.column)
            val state = currentTrace.gateStates[gatePos]
            if (state != null && !state.output) {
                return Hint(
                    type = HintType.GATE,
                    position = gatePos,
                    message = "The ${gate.gateType ?: "Logic"} Gate at Row ${gatePos.row + 1}, Column ${gatePos.column + 1} requires required active inputs to emit light.",
                    priority = 50,
                    suggestedAction = "Guide light to gate input ports"
                )
            }
        }

        // Fallback General Hint
        return Hint(
            type = HintType.GENERAL,
            message = "Adjust mirrors or splitters to redirect active light beams towards unlit targets.",
            priority = 10,
            suggestedAction = "Check beam directions"
        )
    }
}
