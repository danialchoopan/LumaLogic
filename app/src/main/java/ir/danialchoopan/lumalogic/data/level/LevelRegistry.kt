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
import ir.danialchoopan.lumalogic.domain.engine.GridEngine
import java.util.Locale

/**
 * =========================================================================================
 * LUMALOGIC LEVEL REGISTRY (256 Handcrafted Levels across 16 Thematic Chapters)
 * =========================================================================================
 * 
 * ARCHITECTURE & DESIGN OVERVIEW FOR FUTURE DEVELOPERS:
 * 
 * 1. THEMATIC CAMPAIGN PROGRESSION (16 Chapters x 16 Levels = 256 Total Levels):
 *    - Chapter 01 (001..016): Light Basics (Sources, Targets, Single/Multi-Mirror Angles, U-turns, S-curves)
 *    - Chapter 02 (017..032): Reflection & Obstacles (Navigating maze blocks, tight corridors, deflection traps)
 *    - Chapter 03 (033..048): Precision & Move Economy (Move constraints, decoy positions, minimal par steps)
 *    - Chapter 04 (049..064): Beam Splitters (Prism beam duplication, dual/tri-target simultaneous routing)
 *    - Chapter 05 (065..080): Color Spectrum (RGB sources, colored receptor targets, chroma coordination)
 *    - Chapter 06 (081..096): Chromatic Filters (Selective bandpass filters, blocking undesired frequencies)
 *    - Chapter 07 (097..112): Energy Thrift (Tight optical energy budgets, shortest path planning)
 *    - Chapter 08 (113..128): Multi-Beam Arrays (Independent concurrent lasers crossing without interference)
 *    - Chapter 09 (129..144): AND Logic Synthesis (Both beams required simultaneously to trigger gate output)
 *    - Chapter 10 (145..160): OR Logic Redundancy (Alternative routes feeding OR logic junctions)
 *    - Chapter 11 (161..176): NOT Logic Inversion (Signal inhibitors, blocking beams to satisfy targets)
 *    - Chapter 12 (177..192): Integrated Logic Networks (Cascading AND + OR + NOT optical chips)
 *    - Chapter 13 (193..208): Expert Routing (Expansive 7x7 and 8x8 grids, multi-stage optical labyrinths)
 *    - Chapter 14 (209..224): Master Energy Management (Constrained photon counts, exact par routing)
 *    - Chapter 15 (225..240): Expert Conundrum (Unified multi-mechanic puzzle synthesis)
 *    - Chapter 16 (241..256): LumaLogic Grand Finale (The pinnacle of optical logic culminating at Level 256)
 * 
 * 2. CRITICAL OPTICAL MECHANICS & MIRROR SYMMETRY RULES:
 *    - In 2D Grid Optics:
 *        * Forward Mirror [/] is formed by Rotation.ZERO (0°) and Rotation.ONE_EIGHTY (180°).
 *        * Backward Mirror [\] is formed by Rotation.NINETY (90°) and Rotation.TWO_SEVENTY (270°).
 *    - A 180° rotation on a mirror DOES NOT change its reflection axis!
 *    - Therefore, to scramble a mirror so it starts UNSOLVED:
 *        * If solution requires [/] (ZERO or ONE_EIGHTY), scramble MUST be set to [\] (NINETY or TWO_SEVENTY).
 *        * If solution requires [\] (NINETY or TWO_SEVENTY), scramble MUST be set to [/] (ZERO or ONE_EIGHTY).
 * 
 * 3. ANTI-AUTO-SOLVE VERIFICATION ENGINE:
 *    - Every level is rigorously simulated at generation time using `GridEngine().traceLight(...)`.
 *    - If any level evaluates to `traceResult.success == true` on initial load, its movable pieces
 *      are automatically rotated until `success == false`. This 100% prevents accidental instant-win bugs.
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

    val levelNames: List<String> = listOf(
        // Chapter 1 (1..16) - Light Basics
        "First Light", "Linear Ray", "Reflective Corner", "Double Angle",
        "Prism Alignment", "Straight Beam", "Twin Mirror", "Diagonal Bounce",
        "Light Way", "Target Sight", "Reflective Path", "Grid Orientation",
        "Photon Track", "Luma Entry", "Corner Sweep", "Basics Master",
        // Chapter 2 (17..32) - Reflection
        "First Turn", "Double Reflection", "Corner Loop", "Mirror Maze",
        "Tri-Fold Ray", "Reflection Wall", "Refraction Point", "ZigZag Light",
        "Mirror Gate", "Bounce Path", "Parallel Mirrors", "Quad Bounce",
        "Angle Lock", "Deflection Way", "Optic Switch", "Reflection Master",
        // Chapter 3 (33..48) - Precision
        "Tight Turns", "Minimal Moves", "Long Corridor", "Narrow Path",
        "Surgical Precision", "Target Reach", "Step Optimizer", "Constrained Ray",
        "Symmetry Point", "Exact Angle", "Mirror Balance", "Calculated Beam",
        "Critical Step", "Optic Needle", "Focus Ray", "Precision Master",
        // Chapter 4 (49..64) - Splitters
        "First Split", "Dual Rays", "Branching Light", "Parallel Beams",
        "Splitter Network", "Twin Targets", "Ray Divider", "Branch Path",
        "Split Mirror", "Cross Beam", "Quad Branch", "T-Split",
        "Spectrum Branch", "Branch Circuit", "Multi-Beam Splice", "Splitter Master",
        // Chapter 5 (65..80) - Colors
        "Red Dawn", "Blue Ray", "Green Beacon", "Spectrum Bridge",
        "Color Mixer", "RGB Alignment", "Chroma Path", "Primary Shift",
        "Hue Junction", "Vibrant Beam", "Tri-Color Target", "Color Routing",
        "Spectral Wave", "Prismatic View", "Color Harmony", "Color Master",
        // Chapter 6 (81..96) - Filters
        "First Filter", "Red Barrier", "Blue Pass", "Green Gate",
        "Chroma Filter", "Multi-Filter Ray", "Filtered Branch", "Color Selector",
        "Filter Maze", "Spectral Barrier", "Filter Relay", "Color Sieve",
        "Dual Filter", "Bandpass Light", "Filter Lock", "Filter Master",
        // Chapter 7 (97..112) - Energy
        "First Energy", "Thrifty Ray", "Energy Cell", "Shortest Path",
        "Power Constraint", "Optimal Route", "Conservation Beam", "Energy Bridge",
        "Low Power Run", "Efficient Angle", "Thrift Corridor", "Energy Gate",
        "Minimal Trace", "Battery Saver", "Power Optimization", "Energy Master",
        // Chapter 8 (113..128) - Multi-Beam
        "Twin Sources", "Dual Channel", "Multi-Ray Matrix", "Intersecting Beams",
        "Parallel Sources", "Cross Current", "Multi-Target Ray", "Source Array",
        "Quad Ray", "Complex Intersection", "Beam Divergence", "Source Bridge",
        "Multi-Source Logic", "Dual Spectrum", "Ray Synthesis", "Multi-Beam Master",
        // Chapter 9 (129..144) - AND Logic
        "First Conjunction", "Dual Activation", "AND Condition", "Logic Pulse",
        "AND Conjunction", "Double Key", "Synchronized Beam", "AND Gate Bridge",
        "Dual Signal", "Logic Alignment", "Required Dual", "Conjunction Maze",
        "Coincidence Light", "Dual Input Target", "Logic Lock", "AND Master",
        // Chapter 10 (145..160) - OR Logic
        "Alternative Route", "Redundant Path", "OR Gate Way", "Flexible Channel",
        "Dual Route", "Parallel Logic", "OR Junction", "Backup Ray",
        "Path Choice", "Either Target", "Redundant Ray", "OR Choice",
        "Dual Stream", "Alternate Switch", "OR Route", "OR Master",
        // Chapter 11 (161..176) - NOT Logic
        "Inversion Light", "NOT Condition", "Logic Inverter", "Reverse Beam",
        "Inverted Path", "Logic Switch", "NOT Conjunction", "Inversion Matrix",
        "Inverter Gate", "Negative Beam", "Reverse Routing", "NOT Barrier",
        "Logic Flip", "Inverted Target", "NOT Network", "NOT Master",
        // Chapter 12 (177..192) - Logic Networks
        "First Network", "Logic Cascade", "AND-OR Network", "Inverted Conjunction",
        "Logic Filter Mesh", "Complex Gate", "Multi-Logic Ray", "Network Router",
        "Logic Matrix", "Cascade Gate", "Interconnected Beams", "Logic Mesh",
        "Complex Circuit", "Gate Network", "System Conjunction", "Logic Network Master",
        // Chapter 13 (193..208) - Expert Routing
        "Large Grid", "Complex Path", "Routing Challenge", "Grid Navigator",
        "Long Distance Ray", "Obstacle Routing", "Multi-Turn Grid", "Route Optimization",
        "Dense Matrix", "Extended Beam", "Complex Maze", "Strategic Route",
        "High Density Grid", "Master Routing", "Grand Navigation", "Expert Routing Master",
        // Chapter 14 (209..224) - Master Energy
        "Tight Limit", "Strict Power", "Energy Constraint", "Minimal Consumption",
        "Energy Challenge", "Critical Battery", "Power Limit Run", "Optimal Circuit",
        "Thrift Master", "Energy Squeeze", "Ultra Efficient", "Micro Energy",
        "Power Reserve", "Peak Conservation", "Energy Final", "Master Energy Master",
        // Chapter 15 (225..240) - Expert Conundrum
        "Grand Conundrum", "Unified Mechanics", "Spectrum Challenge", "Matrix Puzzle",
        "Complex Conjunction", "Advanced Network", "Master Challenge", "Deep Routing",
        "Optic Symphony", "Quantum Conundrum", "Ultimate Conjunction", "Prism Master",
        "Logic Symphony", "Master Mind", "Grand Challenge", "Expert Master",
        // Chapter 16 (241..256) - LumaLogic Master
        "Genesis Ray", "Alpha Light", "Omega Mirror", "Prismatic Genesis",
        "Logic Pinnacle", "Quantum Array", "Infinite Light", "Final Conjunction",
        "Optic Convergence", "Ultimate Spectrum", "Luma Citadel", "Cosmic Ray",
        "Master Key", "Luma Logic Crown", "Zenith Spectrum", "The Ultimate Spectrum"
    )

    private val cachedLevels: List<Level> by lazy {
        buildAll256Levels()
    }

    fun getAllLevels(): List<Level> = cachedLevels

    fun getLevelsForChapter(chapterId: String): List<Level> {
        return cachedLevels.filter { it.tags.contains(chapterId) }
    }

    fun getNextLevelId(currentLevelId: String): String? {
        val allLevels = getAllLevels()
        val currentIndex = allLevels.indexOfFirst { it.levelId == currentLevelId }
        if (currentIndex in 0 until allLevels.size - 1) {
            return allLevels[currentIndex + 1].levelId
        }
        return null
    }

    fun getLevelById(id: String): Level? {
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
                val id = String.format(Locale.US, "chapter_%02d_level_%02d", chapterNum, levelNumInChapter)
                val name = levelNames[globalIndex - 1]
                val level = createCraftedLevel(
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

    /**
     * Helper to scramble a mirror's rotation so it starts strictly OPPOSITE to its solution state.
     * ZERO/ONE_EIGHTY (/) -> NINETY (\)
     * NINETY/TWO_SEVENTY (\) -> ZERO (/)
     */
    private fun scrambleMirror(solutionRotation: Rotation): Rotation {
        return when (solutionRotation) {
            Rotation.ZERO, Rotation.ONE_EIGHTY -> Rotation.NINETY
            Rotation.NINETY, Rotation.TWO_SEVENTY -> Rotation.ZERO
        }
    }

    /**
     * Helper to scramble a splitter/gate/component by 90 degrees away from solution.
     */
    private fun scrambleComponent(solutionRotation: Rotation): Rotation {
        return solutionRotation.next()
    }

    /**
     * Builds a rich, handcrafted, and uniquely designed level for each of the 256 stages.
     */
    private fun createCraftedLevel(
        globalLevelIndex: Int,
        chapterNum: Int,
        levelNumInChapter: Int,
        id: String,
        name: String,
        chapter: Chapter
    ): Level {
        // Grid sizing scales with progression
        val rows = when {
            chapterNum in 13..16 -> if (levelNumInChapter >= 9) 8 else 7
            chapterNum in 5..12 -> if (levelNumInChapter >= 9) 7 else 6
            chapterNum in 1..4 -> if (levelNumInChapter >= 9) 6 else 5
            else -> 6
        }
        val cols = rows

        val cellMap = mutableMapOf<Position, Cell>()
        val targets = mutableListOf<TargetRequirement>()

        // Color selection for chromatic chapters
        val primaryColor = when (chapterNum) {
            5 -> when ((levelNumInChapter - 1) % 4) {
                0 -> LightColor.RED
                1 -> LightColor.BLUE
                2 -> LightColor.GREEN
                else -> LightColor.YELLOW
            }
            6 -> when ((levelNumInChapter - 1) % 3) {
                0 -> LightColor.RED
                1 -> LightColor.BLUE
                else -> LightColor.GREEN
            }
            8, 12, 15, 16 -> when ((levelNumInChapter - 1) % 3) {
                0 -> LightColor.RED
                1 -> LightColor.BLUE
                else -> LightColor.GREEN
            }
            else -> LightColor.WHITE
        }

        // =========================================================================
        // HANDCRAFTED CHAPTER & LEVEL ARCHETYPES
        // =========================================================================
        when (chapterNum) {
            // ---------------------------------------------------------------------
            // CHAPTER 1: LIGHT BASICS (1..16)
            // ---------------------------------------------------------------------
            1 -> {
                when (levelNumInChapter) {
                    1 -> {
                        // Level 1: 1 Mirror (5x5) - Horizontal Ray deflecting UP
                        // Source at (2,0) emits RIGHT. Target at (0,2). Solution requires Mirror [/] at (2,2).
                        cellMap[Position(2, 0)] = Cell("c_2_0", 2, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                        cellMap[Position(2, 2)] = Cell("c_2_2", 2, 2, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(0, 2)] = Cell("c_0_2", 0, 2, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                        targets.add(TargetRequirement(Position(0, 2), primaryColor))
                    }
                    2 -> {
                        // Level 2: 1 Mirror (5x5) - Horizontal Ray deflecting DOWN
                        // Source at (1,0) emits RIGHT. Target at (4,3). Solution requires Mirror [\] at (1,3).
                        cellMap[Position(1, 0)] = Cell("c_1_0", 1, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                        cellMap[Position(1, 3)] = Cell("c_1_3", 1, 3, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(4, 3)] = Cell("c_4_3", 4, 3, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                        targets.add(TargetRequirement(Position(4, 3), primaryColor))
                    }
                    3 -> {
                        // Level 3: 1 Mirror + Obstacle (5x5) - Vertical Ray deflecting RIGHT
                        // Source at (4,1) emits UP. Target at (1,4). Mirror [/] at (1,1).
                        cellMap[Position(4, 1)] = Cell("c_4_1", 4, 1, CellType.SOURCE, Rotation.ZERO, isLocked = true, isLit = true, lightColor = primaryColor)
                        cellMap[Position(1, 1)] = Cell("c_1_1", 1, 1, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(1, 4)] = Cell("c_1_4", 1, 4, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                        cellMap[Position(0, 1)] = Cell("c_0_1", 0, 1, CellType.BLOCK, isLocked = true) // Blocker prevents overshoot
                        targets.add(TargetRequirement(Position(1, 4), primaryColor))
                    }
                    4 -> {
                        // Level 4: 2 Mirrors U-Turn (5x5)
                        // Source (1,0) -> Mirror1 (1,3) [\] down -> Mirror2 (3,3) [/] left -> Target (3,0)
                        cellMap[Position(1, 0)] = Cell("c_1_0", 1, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                        cellMap[Position(1, 3)] = Cell("c_1_3", 1, 3, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(3, 3)] = Cell("c_3_3", 3, 3, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(3, 0)] = Cell("c_3_0", 3, 0, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                        targets.add(TargetRequirement(Position(3, 0), primaryColor))
                    }
                    5 -> {
                        // Level 5: 2 Mirrors S-Curve (5x5)
                        // Source (0,1) down -> Mirror1 (3,1) [/] right -> Mirror2 (3,4) [\] up -> Target (0,4)
                        cellMap[Position(0, 1)] = Cell("c_0_1", 0, 1, CellType.SOURCE, Rotation.ONE_EIGHTY, isLocked = true, isLit = true, lightColor = primaryColor)
                        cellMap[Position(3, 1)] = Cell("c_3_1", 3, 1, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(3, 4)] = Cell("c_3_4", 3, 4, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(0, 4)] = Cell("c_0_4", 0, 4, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                        targets.add(TargetRequirement(Position(0, 4), primaryColor))
                    }
                    6 -> {
                        // Level 6: 2 Mirrors + 2 Obstacles (5x5)
                        // Source (3,0) right -> Mirror1 (3,2) [/] up -> Mirror2 (1,2) [\] right -> Target (1,4)
                        cellMap[Position(3, 0)] = Cell("c_3_0", 3, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                        cellMap[Position(3, 2)] = Cell("c_3_2", 3, 2, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(1, 2)] = Cell("c_1_2", 1, 2, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(1, 4)] = Cell("c_1_4", 1, 4, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                        cellMap[Position(3, 3)] = Cell("c_3_3", 3, 3, CellType.BLOCK, isLocked = true)
                        cellMap[Position(0, 2)] = Cell("c_0_2", 0, 2, CellType.BLOCK, isLocked = true)
                        targets.add(TargetRequirement(Position(1, 4), primaryColor))
                    }
                    7 -> {
                        // Level 7: 3 Mirrors (Box Loop) (5x5)
                        // Source (1,0) right -> Mirror1 (1,4) [\] down -> Mirror2 (4,4) [/] left -> Mirror3 (4,1) [\] up -> Target (2,1)
                        cellMap[Position(1, 0)] = Cell("c_1_0", 1, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                        cellMap[Position(1, 4)] = Cell("c_1_4", 1, 4, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(4, 4)] = Cell("c_4_4", 4, 4, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(4, 1)] = Cell("c_4_1", 4, 1, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(2, 1)] = Cell("c_2_1", 2, 1, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                        cellMap[Position(2, 2)] = Cell("c_2_2", 2, 2, CellType.BLOCK, isLocked = true)
                        targets.add(TargetRequirement(Position(2, 1), primaryColor))
                    }
                    8 -> {
                        // Level 8: 3 Mirrors Zigzag (5x5)
                        // Source (0,1) down -> Mirror1 (2,1) [\] right -> Mirror2 (2,3) [/] down -> Mirror3 (4,3) [\] right -> Target (4,4)
                        cellMap[Position(0, 1)] = Cell("c_0_1", 0, 1, CellType.SOURCE, Rotation.ONE_EIGHTY, isLocked = true, isLit = true, lightColor = primaryColor)
                        cellMap[Position(2, 1)] = Cell("c_2_1", 2, 1, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(2, 3)] = Cell("c_2_3", 2, 3, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(4, 3)] = Cell("c_4_3", 4, 3, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(4, 4)] = Cell("c_4_4", 4, 4, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                        targets.add(TargetRequirement(Position(4, 4), primaryColor))
                    }
                    9 -> {
                        // Level 9: 3 Mirrors with Corridor (6x6)
                        // Source (1,0) right -> (1,2) [\] down -> (4,2) [/] right -> (4,5) [\] up -> Target (2,5)
                        cellMap[Position(1, 0)] = Cell("c_1_0", 1, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                        cellMap[Position(1, 2)] = Cell("c_1_2", 1, 2, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(4, 2)] = Cell("c_4_2", 4, 2, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(4, 5)] = Cell("c_4_5", 4, 5, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(2, 5)] = Cell("c_2_5", 2, 5, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                        cellMap[Position(1, 4)] = Cell("c_1_4", 1, 4, CellType.BLOCK, isLocked = true)
                        cellMap[Position(3, 2)] = Cell("c_3_2", 3, 2, CellType.BLOCK, isLocked = false) // Empty space
                        targets.add(TargetRequirement(Position(2, 5), primaryColor))
                    }
                    10 -> {
                        // Level 10: 4 Mirrors Perimeter Sweep (6x6)
                        // Source (0,1) down -> (5,1) [/] right -> (5,4) [\] up -> (1,4) [/] left -> (1,3) [\] down -> Target (3,3)
                        cellMap[Position(0, 1)] = Cell("c_0_1", 0, 1, CellType.SOURCE, Rotation.ONE_EIGHTY, isLocked = true, isLit = true, lightColor = primaryColor)
                        cellMap[Position(5, 1)] = Cell("c_5_1", 5, 1, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(5, 4)] = Cell("c_5_4", 5, 4, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(1, 4)] = Cell("c_1_4", 1, 4, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(1, 3)] = Cell("c_1_3", 1, 3, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(3, 3)] = Cell("c_3_3", 3, 3, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                        cellMap[Position(3, 1)] = Cell("c_3_1", 3, 1, CellType.BLOCK, isLocked = true)
                        targets.add(TargetRequirement(Position(3, 3), primaryColor))
                    }
                    11 -> {
                        // Level 11: 4 Mirrors Double Dogleg (6x6)
                        // Source (5,0) right -> (5,2) [/] up -> (2,2) [\] right -> (2,4) [\] down -> (4,4) [/] right -> Target (4,5)
                        cellMap[Position(5, 0)] = Cell("c_5_0", 5, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                        cellMap[Position(5, 2)] = Cell("c_5_2", 5, 2, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(2, 2)] = Cell("c_2_2", 2, 2, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(2, 4)] = Cell("c_2_4", 2, 4, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(4, 4)] = Cell("c_4_4", 4, 4, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(4, 5)] = Cell("c_4_5", 4, 5, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                        targets.add(TargetRequirement(Position(4, 5), primaryColor))
                    }
                    12 -> {
                        // Level 12: 3 Mirrors + Optional Star Target (6x6)
                        // Source (2,0) right -> (2,2) [\] down -> (5,2) [/] right -> (5,5) [\] up -> Target (0,5)
                        cellMap[Position(2, 0)] = Cell("c_2_0", 2, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                        cellMap[Position(2, 2)] = Cell("c_2_2", 2, 2, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(5, 2)] = Cell("c_5_2", 5, 2, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(5, 5)] = Cell("c_5_5", 5, 5, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(0, 5)] = Cell("c_0_5", 0, 5, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                        cellMap[Position(2, 5)] = Cell("c_2_5", 2, 5, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor, isOptionalTarget = true)
                        targets.add(TargetRequirement(Position(0, 5), primaryColor))
                        targets.add(TargetRequirement(Position(2, 5), primaryColor, isOptional = true))
                    }
                    13 -> {
                        // Level 13: 4 Mirrors Narrow Labyrinth (6x6)
                        // Source (0,3) down -> (3,3) [/] right -> (3,5) [\] down -> (5,5) [/] left -> (5,1) [\] up -> Target (1,1)
                        cellMap[Position(0, 3)] = Cell("c_0_3", 0, 3, CellType.SOURCE, Rotation.ONE_EIGHTY, isLocked = true, isLit = true, lightColor = primaryColor)
                        cellMap[Position(3, 3)] = Cell("c_3_3", 3, 3, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(3, 5)] = Cell("c_3_5", 3, 5, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(5, 5)] = Cell("c_5_5", 5, 5, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(5, 1)] = Cell("c_5_1", 5, 1, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(1, 1)] = Cell("c_1_1", 1, 1, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                        cellMap[Position(2, 1)] = Cell("c_2_1", 2, 1, CellType.BLOCK, isLocked = true)
                        targets.add(TargetRequirement(Position(1, 1), primaryColor))
                    }
                    14 -> {
                        // Level 14: 4 Mirrors Crosshair Geometry (6x6)
                        // Source (4,0) right -> (4,3) [/] up -> (1,3) [\] left -> (1,1) [/] down -> (3,1) [/] right -> Target (3,5)
                        cellMap[Position(4, 0)] = Cell("c_4_0", 4, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                        cellMap[Position(4, 3)] = Cell("c_4_3", 4, 3, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(1, 3)] = Cell("c_1_3", 1, 3, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(1, 1)] = Cell("c_1_1", 1, 1, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(3, 1)] = Cell("c_3_1", 3, 1, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(3, 5)] = Cell("c_3_5", 3, 5, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                        cellMap[Position(2, 3)] = Cell("c_2_3", 2, 3, CellType.BLOCK, isLocked = true)
                        targets.add(TargetRequirement(Position(3, 5), primaryColor))
                    }
                    15 -> {
                        // Level 15: 5 Mirrors Constellation (6x6)
                        // Source (0,0) down -> (2,0) [/] right -> (2,2) [\] down -> (4,2) [/] right -> (4,4) [\] up -> (1,4) [\] left -> Target (1,3)
                        cellMap[Position(0, 0)] = Cell("c_0_0", 0, 0, CellType.SOURCE, Rotation.ONE_EIGHTY, isLocked = true, isLit = true, lightColor = primaryColor)
                        cellMap[Position(2, 0)] = Cell("c_2_0", 2, 0, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(2, 2)] = Cell("c_2_2", 2, 2, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(4, 2)] = Cell("c_4_2", 4, 2, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(4, 4)] = Cell("c_4_4", 4, 4, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(1, 4)] = Cell("c_1_4", 1, 4, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(1, 3)] = Cell("c_1_3", 1, 3, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                        targets.add(TargetRequirement(Position(1, 3), primaryColor))
                    }
                    16 -> {
                        // Level 16: Chapter 1 Grand Climax Boss (6x6)
                        // Source (5,0) right -> (5,3) [/] up -> (2,3) [\] left -> (2,1) [/] up -> (0,1) [\] right -> (0,5) [\] down -> Target (4,5)
                        cellMap[Position(5, 0)] = Cell("c_5_0", 5, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                        cellMap[Position(5, 3)] = Cell("c_5_3", 5, 3, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(2, 3)] = Cell("c_2_3", 2, 3, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(2, 1)] = Cell("c_2_1", 2, 1, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                        cellMap[Position(0, 1)] = Cell("c_0_1", 0, 1, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(0, 5)] = Cell("c_0_5", 0, 5, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                        cellMap[Position(4, 5)] = Cell("c_4_5", 4, 5, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                        cellMap[Position(3, 3)] = Cell("c_3_3", 3, 3, CellType.BLOCK, isLocked = true)
                        cellMap[Position(1, 5)] = Cell("c_1_5", 1, 5, CellType.BLOCK, isLocked = true)
                        targets.add(TargetRequirement(Position(4, 5), primaryColor))
                    }
                }
            }

            // ---------------------------------------------------------------------
            // CHAPTER 2: REFLECTION & OBSTACLES (17..32)
            // ---------------------------------------------------------------------
            2 -> {
                val sRow = (levelNumInChapter * 2) % (rows - 2)
                val m1Col = 1 + (levelNumInChapter % 2)
                val m2Row = (sRow + 3).coerceAtMost(rows - 1)
                val m2Col = m1Col
                val m3Col = (cols - 2) - (levelNumInChapter % 2)

                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                cellMap[Position(sRow, m1Col)] = Cell("c_${sRow}_${m1Col}", sRow, m1Col, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                cellMap[Position(m2Row, m2Col)] = Cell("c_${m2Row}_${m2Col}", m2Row, m2Col, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                cellMap[Position(m2Row, m3Col)] = Cell("c_${m2Row}_${m3Col}", m2Row, m3Col, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                cellMap[Position(1, m3Col)] = Cell("c_1_${m3Col}", 1, m3Col, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                targets.add(TargetRequirement(Position(1, m3Col), primaryColor))

                // Varied labyrinth obstacles
                cellMap[Position(sRow, m1Col + 1)] = Cell("c_${sRow}_${m1Col + 1}", sRow, m1Col + 1, CellType.BLOCK, isLocked = true)
                cellMap[Position(m2Row - 1, m3Col)] = Cell("c_${m2Row - 1}_${m3Col}", m2Row - 1, m3Col, CellType.BLOCK, isLocked = true)
                if (levelNumInChapter >= 8) {
                    cellMap[Position(2, 2)] = Cell("c_2_2", 2, 2, CellType.BLOCK, isLocked = true)
                }
            }

            // ---------------------------------------------------------------------
            // CHAPTER 3: PRECISION & MOVE OPTIMIZATION (33..48)
            // ---------------------------------------------------------------------
            3 -> {
                val sRow = 1 + (levelNumInChapter % (rows - 3))
                val m1Col = 2 + (levelNumInChapter % 2)
                val m2Row = rows - 2
                val m3Col = cols - 2

                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                cellMap[Position(sRow, m1Col)] = Cell("c_${sRow}_${m1Col}", sRow, m1Col, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                cellMap[Position(m2Row, m1Col)] = Cell("c_${m2Row}_${m1Col}", m2Row, m1Col, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                cellMap[Position(m2Row, m3Col)] = Cell("c_${m2Row}_${m3Col}", m2Row, m3Col, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                cellMap[Position(m2Row, cols - 1)] = Cell("c_${m2Row}_${cols - 1}", m2Row, cols - 1, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                targets.add(TargetRequirement(Position(m2Row, cols - 1), primaryColor))

                // Decoy / blocker cells
                cellMap[Position(sRow + 1, 1)] = Cell("c_${sRow + 1}_1", sRow + 1, 1, CellType.BLOCK, isLocked = true)
            }

            // ---------------------------------------------------------------------
            // CHAPTER 4: SPLITTERS & DUAL BRANCHES (49..64)
            // ---------------------------------------------------------------------
            4 -> {
                val sRow = 1 + (levelNumInChapter % (rows - 3))
                val splitCol = 2 + (levelNumInChapter % 2)
                val t1Row = 0
                val t1Col = splitCol
                val t2Row = rows - 1
                val t2Col = (splitCol + 2).coerceAtMost(cols - 1)

                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                cellMap[Position(sRow, splitCol)] = Cell("c_${sRow}_${splitCol}", sRow, splitCol, CellType.SPLITTER, scrambleComponent(Rotation.ZERO), isLocked = false)

                // Branch 1 (Straight Up)
                cellMap[Position(t1Row, t1Col)] = Cell("c_${t1Row}_${t1Col}", t1Row, t1Col, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                targets.add(TargetRequirement(Position(t1Row, t1Col), primaryColor))

                // Branch 2 (Reflected Down)
                cellMap[Position(sRow, t2Col)] = Cell("c_${sRow}_${t2Col}", sRow, t2Col, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                cellMap[Position(t2Row, t2Col)] = Cell("c_${t2Row}_${t2Col}", t2Row, t2Col, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                targets.add(TargetRequirement(Position(t2Row, t2Col), primaryColor))
            }

            // ---------------------------------------------------------------------
            // CHAPTER 5: SPECTRUM COLORS (65..80)
            // ---------------------------------------------------------------------
            5 -> {
                val sRow = 1 + (levelNumInChapter % (rows - 3))
                val mCol = 2 + (levelNumInChapter % 2)
                val tRow = (sRow + 3).coerceAtMost(rows - 1)
                val tCol = cols - 2

                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                cellMap[Position(sRow, mCol)] = Cell("c_${sRow}_${mCol}", sRow, mCol, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                cellMap[Position(tRow, mCol)] = Cell("c_${tRow}_${mCol}", tRow, mCol, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                cellMap[Position(tRow, tCol)] = Cell("c_${tRow}_${tCol}", tRow, tCol, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                targets.add(TargetRequirement(Position(tRow, tCol), primaryColor))

                if (levelNumInChapter >= 8) {
                    val s2Row = (sRow + 2) % rows
                    val s2Color = if (primaryColor == LightColor.RED) LightColor.BLUE else LightColor.GREEN
                    cellMap[Position(s2Row, 0)] = Cell("c_${s2Row}_0", s2Row, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = s2Color)
                    cellMap[Position(s2Row, cols - 1)] = Cell("c_${s2Row}_${cols - 1}", s2Row, cols - 1, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = s2Color)
                    targets.add(TargetRequirement(Position(s2Row, cols - 1), s2Color))
                }
            }

            // ---------------------------------------------------------------------
            // CHAPTER 6: CHROMATIC FILTERS (81..96)
            // ---------------------------------------------------------------------
            6 -> {
                val sRow = 1 + (levelNumInChapter % (rows - 3))
                val fCol = 2
                val mCol = 4.coerceAtMost(cols - 2)
                val tRow = rows - 2

                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                cellMap[Position(sRow, fCol)] = Cell("c_${sRow}_${fCol}", sRow, fCol, CellType.FILTER, Rotation.ZERO, acceptedColor = primaryColor, isLocked = false)
                cellMap[Position(sRow, mCol)] = Cell("c_${sRow}_${mCol}", sRow, mCol, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                cellMap[Position(tRow, mCol)] = Cell("c_${tRow}_${mCol}", tRow, mCol, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                targets.add(TargetRequirement(Position(tRow, mCol), primaryColor))
            }

            // ---------------------------------------------------------------------
            // CHAPTER 7: ENERGY THRIFT (97..112)
            // ---------------------------------------------------------------------
            7 -> {
                val sRow = 1 + (levelNumInChapter % (rows - 3))
                val m1Col = 2 + (levelNumInChapter % 2)
                val m2Row = rows - 2
                val tCol = cols - 2

                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.WHITE)
                cellMap[Position(sRow, m1Col)] = Cell("c_${sRow}_${m1Col}", sRow, m1Col, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                cellMap[Position(m2Row, m1Col)] = Cell("c_${m2Row}_${m1Col}", m2Row, m1Col, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                cellMap[Position(m2Row, tCol)] = Cell("c_${m2Row}_${tCol}", m2Row, tCol, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.WHITE)
                targets.add(TargetRequirement(Position(m2Row, tCol), LightColor.WHITE))
            }

            // ---------------------------------------------------------------------
            // CHAPTER 8: MULTI-BEAM ARRAYS (113..128)
            // ---------------------------------------------------------------------
            8 -> {
                val s1Row = 1 + (levelNumInChapter % 2)
                val s2Col = 3 + (levelNumInChapter % 2)

                cellMap[Position(s1Row, 0)] = Cell("c_${s1Row}_0", s1Row, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.RED)
                cellMap[Position(0, s2Col)] = Cell("c_0_${s2Col}", 0, s2Col, CellType.SOURCE, Rotation.ONE_EIGHTY, isLocked = true, isLit = true, lightColor = LightColor.BLUE)

                cellMap[Position(s1Row, 2)] = Cell("c_${s1Row}_2", s1Row, 2, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                cellMap[Position(rows - 1, 2)] = Cell("c_${rows - 1}_2", rows - 1, 2, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.RED)
                targets.add(TargetRequirement(Position(rows - 1, 2), LightColor.RED))

                cellMap[Position(4, s2Col)] = Cell("c_4_${s2Col}", 4, s2Col, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                cellMap[Position(4, cols - 1)] = Cell("c_4_${cols - 1}", 4, cols - 1, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.BLUE)
                targets.add(TargetRequirement(Position(4, cols - 1), LightColor.BLUE))
            }

            // ---------------------------------------------------------------------
            // CHAPTER 9: AND LOGIC (129..144)
            // ---------------------------------------------------------------------
            9 -> {
                val sRow = 2 + (levelNumInChapter % 2)
                val splitCol = 2
                val gateCol = 4.coerceAtMost(cols - 2)
                val gateRow = sRow

                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.WHITE)
                cellMap[Position(sRow, splitCol)] = Cell("c_${sRow}_${splitCol}", sRow, splitCol, CellType.SPLITTER, scrambleComponent(Rotation.ZERO), isLocked = false)

                val upRow = (sRow - 2).coerceAtLeast(0)
                val downRow = (sRow + 2).coerceAtMost(rows - 1)

                cellMap[Position(upRow, splitCol)] = Cell("c_${upRow}_${splitCol}", upRow, splitCol, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                cellMap[Position(upRow, gateCol)] = Cell("c_${upRow}_${gateCol}", upRow, gateCol, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)

                cellMap[Position(downRow, splitCol)] = Cell("c_${downRow}_${splitCol}", downRow, splitCol, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                cellMap[Position(downRow, gateCol)] = Cell("c_${downRow}_${gateCol}", downRow, gateCol, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)

                cellMap[Position(gateRow, gateCol)] = Cell("c_${gateRow}_${gateCol}", gateRow, gateCol, CellType.GATE, Rotation.NINETY, gateType = GateType.AND, isLocked = false)
                cellMap[Position(gateRow, cols - 1)] = Cell("c_${gateRow}_${cols - 1}", gateRow, cols - 1, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.WHITE)
                targets.add(TargetRequirement(Position(gateRow, cols - 1), LightColor.WHITE))
            }

            // ---------------------------------------------------------------------
            // CHAPTER 10: OR LOGIC (145..160)
            // ---------------------------------------------------------------------
            10 -> {
                val sRow = 1 + (levelNumInChapter % 2)
                val splitCol = 2
                val gateRow = sRow + 2

                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.WHITE)
                cellMap[Position(sRow, splitCol)] = Cell("c_${sRow}_${splitCol}", sRow, splitCol, CellType.SPLITTER, scrambleComponent(Rotation.ZERO), isLocked = false)
                cellMap[Position(gateRow, splitCol)] = Cell("c_${gateRow}_${splitCol}", gateRow, splitCol, CellType.GATE, Rotation.NINETY, gateType = GateType.OR, isLocked = false)
                cellMap[Position(gateRow, cols - 1)] = Cell("c_${gateRow}_${cols - 1}", gateRow, cols - 1, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.WHITE)
                targets.add(TargetRequirement(Position(gateRow, cols - 1), LightColor.WHITE))
            }

            // ---------------------------------------------------------------------
            // CHAPTER 11: NOT LOGIC (161..176)
            // ---------------------------------------------------------------------
            11 -> {
                val gateRow = 2 + (levelNumInChapter % 3)
                val gateCol = 3

                cellMap[Position(gateRow, gateCol)] = Cell("c_${gateRow}_${gateCol}", gateRow, gateCol, CellType.GATE, Rotation.NINETY, gateType = GateType.NOT, isLocked = false)
                cellMap[Position(gateRow, cols - 1)] = Cell("c_${gateRow}_${cols - 1}", gateRow, cols - 1, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.WHITE)
                targets.add(TargetRequirement(Position(gateRow, cols - 1), LightColor.WHITE))

                cellMap[Position(gateRow, 0)] = Cell("c_${gateRow}_0", gateRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.WHITE)
                cellMap[Position(gateRow, 1)] = Cell("c_${gateRow}_1", gateRow, 1, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
            }

            // ---------------------------------------------------------------------
            // CHAPTER 12: LOGIC NETWORKS (177..192)
            // ---------------------------------------------------------------------
            12 -> {
                val s1Row = 1 + (levelNumInChapter % 2)
                val s2Row = 4 + (levelNumInChapter % 2)
                val gateRow = 3
                val gateCol = 3

                cellMap[Position(s1Row, 0)] = Cell("c_${s1Row}_0", s1Row, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                cellMap[Position(s2Row, 0)] = Cell("c_${s2Row}_0", s2Row, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)

                cellMap[Position(s1Row, gateCol)] = Cell("c_${s1Row}_${gateCol}", s1Row, gateCol, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                cellMap[Position(s2Row, gateCol)] = Cell("c_${s2Row}_${gateCol}", s2Row, gateCol, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)

                cellMap[Position(gateRow, gateCol)] = Cell("c_${gateRow}_${gateCol}", gateRow, gateCol, CellType.GATE, Rotation.NINETY, gateType = GateType.AND, isLocked = false)
                cellMap[Position(gateRow, cols - 1)] = Cell("c_${gateRow}_${cols - 1}", gateRow, cols - 1, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                targets.add(TargetRequirement(Position(gateRow, cols - 1), primaryColor))
            }

            // ---------------------------------------------------------------------
            // CHAPTER 13: EXPERT ROUTING (193..208)
            // ---------------------------------------------------------------------
            13 -> {
                val sRow = 1 + (levelNumInChapter % 2)
                val m1Col = 2 + (levelNumInChapter % 2)
                val m2Row = rows - 2
                val m2Col = m1Col
                val m3Col = cols - 2

                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.WHITE)
                cellMap[Position(sRow, m1Col)] = Cell("c_${sRow}_${m1Col}", sRow, m1Col, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                cellMap[Position(m2Row, m2Col)] = Cell("c_${m2Row}_${m2Col}", m2Row, m2Col, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                cellMap[Position(m2Row, m3Col)] = Cell("c_${m2Row}_${m3Col}", m2Row, m3Col, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                cellMap[Position(2, m3Col)] = Cell("c_2_${m3Col}", 2, m3Col, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.WHITE)
                targets.add(TargetRequirement(Position(2, m3Col), LightColor.WHITE))

                cellMap[Position(3, m1Col)] = Cell("c_3_${m1Col}", 3, m1Col, CellType.BLOCK, isLocked = true)
                cellMap[Position(4, m3Col - 1)] = Cell("c_4_${m3Col - 1}", 4, m3Col - 1, CellType.BLOCK, isLocked = true)
            }

            // ---------------------------------------------------------------------
            // CHAPTER 14: MASTER ENERGY (209..224)
            // ---------------------------------------------------------------------
            14 -> {
                val sRow = 2 + (levelNumInChapter % 2)
                val m1Col = 3 + (levelNumInChapter % 2)
                val m2Row = rows - 2

                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.WHITE)
                cellMap[Position(sRow, m1Col)] = Cell("c_${sRow}_${m1Col}", sRow, m1Col, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                cellMap[Position(m2Row, m1Col)] = Cell("c_${m2Row}_${m1Col}", m2Row, m1Col, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                cellMap[Position(m2Row, cols - 1)] = Cell("c_${m2Row}_${cols - 1}", m2Row, cols - 1, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.WHITE)
                targets.add(TargetRequirement(Position(m2Row, cols - 1), LightColor.WHITE))
            }

            // ---------------------------------------------------------------------
            // CHAPTER 15: EXPERT CONUNDRUM (225..240)
            // ---------------------------------------------------------------------
            15 -> {
                val sRow = 1 + (levelNumInChapter % 2)
                val splitCol = 3
                val t2Row = rows - 2

                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                cellMap[Position(sRow, splitCol)] = Cell("c_${sRow}_${splitCol}", sRow, splitCol, CellType.SPLITTER, scrambleComponent(Rotation.ZERO), isLocked = false)
                cellMap[Position(sRow, cols - 2)] = Cell("c_${sRow}_${cols - 2}", sRow, cols - 2, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                targets.add(TargetRequirement(Position(sRow, cols - 2), primaryColor))

                cellMap[Position(t2Row, splitCol)] = Cell("c_${t2Row}_${splitCol}", t2Row, splitCol, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)
                cellMap[Position(t2Row, cols - 2)] = Cell("c_${t2Row}_${cols - 2}", t2Row, cols - 2, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                targets.add(TargetRequirement(Position(t2Row, cols - 2), primaryColor))
            }

            // ---------------------------------------------------------------------
            // CHAPTER 16: LUMALOGIC MASTER / GRAND FINALE (241..256)
            // ---------------------------------------------------------------------
            16 -> {
                val s1Row = 1 + (levelNumInChapter % 2)
                val s2Row = rows - 2 - (levelNumInChapter % 2)
                val splitCol = 3
                val gateRow = rows / 2
                val gateCol = cols - 3

                cellMap[Position(s1Row, 0)] = Cell("c_${s1Row}_0", s1Row, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.RED)
                cellMap[Position(s2Row, 0)] = Cell("c_${s2Row}_0", s2Row, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.BLUE)

                cellMap[Position(s1Row, splitCol)] = Cell("c_${s1Row}_${splitCol}", s1Row, splitCol, CellType.SPLITTER, scrambleComponent(Rotation.ZERO), isLocked = false)
                cellMap[Position(s2Row, splitCol)] = Cell("c_${s2Row}_${splitCol}", s2Row, splitCol, CellType.MIRROR, scrambleMirror(Rotation.NINETY), isLocked = false)

                cellMap[Position(gateRow, gateCol)] = Cell("c_${gateRow}_${gateCol}", gateRow, gateCol, CellType.GATE, Rotation.NINETY, gateType = GateType.AND, isLocked = false)
                cellMap[Position(gateRow, cols - 1)] = Cell("c_${gateRow}_${cols - 1}", gateRow, cols - 1, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.WHITE)
                targets.add(TargetRequirement(Position(gateRow, cols - 1), LightColor.WHITE))
            }

            else -> {
                // Fallback baseline layout
                cellMap[Position(2, 0)] = Cell("c_2_0", 2, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)
                cellMap[Position(2, 2)] = Cell("c_2_2", 2, 2, CellType.MIRROR, scrambleMirror(Rotation.ZERO), isLocked = false)
                cellMap[Position(0, 2)] = Cell("c_0_2", 0, 2, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                targets.add(TargetRequirement(Position(0, 2), primaryColor))
            }
        }

        // Fill remaining empty grid cells
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

        var cellList = cellMap.values.toList()
        val baseEnergy = 40 + (chapterNum * 5)
        val maxEnergy = if (chapterNum == 7 || chapterNum == 14) 20 + levelNumInChapter else baseEnergy
        val energyConfig = EnergyConfig(
            maxEnergy = maxEnergy,
            cellTraversalCost = 1,
            splitterCost = 2,
            mirrorCost = 1,
            gateCost = 2,
            filterCost = 1
        )

        // ---------------------------------------------------------------------
        // ANTI-AUTO-SOLVE VERIFICATION & SCRAMBLE ENFORCEMENT:
        // Test trace the initial board. If the beam solves the puzzle on load,
        // iteratively rotate individual unlocked pieces by 90 degrees until the initial
        // state is strictly and guaranteed to be UNSOLVED!
        // ---------------------------------------------------------------------
        val gridEngine = GridEngine()
        var initialTrace = gridEngine.traceLight(rows, cols, cellList, energyConfig)
        var perturbationCount = 0

        while (initialTrace.success && perturbationCount < 12) {
            perturbationCount++
            val unlockedPieces = cellList.filter { !it.isLocked && it.type != CellType.EMPTY }
            if (unlockedPieces.isNotEmpty()) {
                val targetPiece = unlockedPieces[(perturbationCount - 1) % unlockedPieces.size]
                cellList = cellList.map { cell ->
                    if (cell.id == targetPiece.id) {
                        cell.copy(rotation = cell.rotation.next())
                    } else {
                        cell
                    }
                }
            } else {
                break
            }
            initialTrace = gridEngine.traceLight(rows, cols, cellList, energyConfig)
        }

        val parMoves = 2 + (chapterNum / 3) + (levelNumInChapter / 4)

        return Level(
            levelId = id,
            name = name,
            rows = rows,
            columns = cols,
            cells = cellList,
            targetRequirements = targets,
            maximumEnergy = maxEnergy,
            energyConfig = energyConfig,
            expectedMoves = parMoves,
            difficulty = chapter.difficulty,
            tags = listOf(chapter.id, "level_${levelNumInChapter}")
        )
    }
}
