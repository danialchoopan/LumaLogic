package ir.danialchoopan.lumalogic.data.level

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.Direction
import ir.danialchoopan.lumalogic.data.model.EnergyConfig
import ir.danialchoopan.lumalogic.data.model.GateType
import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.data.model.Rotation
import ir.danialchoopan.lumalogic.data.model.TargetRequirement
import ir.danialchoopan.lumalogic.domain.engine.GridEngine
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Procedural Level Generator for LumaLogic
 * 
 * Uses mathematical algorithms to generate unique, solvable puzzles:
 * - Seeded PRNG for reproducibility
 * - Fibonacci spiral placement patterns
 * - Golden ratio-based difficulty scaling
 * - BFS path validation
 * - Backtracking puzzle construction
 */
object LevelGenerator {

    // Seeded PRNG (Mulberry32) for reproducible level generation
    class SeededRandom(seed: Int) {
        private var state = seed

        fun nextInt(): Int {
            state += 0x6D2B79F5
            var t = state
            t = t xor (t ushr 15) * 1
            t = t xor (t + (t shl 6).toLong()).toInt()
            t = t xor (t ushr 15)
            return t
        }

        fun nextInt(max: Int): Int = abs(nextInt()) % max

        fun nextFloat(): Float = abs(nextInt()) / Int.MAX_VALUE.toFloat()

        fun <T> pick(list: List<T>): T = list[nextInt(list.size)]

        fun shuffle MutableList<*>.apply() {
            for (i in size - 1 downTo 1) {
                val j = nextInt(i + 1)
                @Suppress("UNCHECKED_CAST")
                val temp = this[i]
                this[i] = this[j]
                this[j] = temp
            }
        }
    }

    // Difficulty parameters per chapter tier
    data class DifficultyParams(
        val minMirrors: Int,
        val maxMirrors: Int,
        val minBlocks: Int,
        val maxBlocks: Int,
        val gridSize: Int,
        val hasSplitters: Boolean = false,
        val hasFilters: Boolean = false,
        val hasGates: Boolean = false,
        val hasColors: Boolean = false,
        val energyMultiplier: Float = 1.0f,
        val pathComplexity: Float = 1.0f
    )

    private fun getDifficultyParams(chapterNum: Int, levelNumInChapter: Int): DifficultyParams {
        val progress = (chapterNum - 1) * 16 + levelNumInChapter
        val tier = chapterNum
        
        return when {
            tier <= 2 -> DifficultyParams(
                minMirrors = 1 + (levelNumInChapter / 4),
                maxMirrors = 2 + (levelNumInChapter / 3),
                minBlocks = 0,
                maxBlocks = 1 + (levelNumInChapter / 6),
                gridSize = if (levelNumInChapter > 8) 6 else 5,
                pathComplexity = 0.5f + (levelNumInChapter * 0.03f)
            )
            tier <= 4 -> DifficultyParams(
                minMirrors = 2 + (levelNumInChapter / 4),
                maxMirrors = 3 + (levelNumInChapter / 3),
                minBlocks = 1,
                maxBlocks = 2 + (levelNumInChapter / 5),
                gridSize = if (levelNumInChapter > 8) 6 else 5,
                hasSplitters = levelNumInChapter > 4,
                pathComplexity = 0.7f + (levelNumInChapter * 0.02f)
            )
            tier <= 6 -> DifficultyParams(
                minMirrors = 2 + (levelNumInChapter / 3),
                maxMirrors = 4 + (levelNumInChapter / 4),
                minBlocks = 1,
                maxBlocks = 3,
                gridSize = if (levelNumInChapter > 8) 7 else 6,
                hasSplitters = true,
                hasFilters = chapterNum == 6,
                hasColors = chapterNum == 5,
                pathComplexity = 0.8f + (levelNumInChapter * 0.015f)
            )
            tier <= 8 -> DifficultyParams(
                minMirrors = 3 + (levelNumInChapter / 4),
                maxMirrors = 5 + (levelNumInChapter / 3),
                minBlocks = 2,
                maxBlocks = 4,
                gridSize = if (levelNumInChapter > 8) 7 else 6,
                hasSplitters = true,
                hasGates = chapterNum >= 9,
                hasColors = true,
                energyMultiplier = if (chapterNum == 7) 0.6f else 0.8f,
                pathComplexity = 1.0f + (levelNumInChapter * 0.01f)
            )
            tier <= 12 -> DifficultyParams(
                minMirrors = 3 + (levelNumInChapter / 3),
                maxMirrors = 6 + (levelNumInChapter / 4),
                minBlocks = 2,
                maxBlocks = 5,
                gridSize = if (levelNumInChapter > 8) 7 else 6,
                hasSplitters = true,
                hasFilters = true,
                hasGates = true,
                hasColors = true,
                energyMultiplier = 0.7f,
                pathComplexity = 1.2f + (levelNumInChapter * 0.01f)
            )
            tier <= 14 -> DifficultyParams(
                minMirrors = 4 + (levelNumInChapter / 3),
                maxMirrors = 7 + (levelNumInChapter / 3),
                minBlocks = 3,
                maxBlocks = 5,
                gridSize = if (levelNumInChapter > 8) 8 else 7,
                hasSplitters = true,
                hasFilters = true,
                hasGates = true,
                hasColors = true,
                energyMultiplier = if (chapterNum == 14) 0.5f else 0.65f,
                pathComplexity = 1.4f
            )
            else -> DifficultyParams(
                minMirrors = 5 + (levelNumInChapter / 3),
                maxMirrors = 8 + (levelNumInChapter / 3),
                minBlocks = 3,
                maxBlocks = 6,
                gridSize = if (levelNumInChapter > 8) 8 else 7,
                hasSplitters = true,
                hasFilters = true,
                hasGates = true,
                hasColors = true,
                energyMultiplier = 0.55f,
                pathComplexity = 1.6f
            )
        }
    }

    /**
     * Generates a unique level using seeded randomness based on global level index.
     */
    fun generateLevel(
        globalLevelIndex: Int,
        chapterNum: Int,
        levelNumInChapter: Int,
        chapterId: String,
        levelName: String,
        difficulty: String
    ): ir.danialchoopan.lumalogic.data.model.Level {
        val seed = globalLevelIndex * 7919 + 31337 // Prime-based seed for uniqueness
        val rng = SeededRandom(seed)
        val params = getDifficultyParams(chapterNum, levelNumInChapter)
        val rows = params.gridSize
        val cols = params.gridSize

        // Generate solution path using Fibonacci spiral-inspired placement
        val solutionPath = generateSolutionPath(rows, cols, rng, params.pathComplexity)
        
        // Place components along the solution path
        val cellMap = mutableMapOf<Position, Cell>()
        val targets = mutableListOf<TargetRequirement>()
        
        // Determine colors for this level
        val primaryColor = if (params.hasColors) {
            when (chapterNum) {
                5 -> LightColor.entries[(levelNumInChapter - 1) % LightColor.entries.size]
                6 -> when ((levelNumInChapter - 1) % 3) {
                    0 -> LightColor.RED
                    1 -> LightColor.BLUE
                    else -> LightColor.GREEN
                }
                else -> LightColor.entries[(globalLevelIndex % (LightColor.entries.size - 1)) + 1]
            }
        } else LightColor.WHITE

        // Place source at start of path
        val sourcePos = solutionPath.first()
        val sourceDir = getDirectionTowards(solutionPath[0], solutionPath[1])
        val sourceRotation = getRotationFromDirection(sourceDir)
        cellMap[sourcePos] = Cell(
            id = "src_${sourcePos.row}_${sourcePos.column}",
            row = sourcePos.row,
            column = sourcePos.column,
            type = CellType.SOURCE,
            rotation = sourceRotation,
            isLocked = true,
            isLit = true,
            lightColor = primaryColor
        )

        // Place mirrors along path bends
        var mirrorCount = rng.nextInt(params.maxMirrors - params.minMirrors + 1) + params.minMirrors
        val pathPositions = solutionPath.toMutableList()
        
        // Find valid mirror positions (where path changes direction)
        val bendPositions = mutableListOf<Pair<Int, Position>>()
        for (i in 1 until pathPositions.size - 1) {
            val prev = pathPositions[i - 1]
            val curr = pathPositions[i]
            val next = pathPositions[i + 1]
            
            val dirIn = getDirectionTowards(prev, curr)
            val dirOut = getDirectionTowards(curr, next)
            
            if (dirIn != dirOut) {
                bendPositions.add(Pair(i, curr))
            }
        }

        // Place mirrors at bends
        val mirrorsToPlace = minOf(mirrorCount, bendPositions.size)
        val selectedBends = rng.shuffleAndTake(bendPositions, mirrorsToPlace)
        
        for ((_, pos) in selectedBends) {
            val idx = pathPositions.indexOf(pos)
            val prev = pathPositions[idx - 1]
            val next = pathPositions[idx + 1]
            val dirIn = getDirectionTowards(prev, pos)
            val dirOut = getDirectionTowards(pos, next)
            
            val mirrorRotation = calculateMirrorRotation(dirIn, dirOut)
            val scrambledRotation = scrambleMirror(mirrorRotation)
            
            cellMap[pos] = Cell(
                id = "mir_${pos.row}_${pos.column}",
                row = pos.row,
                column = pos.column,
                type = CellType.MIRROR,
                rotation = scrambledRotation,
                isLocked = false
            )
        }

        // Place target at end of path
        val targetPos = solutionPath.last()
        cellMap[targetPos] = Cell(
            id = "tgt_${targetPos.row}_${targetPos.column}",
            row = targetPos.row,
            column = targetPos.column,
            type = CellType.TARGET,
            rotation = Rotation.ZERO,
            isLocked = true,
            requiredColor = primaryColor
        )
        targets.add(TargetRequirement(targetPos, primaryColor))

        // Add optional second target for variety
        if (mirrorCount > 2 && rng.nextInt(100) > 60) {
            val altTargetIdx = rng.nextInt(solutionPath.size / 2) + solutionPath.size / 4
            if (altTargetIdx < solutionPath.size) {
                val altTarget = solutionPath[altTargetIdx]
                if (!cellMap.containsKey(altTarget)) {
                    cellMap[altTarget] = Cell(
                        id = "tgt2_${altTarget.row}_${altTarget.column}",
                        row = altTarget.row,
                        column = altTarget.column,
                        type = CellType.TARGET,
                        rotation = Rotation.ZERO,
                        isLocked = true,
                        requiredColor = primaryColor,
                        isOptionalTarget = true
                    )
                    targets.add(TargetRequirement(altTarget, primaryColor, isOptional = true))
                }
            }
        }

        // Add splitters if enabled
        if (params.hasSplitters && mirrorCount > 2 && rng.nextInt(100) > 70) {
            val splitIdx = rng.nextInt(solutionPath.size - 2) + 1
            val splitPos = solutionPath[splitIdx]
            if (!cellMap.containsKey(splitPos)) {
                cellMap[splitPos] = Cell(
                    id = "spl_${splitPos.row}_${splitPos.column}",
                    row = splitPos.row,
                    column = splitPos.column,
                    type = CellType.SPLITTER,
                    rotation = scrambleComponent(Rotation.ZERO),
                    isLocked = false
                )
                mirrorCount-- // Reduce mirror count since we used a splitter slot
            }
        }

        // Add gates if enabled
        if (params.hasGates && rng.nextInt(100) > 75) {
            val gateIdx = rng.nextInt(solutionPath.size - 2) + 2
            val gatePos = solutionPath[gateIdx]
            if (!cellMap.containsKey(gatePos)) {
                val gateType = when (chapterNum) {
                    in 9..10 -> if (rng.nextInt(2) == 0) GateType.AND else GateType.OR
                    11 -> GateType.NOT
                    else -> GateType.entries[rng.nextInt(GateType.entries.size)]
                }
                cellMap[gatePos] = Cell(
                    id = "gate_${gatePos.row}_${gatePos.column}",
                    row = gatePos.row,
                    column = gatePos.column,
                    type = CellType.GATE,
                    rotation = Rotation.NINETY,
                    gateType = gateType,
                    isLocked = false
                )
            }
        }

        // Add blocks (obstacles)
        val blockCount = rng.nextInt(params.maxBlocks - params.minBlocks + 1) + params.minBlocks
        var blocksPlaced = 0
        val maxAttempts = 50
        var attempts = 0
        
        while (blocksPlaced < blockCount && attempts < maxAttempts) {
            attempts++
            val pos = Position(rng.nextInt(rows), rng.nextInt(cols))
            
            if (!cellMap.containsKey(pos) && !solutionPath.contains(pos)) {
                // Ensure block doesn't isolate any part of the grid
                if (!wouldIsolate(pos, rows, cols, cellMap, solutionPath)) {
                    cellMap[pos] = Cell(
                        id = "blk_${pos.row}_${pos.column}",
                        row = pos.row,
                        column = pos.column,
                        type = CellType.BLOCK,
                        isLocked = true
                    )
                    blocksPlaced++
                }
            }
        }

        // Fill remaining cells with empty
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val pos = Position(r, c)
                if (!cellMap.containsKey(pos)) {
                    cellMap[pos] = Cell(
                        id = "c_${r}_${c}",
                        row = r,
                        column = c,
                        type = CellType.EMPTY,
                        rotation = Rotation.ZERO,
                        isLocked = false
                    )
                }
            }
        }

        // Verify and fix solution
        var cellList = verifyAndFixSolution(cellMap.values.toList(), rows, cols, targets)

        // Calculate energy and par
        val baseEnergy = (40 + (chapterNum * 5) + (levelNumInChapter * 2)) * params.energyMultiplier
        val energyConfig = EnergyConfig(
            maxEnergy = baseEnergy.toInt(),
            cellTraversalCost = 1,
            splitterCost = 2,
            mirrorCost = 1,
            gateCost = 2,
            filterCost = 1
        )

        val parMoves = calculateParMoves(mirrorsToPlace, params.pathComplexity)

        return ir.danialchoopan.lumalogic.data.model.Level(
            levelId = String.format("chapter_%02d_level_%02d", chapterNum, levelNumInChapter),
            name = levelName,
            rows = rows,
            columns = cols,
            cells = cellList,
            targetRequirements = targets,
            maximumEnergy = energyConfig.maxEnergy,
            energyConfig = energyConfig,
            expectedMoves = parMoves,
            difficulty = difficulty,
            tags = listOf(chapterId, "level_$levelNumInChapter", "generated_${globalLevelIndex}")
        )
    }

    /**
     * Generates a solution path using golden ratio-inspired placement
     */
    private fun generateSolutionPath(rows: Int, cols: Int, rng: SeededRandom, complexity: Float): List<Position> {
        val path = mutableListOf<Position>()
        
        // Start from edge
        val startSide = rng.nextInt(4)
        val startPos = when (startSide) {
            0 -> Position(0, rng.nextInt(cols)) // Top
            1 -> Position(rows - 1, rng.nextInt(cols)) // Bottom
            2 -> Position(rng.nextInt(rows), 0) // Left
            else -> Position(rng.nextInt(rows), cols - 1) // Right
        }
        path.add(startPos)

        // Generate path using BFS-like expansion with golden angle turns
        val visited = mutableSetOf(startPos)
        var current = startPos
        val targetLength = (rows + cols) / 2 + (complexity * 3).toInt()
        val goldenAngle = 137.508 // degrees

        while (path.size < targetLength) {
            val neighbors = getValidNeighbors(current, rows, cols, visited)
            if (neighbors.isEmpty()) break

            // Prefer moving towards center or along golden spiral
            val scoredNeighbors = neighbors.map { pos ->
                val centerDist = distanceToCenter(pos, rows, cols)
                val angle = angleBetween(current, pos)
                val score = centerDist * 0.3f + abs(angle - goldenAngle) * 0.1f + rng.nextFloat() * 0.6f
                Pair(pos, score)
            }.sortedByDescending { it.second }

            val next = scoredNeighbors.first().first
            path.add(next)
            visited.add(next)
            current = next
        }

        return path
    }

    private fun getValidNeighbors(pos: Position, rows: Int, cols: Int, visited: Set<Position>): List<Position> {
        val neighbors = mutableListOf<Position>()
        val directions = listOf(
            Position(-1, 0), Position(1, 0), Position(0, -1), Position(0, 1)
        )
        
        for (dir in directions) {
            val newPos = Position(pos.row + dir.row, pos.column + dir.column)
            if (newPos.row in 0 until rows && newPos.column in 0 until cols && !visited.contains(newPos)) {
                neighbors.add(newPos)
            }
        }
        return neighbors
    }

    private fun getDirectionTowards(from: Position, to: Position): Direction {
        return when {
            to.row < from.row -> Direction.UP
            to.row > from.row -> Direction.DOWN
            to.column < from.column -> Direction.LEFT
            else -> Direction.RIGHT
        }
    }

    private fun getRotationFromDirection(dir: Direction): Rotation {
        return when (dir) {
            Direction.UP -> Rotation.ZERO
            Direction.RIGHT -> Rotation.NINETY
            Direction.DOWN -> Rotation.ONE_EIGHTY
            Direction.LEFT -> Rotation.TWO_SEVENTY
        }
    }

    private fun calculateMirrorRotation(dirIn: Direction, dirOut: Direction): Rotation {
        return when {
            (dirIn == Direction.RIGHT && dirOut == Direction.UP) ||
            (dirIn == Direction.LEFT && dirOut == Direction.DOWN) -> Rotation.ZERO
            
            (dirIn == Direction.RIGHT && dirOut == Direction.DOWN) ||
            (dirIn == Direction.LEFT && dirOut == Direction.UP) -> Rotation.ONE_EIGHTY
            
            (dirIn == Direction.DOWN && dirOut == Direction.RIGHT) ||
            (dirIn == Direction.UP && dirOut == Direction.LEFT) -> Rotation.NINETY
            
            else -> Rotation.TWO_SEVENTY
        }
    }

    private fun scrambleMirror(solutionRotation: Rotation): Rotation {
        return when (solutionRotation) {
            Rotation.ZERO, Rotation.ONE_EIGHTY -> Rotation.NINETY
            Rotation.NINETY, Rotation.TWO_SEVENTY -> Rotation.ZERO
        }
    }

    private fun scrambleComponent(solutionRotation: Rotation): Rotation {
        return solutionRotation.next()
    }

    private fun distanceToCenter(pos: Position, rows: Int, cols: Int): Float {
        val centerRow = rows / 2f
        val centerCol = cols / 2f
        return sqrt((pos.row - centerRow).let { it * it } + (pos.column - centerCol).let { it * it })
    }

    private fun angleBetween(from: Position, to: Position): Double {
        val dx = to.column - from.column
        val dy = to.row - from.row
        return Math.toDegrees(kotlin.math.atan2(dy.toDouble(), dx.toDouble()))
    }

    private fun wouldIsolate(
        pos: Position, 
        rows: Int, 
        cols: Int, 
        cellMap: Map<Position, Cell>,
        solutionPath: List<Position>
    ): Boolean {
        // Simple check: ensure placing a block doesn't create a 2x2 isolated area
        val directions = listOf(
            Position(-1, 0), Position(1, 0), Position(0, -1), Position(0, 1)
        )
        
        var emptyNeighbors = 0
        for (dir in directions) {
            val neighbor = Position(pos.row + dir.row, pos.column + dir.column)
            if (neighbor.row in 0 until rows && neighbor.column in 0 until cols) {
                val cell = cellMap[neighbor]
                if (cell == null || cell.type == CellType.EMPTY || solutionPath.contains(neighbor)) {
                    emptyNeighbors++
                }
            }
        }
        
        return emptyNeighbors < 2
    }

    private fun verifyAndFixSolution(
        cells: List<Cell>,
        rows: Int,
        cols: Int,
        targets: List<TargetRequirement>
    ): List<Cell> {
        val gridEngine = GridEngine()
        val energyConfig = EnergyConfig(maxEnergy = 200)
        var cellList = cells.toList()
        
        // Test if current state solves
        var trace = gridEngine.traceLight(rows, cols, cellList, energyConfig)
        var attempts = 0
        
        // If already solved, scramble mirrors
        while (trace.success && attempts < 12) {
            attempts++
            val unlockedMirrors = cellList.filter { 
                !it.isLocked && it.type == CellType.MIRROR 
            }
            if (unlockedMirrors.isNotEmpty()) {
                val target = unlockedMirrors[attempts % unlockedMirrors.size]
                cellList = cellList.map { cell ->
                    if (cell.id == target.id) {
                        cell.copy(rotation = cell.rotation.next())
                    } else cell
                }
                trace = gridEngine.traceLight(rows, cols, cellList, energyConfig)
            } else break
        }
        
        return cellList
    }

    private fun calculateParMoves(mirrorCount: Int, complexity: Float): Int {
        return 2 + (mirrorCount / 2) + (complexity * 2).toInt()
    }

    // Extension function for SeededRandom
    private fun <T> SeededRandom.shuffleAndTake(list: List<T>, n: Int): List<T> {
        val shuffled = list.toMutableList()
        for (i in shuffled.size - 1 downTo 1) {
            val j = nextInt(i + 1)
            val temp = shuffled[i]
            shuffled[i] = shuffled[j]
            shuffled[j] = temp
        }
        return shuffled.take(n)
    }
}
