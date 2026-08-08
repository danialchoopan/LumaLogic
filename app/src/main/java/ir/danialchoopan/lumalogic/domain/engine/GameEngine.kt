package ir.danialchoopan.lumalogic.domain.engine

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.Level
import ir.danialchoopan.lumalogic.data.model.Rotation

import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.domain.usecase.MoveResult

/**
 * Core Game Engine responsible for grid state and light tracing logic.
 *
 * NOTE: GameEngine MUST NEVER depend on Android/Compose classes.
 */
class GameEngine {

    private var currentLevel: Level? = null
    private var activeCells: MutableList<Cell> = mutableListOf()
    private val gridEngine = GridEngine()
    private var lastTraceResult: LightTraceResult? = null

    fun initialize() {
        activeCells.clear()
        currentLevel = null
        lastTraceResult = null
    }

    fun loadLevel(level: Level) {
        currentLevel = level
        activeCells = level.cells.toMutableList()
        simulate()
    }

    fun simulate(): LightTraceResult {
        val level = currentLevel
        if (level == null) {
            val emptyResult = LightTraceResult(
                path = emptyList(),
                visitedCells = emptySet(),
                activatedTargets = emptySet(),
                success = false,
                energyUsed = 0,
                stoppedReason = StopReason.NO_SOURCE
            )
            lastTraceResult = emptyResult
            return emptyResult
        }

        val traceResult = gridEngine.traceLight(level.rows, level.columns, activeCells)
        activeCells = gridEngine.updateGridWithTrace(activeCells, traceResult).toMutableList()
        lastTraceResult = traceResult
        return traceResult
    }

    fun reset(): List<Cell> {
        currentLevel?.let { level ->
            activeCells = level.cells.map { cell ->
                if (!cell.isLocked) {
                    cell.copy(rotation = Rotation.ZERO, isLit = false)
                } else {
                    cell
                }
            }.toMutableList()
            simulate()
        }
        return getGrid()
    }

    fun getGrid(): List<Cell> {
        return activeCells.toList()
    }

    fun getLastTraceResult(): LightTraceResult? {
        return lastTraceResult
    }

    fun rotateCell(cellId: String): List<Cell> {
        val index = activeCells.indexOfFirst { it.id == cellId }
        if (index != -1) {
            val cell = activeCells[index]
            if (!cell.isLocked) {
                activeCells[index] = cell.copy(rotation = cell.rotation.next())
                simulate()
            }
        }
        return getGrid()
    }

    fun rotateCellAt(position: Position): List<Cell> {
        val index = activeCells.indexOfFirst { it.row == position.row && it.column == position.column }
        if (index != -1) {
            val cell = activeCells[index]
            if (!cell.isLocked) {
                activeCells[index] = cell.copy(rotation = cell.rotation.next())
                simulate()
            }
        }
        return getGrid()
    }

    fun isMovable(cell: Cell): Boolean {
        if (cell.isLocked) return false
        return when (cell.type) {
            CellType.MIRROR, CellType.WIRE, CellType.SPLITTER, CellType.FILTER -> true
            else -> false
        }
    }

    fun moveCell(from: Position, to: Position): MoveResult {
        val level = currentLevel ?: return MoveResult.Failure("No active level")

        if (to.row !in 0 until level.rows || to.column !in 0 until level.columns) {
            return MoveResult.Failure("Destination is outside grid bounds")
        }

        if (from == to) {
            return MoveResult.Failure("Destination is same as source")
        }

        val fromIndex = activeCells.indexOfFirst { it.row == from.row && it.column == from.column }
        val toIndex = activeCells.indexOfFirst { it.row == to.row && it.column == to.column }

        if (fromIndex == -1 || toIndex == -1) {
            return MoveResult.Failure("Invalid cell position")
        }

        val fromCell = activeCells[fromIndex]
        val toCell = activeCells[toIndex]

        if (!isMovable(fromCell)) {
            return MoveResult.Failure("Sources, Targets, and Blocks cannot be moved")
        }

        if (toCell.type != CellType.EMPTY) {
            return MoveResult.Failure("Destination cell is occupied")
        }

        val newToCell = fromCell.copy(row = to.row, column = to.column)
        val newFromCell = Cell(
            id = "empty_${from.row}_${from.column}",
            row = from.row,
            column = from.column,
            type = CellType.EMPTY,
            rotation = Rotation.ZERO,
            isLocked = false,
            isLit = false
        )

        activeCells[fromIndex] = newFromCell
        activeCells[toIndex] = newToCell

        simulate()
        return MoveResult.Success(getGrid())
    }
}

