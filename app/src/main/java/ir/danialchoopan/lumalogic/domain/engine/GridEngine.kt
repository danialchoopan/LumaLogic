package ir.danialchoopan.lumalogic.domain.engine

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.Direction
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.data.model.Rotation

/**
 * GridEngine responsible for light movement simulation across a grid.
 *
 * Fully decoupled from Jetpack Compose / Android UI layer.
 */
class GridEngine {

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
     * Traces light starting from all Source cells across the grid.
     */
    fun traceLight(rows: Int, columns: Int, cells: List<Cell>): LightTraceResult {
        val cellMap = cells.associateBy { Position(it.row, it.column) }

        val sources = cells.filter { it.type == CellType.SOURCE }
        if (sources.isEmpty()) {
            return LightTraceResult(
                path = emptyList(),
                visitedCells = emptySet(),
                activatedTargets = emptySet(),
                success = false,
                energyUsed = 0,
                stoppedReason = StopReason.NO_SOURCE
            )
        }

        val allTargets = cells.filter { it.type == CellType.TARGET }
            .map { Position(it.row, it.column) }
            .toSet()

        val fullPath = mutableListOf<Position>()
        val visitedCells = mutableSetOf<Position>()
        val activatedTargets = mutableSetOf<Position>()
        val visitedStates = mutableSetOf<Pair<Position, Direction>>()

        var finalStoppedReason = StopReason.OUT_OF_BOUNDS

        for (source in sources) {
            val startPos = Position(source.row, source.column)
            var currDir = getSourceDirection(source.rotation)
            var currPos = startPos

            fullPath.add(startPos)
            visitedCells.add(startPos)
            visitedStates.add(Pair(startPos, currDir))

            while (true) {
                val nextPos = when (currDir) {
                    Direction.UP -> Position(currPos.row - 1, currPos.column)
                    Direction.DOWN -> Position(currPos.row + 1, currPos.column)
                    Direction.LEFT -> Position(currPos.row, currPos.column - 1)
                    Direction.RIGHT -> Position(currPos.row, currPos.column + 1)
                }

                // Check out of bounds
                if (nextPos.row !in 0 until rows || nextPos.column !in 0 until columns) {
                    finalStoppedReason = StopReason.OUT_OF_BOUNDS
                    break
                }

                // Check loop detection
                val statePair = Pair(nextPos, currDir)
                if (visitedStates.contains(statePair)) {
                    finalStoppedReason = StopReason.LOOP_DETECTED
                    break
                }
                visitedStates.add(statePair)

                fullPath.add(nextPos)
                visitedCells.add(nextPos)

                val targetCell = cellMap[nextPos]
                if (targetCell != null) {
                    when (targetCell.type) {
                        CellType.BLOCK -> {
                            finalStoppedReason = StopReason.BLOCKED
                            break
                        }

                        CellType.TARGET -> {
                            activatedTargets.add(nextPos)
                            if (allTargets.isNotEmpty() && activatedTargets.containsAll(allTargets)) {
                                finalStoppedReason = StopReason.TARGET_REACHED
                                break
                            }
                        }

                        CellType.MIRROR -> {
                            val mirrorType = MirrorLogic.getMirrorType(targetCell.rotation)
                            currDir = MirrorLogic.reflect(currDir, mirrorType)
                        }

                        CellType.EMPTY, CellType.WIRE, CellType.SOURCE, CellType.SPLITTER, CellType.FILTER, CellType.GATE -> {
                            // Light continues straight in current direction
                        }
                    }
                }

                currPos = nextPos
            }
        }

        val success = allTargets.isNotEmpty() && activatedTargets.containsAll(allTargets)
        if (success) {
            finalStoppedReason = StopReason.TARGET_REACHED
        }

        return LightTraceResult(
            path = fullPath,
            visitedCells = visitedCells,
            activatedTargets = activatedTargets,
            success = success,
            energyUsed = (fullPath.size - 1).coerceAtLeast(0),
            stoppedReason = finalStoppedReason
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
