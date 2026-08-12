package ir.danialchoopan.lumalogic.data.level

import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.Chapter
import ir.danialchoopan.lumalogic.data.model.EnergyConfig
import ir.danialchoopan.lumalogic.data.model.GateType
import ir.danialchoopan.lumalogic.data.model.Level
import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.data.model.Rotation
import ir.danialchoopan.lumalogic.data.model.TargetRequirement

/**
 * Data registry containing definitions for all 16 chapters and 256 levels in LumaLogic.
 */
object LevelRegistry {

    val chapters: List<Chapter> = listOf(
        Chapter("chapter_01", 1, "Light Basics", "The First Light", "Master sources, targets, straight beams, and basic mirror rotation.", "BEGINNER", 0, 16, "Lightbulb", "#FFC107"),
        Chapter("chapter_02", 2, "Reflection", "Reflections", "Explore mirror placement, multiple reflection angles, and path planning.", "EASY", 12, 16, "CompareArrows", "#00BCD4"),
        Chapter("chapter_03", 3, "Precision", "Precision", "Navigate long corridors with move constraints and tight route optimization.", "EASY", 28, 16, "CenterFocusStrong", "#4CAF50"),
        Chapter("chapter_04", 4, "Splitters", "Prism Splitters", "Split single rays into parallel branches targeting multiple receivers.", "NORMAL", 44, 16, "CallSplit", "#3F51B5"),
        Chapter("chapter_05", 5, "Colors", "Spectrum Colors", "Work with RGB colored light sources and color-matching targets.", "NORMAL", 60, 16, "Palette", "#E91E63"),
        Chapter("chapter_06", 6, "Filters", "Chromatic Filters", "Filter light colors to isolate specific wavelengths along the route.", "NORMAL", 76, 16, "FilterAlt", "#9C27B0"),
        Chapter("chapter_07", 7, "Energy", "Energy Thrift", "Route light efficiently under strict energy depletion limits.", "NORMAL", 92, 16, "Bolt", "#FF9800"),
        Chapter("chapter_08", 8, "Multi-Beam", "Multi-Beam Array", "Manage multiple light sources and complex beam intersections.", "HARD", 108, 16, "ViewArray", "#009688"),
        Chapter("chapter_09", 9, "AND Logic", "AND Conjunction", "Activate AND logic gates requiring synchronized dual light inputs.", "HARD", 124, 16, "Input", "#795548"),
        Chapter("chapter_10", 10, "OR Logic", "OR Redundancy", "Use OR gates to build redundant, alternative routing channels.", "HARD", 140, 16, "AltRoute", "#607D8B"),
        Chapter("chapter_11", 11, "NOT Logic", "NOT Inversion", "Invert light signals and master negative logic puzzle mechanics.", "HARD", 156, 16, "Flip", "#8BC34A"),
        Chapter("chapter_12", 12, "Logic Networks", "Logic Networks", "Combine AND, OR, NOT gates with splitters and color filters.", "EXPERT", 172, 16, "AccountTree", "#FF5722"),
        Chapter("chapter_13", 13, "Expert Routing", "Expert Routing", "Tackle expansive grids, tight energy limits, and multi-objective routing.", "EXPERT", 188, 16, "Grid4x4", "#673AB7"),
        Chapter("chapter_14", 14, "Master Energy", "Master Energy", "Extreme energy optimization with zero room for wasted reflections.", "EXPERT", 204, 16, "BatteryChargingFull", "#CDDC39"),
        Chapter("chapter_15", 15, "Expert", "Expert Conundrum", "Deeply challenging puzzles integrating all gameplay mechanics.", "MASTER", 220, 16, "Psychology", "#F44336"),
        Chapter("chapter_16", 16, "LumaLogic Master", "LumaLogic Master", "The ultimate test of optic logic culminating in Level 256.", "MASTER", 236, 16, "AutoAwesome", "#FFD700")
    )

    private val levelNames: List<String> = listOf(
        // Chapter 1 (1..16)
        "First Light", "Linear Ray", "Reflective Corner", "Double Angle", "Prism Alignment", "Straight Beam", "Twin Mirror", "Diagonal Bounce", "Light Way", "Target Sight", "Reflective Path", "Grid Orientation", "Photon Track", "Luma Entry", "Corner Sweep", "Basics Master",
        // Chapter 2 (17..32)
        "First Turn", "Double Reflection", "Corner Loop", "Mirror Maze", "Tri-Fold Ray", "Reflection Wall", "Refraction Point", "ZigZag Light", "Mirror Gate", "Bounce Path", "Parallel Mirrors", "Quad Bounce", "Angle Lock", "Deflection Way", "Optic Switch", "Reflection Master",
        // Chapter 3 (33..48)
        "Tight Turns", "Minimal Moves", "Long Corridor", "Narrow Path", "Surgical Precision", "Target Reach", "Step Optimizer", "Constrained Ray", "Symmetry Point", "Exact Angle", "Mirror Balance", "Calculated Beam", "Critical Step", "Optic Needle", "Focus Ray", "Precision Master",
        // Chapter 4 (49..64)
        "First Split", "Dual Rays", "Branching Light", "Parallel Beams", "Splitter Network", "Twin Targets", "Ray Divider", "Branch Path", "Split Mirror", "Cross Beam", "Quad Branch", "T-Split", "Spectrum Branch", "Branch Circuit", "Multi-Beam Splice", "Splitter Master",
        // Chapter 5 (65..80)
        "Red Dawn", "Blue Ray", "Green Beacon", "Spectrum Bridge", "Color Mixer", "RGB Alignment", "Chroma Path", "Primary Shift", "Hue Junction", "Vibrant Beam", "Tri-Color Target", "Color Routing", "Spectral Wave", "Prismatic View", "Color Harmony", "Color Master",
        // Chapter 6 (81..96)
        "First Filter", "Red Barrier", "Blue Pass", "Green Gate", "Chroma Filter", "Multi-Filter Ray", "Filtered Branch", "Color Selector", "Filter Maze", "Spectral Barrier", "Filter Relay", "Color Sieve", "Dual Filter", "Bandpass Light", "Filter Lock", "Filter Master",
        // Chapter 7 (97..112)
        "First Energy", "Thrifty Ray", "Energy Cell", "Shortest Path", "Power Constraint", "Optimal Route", "Conservation Beam", "Energy Bridge", "Low Power Run", "Efficient Angle", "Thrift Corridor", "Energy Gate", "Minimal Trace", "Battery Saver", "Power Optimization", "Energy Master",
        // Chapter 8 (113..128)
        "Twin Sources", "Dual Channel", "Multi-Ray Matrix", "Intersecting Beams", "Parallel Sources", "Cross Current", "Multi-Target Ray", "Source Array", "Quad Ray", "Complex Intersection", "Beam Divergence", "Source Bridge", "Multi-Source Logic", "Dual Spectrum", "Ray Synthesis", "Multi-Beam Master",
        // Chapter 9 (129..144)
        "First Conjunction", "Dual Activation", "AND Condition", "Logic Pulse", "AND Conjunction", "Double Key", "Synchronized Beam", "AND Gate Bridge", "Dual Signal", "Logic Alignment", "Required Dual", "Conjunction Maze", "Coincidence Light", "Dual Input Target", "Logic Lock", "AND Master",
        // Chapter 10 (145..160)
        "Alternative Route", "Redundant Path", "OR Gate Way", "Flexible Channel", "Dual Route", "Parallel Logic", "OR Junction", "Backup Ray", "Path Choice", "Either Target", "Redundant Ray", "OR Choice", "Dual Stream", "Alternate Switch", "OR Route", "OR Master",
        // Chapter 11 (161..176)
        "Inversion Light", "NOT Condition", "Logic Inverter", "Reverse Beam", "Inverted Path", "Logic Switch", "NOT Conjunction", "Inversion Matrix", "Inverter Gate", "Negative Beam", "Reverse Routing", "NOT Barrier", "Logic Flip", "Inverted Target", "NOT Network", "NOT Master",
        // Chapter 12 (177..192)
        "First Network", "Logic Cascade", "AND-OR Network", "Inverted Conjunction", "Logic Filter Mesh", "Complex Gate", "Multi-Logic Ray", "Network Router", "Logic Matrix", "Cascade Gate", "Interconnected Beams", "Logic Mesh", "Complex Circuit", "Gate Network", "System Conjunction", "Logic Network Master",
        // Chapter 13 (193..208)
        "Large Grid", "Complex Path", "Routing Challenge", "Grid Navigator", "Long Distance Ray", "Obstacle Routing", "Multi-Turn Grid", "Route Optimization", "Dense Matrix", "Extended Beam", "Complex Maze", "Strategic Route", "High Density Grid", "Master Routing", "Grand Navigation", "Expert Routing Master",
        // Chapter 14 (209..224)
        "Tight Limit", "Strict Power", "Energy Constraint", "Minimal Consumption", "Energy Challenge", "Critical Battery", "Power Limit Run", "Optimal Circuit", "Thrift Master", "Energy Squeeze", "Ultra Efficient", "Micro Energy", "Power Reserve", "Peak Conservation", "Energy Final", "Master Energy Master",
        // Chapter 15 (225..240)
        "Grand Conundrum", "Unified Mechanics", "Spectrum Challenge", "Matrix Puzzle", "Complex Conjunction", "Advanced Network", "Master Challenge", "Deep Routing", "Optic Symphony", "Quantum Conundrum", "Ultimate Conjunction", "Prism Master", "Logic Symphony", "Master Mind", "Grand Challenge", "Expert Master",
        // Chapter 16 (241..256)
        "Genesis Ray", "Alpha Light", "Omega Mirror", "Prismatic Genesis", "Logic Pinnacle", "Quantum Array", "Infinite Light", "Final Conjunction", "Optic Convergence", "Ultimate Spectrum", "Luma Citadel", "Cosmic Ray", "Master Key", "Luma Logic Crown", "Zenith Spectrum", "The Ultimate Spectrum"
    )

    private val cachedLevels: List<Level> by lazy {
        buildAll256Levels()
    }

    fun getAllLevels(): List<Level> = cachedLevels

    fun getLevelsForChapter(chapterId: String): List<Level> {
        return cachedLevels.filter { it.tags.contains(chapterId) }
    }

    fun getLevelById(id: String): Level? {
        // Direct match or legacy ID mapping
        val mappedId = when (id) {
            "lumalogic_001" -> "chapter_01_level_01"
            "lumalogic_002" -> "chapter_01_level_02"
            "lumalogic_003" -> "chapter_01_level_03"
            "lumalogic_004" -> "chapter_01_level_04"
            "lumalogic_005" -> "chapter_01_level_05"
            "lumalogic_006" -> "chapter_01_level_06"
            "lumalogic_007" -> "chapter_01_level_07"
            "lumalogic_008" -> "chapter_01_level_08"
            else -> id
        }
        return cachedLevels.find { it.levelId == mappedId }
    }

    private fun buildAll256Levels(): List<Level> {
        val list = mutableListOf<Level>()
        var globalIndex = 0

        for (chIndex in 0 until 16) {
            val chapter = chapters[chIndex]
            val chapterNum = chIndex + 1

            for (lvlIndex in 0 until 16) {
                globalIndex++
                val levelNumInChapter = lvlIndex + 1
                val id = String.format("chapter_%02d_level_%02d", chapterNum, levelNumInChapter)
                val name = levelNames[globalIndex - 1]
                val level = createLevelDefinition(
                    globalLevelIndex = globalIndex,
                    chapterNum = chapterNum,
                    levelNumInChapter = levelNumInChapter,
                    id = id,
                    name = name,
                    chapter = chapter
                )
                list.add(level)
            }
        }
        return list
    }

    private fun createLevelDefinition(
        globalLevelIndex: Int,
        chapterNum: Int,
        levelNumInChapter: Int,
        id: String,
        name: String,
        chapter: Chapter
    ): Level {
        val rows = when {
            chapterNum in 13..16 -> 8
            chapterNum in 7..12 -> 7
            else -> 6
        }
        val cols = rows

        val cells = mutableListOf<Cell>()

        // Pick mechanics based on chapter
        val hasSplitter = chapterNum in listOf(4, 8, 12, 13, 15, 16)
        val hasColors = chapterNum in listOf(5, 6, 8, 12, 15, 16)
        val hasFilter = chapterNum in listOf(6, 12, 15, 16)
        val hasEnergy = chapterNum in listOf(7, 13, 14, 15, 16)
        val hasGate = chapterNum in listOf(5, 9, 10, 11, 12, 15, 16)

        val sourceColor = when {
            hasColors && levelNumInChapter % 3 == 0 -> LightColor.RED
            hasColors && levelNumInChapter % 3 == 1 -> LightColor.BLUE
            hasColors && levelNumInChapter % 3 == 2 -> LightColor.GREEN
            else -> LightColor.WHITE
        }

        val sourceRow = 1 + (levelNumInChapter % (rows - 2))
        val targetRow = (sourceRow + 2) % (rows - 1)
        val targetCol = cols - 2

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val cellId = "c_${r}_${c}"
                val cell = when {
                    // Source
                    r == sourceRow && c == 0 -> Cell(
                        id = cellId, row = r, column = c, type = CellType.SOURCE,
                        rotation = Rotation.NINETY, isLocked = true, isLit = true, lightColor = sourceColor
                    )
                    // Secondary source for multi-beam chapters
                    (chapterNum == 8 || chapterNum == 12 || chapterNum == 16) && r == (sourceRow + 2) % (rows - 1) && c == 0 -> Cell(
                        id = cellId, row = r, column = c, type = CellType.SOURCE,
                        rotation = Rotation.NINETY, isLocked = true, isLit = true,
                        lightColor = if (sourceColor == LightColor.WHITE) LightColor.RED else LightColor.BLUE
                    )
                    // Target 1
                    r == targetRow && c == targetCol -> Cell(
                        id = cellId, row = r, column = c, type = CellType.TARGET,
                        rotation = Rotation.ZERO, isLocked = true, requiredColor = sourceColor
                    )
                    // Target 2 for splitters / multi-beam
                    (hasSplitter || chapterNum == 8) && r == (targetRow + 2) % (rows - 1) && c == targetCol -> Cell(
                        id = cellId, row = r, column = c, type = CellType.TARGET,
                        rotation = Rotation.ZERO, isLocked = true, requiredColor = sourceColor
                    )
                    // Splitter
                    hasSplitter && r == sourceRow && c == 2 -> Cell(
                        id = cellId, row = r, column = c, type = CellType.SPLITTER,
                        rotation = Rotation.ZERO, isLocked = false
                    )
                    // Filter
                    hasFilter && r == sourceRow && c == 3 -> Cell(
                        id = cellId, row = r, column = c, type = CellType.FILTER,
                        rotation = Rotation.ZERO, acceptedColor = sourceColor, isLocked = false
                    )
                    // Logic Gate
                    hasGate && r == targetRow && c == targetCol - 1 -> Cell(
                        id = cellId, row = r, column = c, type = CellType.GATE,
                        gateType = when (chapterNum) {
                            10 -> GateType.OR
                            11 -> GateType.NOT
                            else -> GateType.AND
                        },
                        rotation = Rotation.ZERO, isLocked = false
                    )
                    // Movable Mirrors
                    r == sourceRow && c == (1 + (levelNumInChapter % 2)) -> Cell(
                        id = cellId, row = r, column = c, type = CellType.MIRROR,
                        rotation = Rotation.ZERO, isLocked = false
                    )
                    r == targetRow && c == (1 + (levelNumInChapter % 2)) -> Cell(
                        id = cellId, row = r, column = c, type = CellType.MIRROR,
                        rotation = Rotation.NINETY, isLocked = false
                    )
                    // Empty grid cells
                    else -> Cell(id = cellId, row = r, column = c, type = CellType.EMPTY)
                }
                cells.add(cell)
            }
        }

        val targets = mutableListOf<TargetRequirement>()
        targets.add(TargetRequirement(position = Position(targetRow, targetCol), requiredColor = sourceColor))
        if (hasSplitter || chapterNum == 8) {
            targets.add(TargetRequirement(position = Position((targetRow + 2) % (rows - 1), targetCol), requiredColor = sourceColor))
        }

        val energyLimit = if (hasEnergy) 10 + (levelNumInChapter % 6) * 2 else 50

        return Level(
            levelId = id,
            name = name,
            description = "Chapter ${chapterNum} - ${chapter.name}: $name",
            author = "LumaLogic",
            version = 1,
            schemaVersion = 1,
            rows = rows,
            columns = cols,
            cells = cells,
            difficulty = chapter.difficulty,
            maximumEnergy = energyLimit,
            energyConfig = EnergyConfig(maxEnergy = energyLimit, cellTraversalCost = 1, mirrorCost = 2),
            threeStarThreshold = 1000 + globalLevelIndex * 5,
            twoStarThreshold = 600 + globalLevelIndex * 3,
            expectedMoves = 3 + (globalLevelIndex % 8),
            tags = listOf(chapter.id, chapter.difficulty.lowercase()),
            targetRequirements = targets
        )
    }
}
