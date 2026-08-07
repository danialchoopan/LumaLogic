package ir.danialchoopan.lumalogic.domain.engine

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.Level
import ir.danialchoopan.lumalogic.data.model.Rotation

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
}

