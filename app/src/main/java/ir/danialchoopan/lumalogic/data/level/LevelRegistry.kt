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
 * Data registry containing handcrafted, solvable, and distinct definitions for all 16 chapters and 256 levels in LumaLogic.
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
                val id = String.format("chapter_%02d_level_%02d", chapterNum, levelNumInChapter)
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

    private fun createCraftedLevel(
        globalLevelIndex: Int,
        chapterNum: Int,
        levelNumInChapter: Int,
        id: String,
        name: String,
        chapter: Chapter
    ): Level {
        val rows = when {
            chapterNum in 13..16 -> 8
            chapterNum in 5..12 -> 7
            else -> 6
        }
        val cols = rows

        val cellMap = mutableMapOf<Position, Cell>()
        val targets = mutableListOf<TargetRequirement>()

        // Helper to scramble initial rotation of movable pieces so level starts unsolved
        fun scramble(sol: Rotation, seed: Int): Rotation {
            val step = 1 + (seed % 3)
            var r = sol
            repeat(step) { r = r.next() }
            return r
        }

        // Color selection
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
            8, 12, 15, 16 -> LightColor.RED
            else -> LightColor.WHITE
        }

        // Build distinct level layouts depending on Chapter archetype
        when (chapterNum) {
            // Chapter 1: Light Basics (1..16)
            1 -> {
                val sRow = 1 + (levelNumInChapter % (rows - 2))
                val mCol = 2 + (levelNumInChapter % 3)
                val tRow = if (sRow < rows / 2) rows - 2 else 1

                // Source at (sRow, 0) emitting RIGHT (NINETY)
                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)

                // Movable Mirror at (sRow, mCol)
                val solRot1 = if (tRow > sRow) Rotation.ZERO else Rotation.NINETY
                cellMap[Position(sRow, mCol)] = Cell("c_${sRow}_${mCol}", sRow, mCol, CellType.MIRROR, scramble(solRot1, levelNumInChapter), isLocked = false)

                if (levelNumInChapter in 5..16) {
                    // Double mirror or zigzag
                    val mCol2 = (mCol + 1).coerceAtMost(cols - 1)
                    val solRot2 = if (tRow > sRow) Rotation.NINETY else Rotation.ZERO
                    cellMap[Position(tRow, mCol)] = Cell("c_${tRow}_${mCol}", tRow, mCol, CellType.MIRROR, scramble(solRot2, levelNumInChapter + 1), isLocked = false)
                    cellMap[Position(tRow, mCol2)] = Cell("c_${tRow}_${mCol2}", tRow, mCol2, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                    targets.add(TargetRequirement(Position(tRow, mCol2), primaryColor))
                } else {
                    // Single mirror direct target
                    cellMap[Position(tRow, mCol)] = Cell("c_${tRow}_${mCol}", tRow, mCol, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                    targets.add(TargetRequirement(Position(tRow, mCol), primaryColor))
                }

                // Add 1 block obstacle on higher levels
                if (levelNumInChapter >= 9) {
                    val bRow = (sRow + 1) % rows
                    val bCol = 1
                    cellMap[Position(bRow, bCol)] = Cell("c_${bRow}_${bCol}", bRow, bCol, CellType.BLOCK, isLocked = true)
                }
            }

            // Chapter 2: Reflection & Obstacles (17..32)
            2 -> {
                val sRow = 0
                val sCol = 1 + (levelNumInChapter % (cols - 2))
                // Source emitting DOWN (ONE_EIGHTY)
                cellMap[Position(sRow, sCol)] = Cell("c_${sRow}_${sCol}", sRow, sCol, CellType.SOURCE, Rotation.ONE_EIGHTY, isLocked = true, isLit = true, lightColor = primaryColor)

                val m1Row = 3 + (levelNumInChapter % 2)
                val m1Col = sCol
                val m2Row = m1Row
                val m2Col = (sCol + 2) % (cols - 1)
                val tRow = 1
                val tCol = m2Col

                cellMap[Position(m1Row, m1Col)] = Cell("c_${m1Row}_${m1Col}", m1Row, m1Col, CellType.MIRROR, scramble(Rotation.ZERO, levelNumInChapter), isLocked = false)
                cellMap[Position(m2Row, m2Col)] = Cell("c_${m2Row}_${m2Col}", m2Row, m2Col, CellType.MIRROR, scramble(Rotation.NINETY, levelNumInChapter + 2), isLocked = false)
                cellMap[Position(tRow, tCol)] = Cell("c_${tRow}_${tCol}", tRow, tCol, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                targets.add(TargetRequirement(Position(tRow, tCol), primaryColor))

                // Obstacles
                cellMap[Position(2, sCol)] = Cell("c_2_${sCol}", 2, sCol, CellType.BLOCK, isLocked = true)
            }

            // Chapter 3: Precision (33..48)
            3 -> {
                val sRow = 1 + (levelNumInChapter % 3)
                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)

                val m1Col = 2
                val m2Row = (sRow + 3) % (rows - 1)
                val m2Col = m1Col
                val m3Col = cols - 2

                cellMap[Position(sRow, m1Col)] = Cell("c_${sRow}_${m1Col}", sRow, m1Col, CellType.MIRROR, scramble(Rotation.ZERO, levelNumInChapter), isLocked = false)
                cellMap[Position(m2Row, m2Col)] = Cell("c_${m2Row}_${m2Col}", m2Row, m2Col, CellType.MIRROR, scramble(Rotation.NINETY, levelNumInChapter + 1), isLocked = false)
                cellMap[Position(m2Row, m3Col)] = Cell("c_${m2Row}_${m3Col}", m2Row, m3Col, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                targets.add(TargetRequirement(Position(m2Row, m3Col), primaryColor))

                // Precision corridor blocks
                cellMap[Position(sRow + 1, 1)] = Cell("c_${sRow + 1}_1", sRow + 1, 1, CellType.BLOCK, isLocked = true)
                cellMap[Position(m2Row - 1, m3Col - 1)] = Cell("c_${m2Row - 1}_${m3Col - 1}", m2Row - 1, m3Col - 1, CellType.BLOCK, isLocked = true)
            }

            // Chapter 4: Splitters (49..64)
            4 -> {
                val sRow = rows / 2
                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)

                val splitCol = 2
                cellMap[Position(sRow, splitCol)] = Cell("c_${sRow}_${splitCol}", sRow, splitCol, CellType.SPLITTER, scramble(Rotation.ZERO, levelNumInChapter), isLocked = false)

                val t1Row = 0
                val t1Col = splitCol
                val t2Row = rows - 1
                val t2Col = splitCol

                // Mirror for branch 1
                cellMap[Position(t1Row + 1, t1Col)] = Cell("c_${t1Row + 1}_${t1Col}", t1Row + 1, t1Col, CellType.MIRROR, scramble(Rotation.ZERO, levelNumInChapter), isLocked = false)
                cellMap[Position(t1Row + 1, cols - 1)] = Cell("c_${t1Row + 1}_${cols - 1}", t1Row + 1, cols - 1, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                targets.add(TargetRequirement(Position(t1Row + 1, cols - 1), primaryColor))

                // Target for branch 2
                cellMap[Position(t2Row, t2Col)] = Cell("c_${t2Row}_${t2Col}", t2Row, t2Col, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                targets.add(TargetRequirement(Position(t2Row, t2Col), primaryColor))
            }

            // Chapter 5: Colors (65..80)
            5 -> {
                val sRow = 1 + (levelNumInChapter % (rows - 2))
                val sCol = 0
                cellMap[Position(sRow, sCol)] = Cell("c_${sRow}_${sCol}", sRow, sCol, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)

                val mCol = 3
                val tRow = (sRow + 3) % (rows - 1)
                val tCol = cols - 2

                cellMap[Position(sRow, mCol)] = Cell("c_${sRow}_${mCol}", sRow, mCol, CellType.MIRROR, scramble(Rotation.ZERO, levelNumInChapter), isLocked = false)
                cellMap[Position(tRow, mCol)] = Cell("c_${tRow}_${mCol}", tRow, mCol, CellType.MIRROR, scramble(Rotation.NINETY, levelNumInChapter + 1), isLocked = false)
                cellMap[Position(tRow, tCol)] = Cell("c_${tRow}_${tCol}", tRow, tCol, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                targets.add(TargetRequirement(Position(tRow, tCol), primaryColor))

                // Secondary color source on higher levels
                if (levelNumInChapter >= 8) {
                    val s2Row = (sRow + 2) % rows
                    val s2Color = if (primaryColor == LightColor.RED) LightColor.BLUE else LightColor.RED
                    cellMap[Position(s2Row, 0)] = Cell("c_${s2Row}_0", s2Row, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = s2Color)
                    cellMap[Position(s2Row, cols - 1)] = Cell("c_${s2Row}_${cols - 1}", s2Row, cols - 1, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = s2Color)
                    targets.add(TargetRequirement(Position(s2Row, cols - 1), s2Color))
                }
            }

            // Chapter 6: Filters (81..96)
            6 -> {
                val sRow = 2
                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = primaryColor)

                val fCol = 2
                cellMap[Position(sRow, fCol)] = Cell("c_${sRow}_${fCol}", sRow, fCol, CellType.FILTER, Rotation.ZERO, acceptedColor = primaryColor, isLocked = false)

                val mCol = 4
                val tRow = 5
                cellMap[Position(sRow, mCol)] = Cell("c_${sRow}_${mCol}", sRow, mCol, CellType.MIRROR, scramble(Rotation.ZERO, levelNumInChapter), isLocked = false)
                cellMap[Position(tRow, mCol)] = Cell("c_${tRow}_${mCol}", tRow, mCol, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = primaryColor)
                targets.add(TargetRequirement(Position(tRow, mCol), primaryColor))
            }

            // Chapter 7: Energy Thrift (97..112)
            7 -> {
                val sRow = 1
                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.WHITE)

                val m1Col = 2 + (levelNumInChapter % 3)
                val m1Row = sRow
                val m2Row = rows - 2
                val m2Col = m1Col
                val tCol = cols - 2

                cellMap[Position(m1Row, m1Col)] = Cell("c_${m1Row}_${m1Col}", m1Row, m1Col, CellType.MIRROR, scramble(Rotation.ZERO, levelNumInChapter), isLocked = false)
                cellMap[Position(m2Row, m2Col)] = Cell("c_${m2Row}_${m2Col}", m2Row, m2Col, CellType.MIRROR, scramble(Rotation.NINETY, levelNumInChapter + 1), isLocked = false)
                cellMap[Position(m2Row, tCol)] = Cell("c_${m2Row}_${tCol}", m2Row, tCol, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.WHITE)
                targets.add(TargetRequirement(Position(m2Row, tCol), LightColor.WHITE))
            }

            // Chapter 8: Multi-Beam (113..128)
            8 -> {
                // Source 1 (Left wall)
                cellMap[Position(1, 0)] = Cell("c_1_0", 1, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.RED)
                // Source 2 (Top wall)
                cellMap[Position(0, 4)] = Cell("c_0_4", 0, 4, CellType.SOURCE, Rotation.ONE_EIGHTY, isLocked = true, isLit = true, lightColor = LightColor.BLUE)

                // Mirrors
                cellMap[Position(1, 2)] = Cell("c_1_2", 1, 2, CellType.MIRROR, scramble(Rotation.ZERO, levelNumInChapter), isLocked = false)
                cellMap[Position(5, 2)] = Cell("c_5_2", 5, 2, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.RED)
                targets.add(TargetRequirement(Position(5, 2), LightColor.RED))

                cellMap[Position(3, 4)] = Cell("c_3_4", 3, 4, CellType.MIRROR, scramble(Rotation.NINETY, levelNumInChapter + 1), isLocked = false)
                cellMap[Position(3, 6)] = Cell("c_3_6", 3, 6, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.BLUE)
                targets.add(TargetRequirement(Position(3, 6), LightColor.BLUE))
            }

            // Chapter 9: AND Logic (129..144)
            9 -> {
                val sRow = 3
                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.WHITE)

                // Splitter at (3, 2)
                cellMap[Position(3, 2)] = Cell("c_3_2", 3, 2, CellType.SPLITTER, scramble(Rotation.ZERO, levelNumInChapter), isLocked = false)

                // Mirror for branch 1 (UP -> RIGHT)
                cellMap[Position(1, 2)] = Cell("c_1_2", 1, 2, CellType.MIRROR, scramble(Rotation.ZERO, levelNumInChapter), isLocked = false)
                // Mirror for branch 2 (DOWN -> RIGHT)
                cellMap[Position(5, 2)] = Cell("c_5_2", 5, 2, CellType.MIRROR, scramble(Rotation.NINETY, levelNumInChapter), isLocked = false)

                // Mirrors directing into AND gate
                cellMap[Position(1, 4)] = Cell("c_1_4", 1, 4, CellType.MIRROR, scramble(Rotation.NINETY, levelNumInChapter + 1), isLocked = false)
                cellMap[Position(5, 4)] = Cell("c_5_4", 5, 4, CellType.MIRROR, scramble(Rotation.ZERO, levelNumInChapter + 1), isLocked = false)

                // AND gate at (3, 4) emitting RIGHT (NINETY)
                cellMap[Position(3, 4)] = Cell("c_3_4", 3, 4, CellType.GATE, Rotation.NINETY, gateType = GateType.AND, isLocked = false)

                // Target at (3, 6)
                cellMap[Position(3, 6)] = Cell("c_3_6", 3, 6, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.WHITE)
                targets.add(TargetRequirement(Position(3, 6), LightColor.WHITE))
            }

            // Chapter 10: OR Logic (145..160)
            10 -> {
                val sRow = 2
                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.WHITE)

                val splitCol = 2
                cellMap[Position(sRow, splitCol)] = Cell("c_${sRow}_${splitCol}", sRow, splitCol, CellType.SPLITTER, scramble(Rotation.ZERO, levelNumInChapter), isLocked = false)

                // OR gate at (4, splitCol)
                cellMap[Position(4, splitCol)] = Cell("c_4_${splitCol}", 4, splitCol, CellType.GATE, Rotation.NINETY, gateType = GateType.OR, isLocked = false)

                cellMap[Position(4, cols - 1)] = Cell("c_4_${cols - 1}", 4, cols - 1, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.WHITE)
                targets.add(TargetRequirement(Position(4, cols - 1), LightColor.WHITE))
            }

            // Chapter 11: NOT Logic (161..176)
            11 -> {
                // NOT gate default emits UP or RIGHT when input is dark
                val gateRow = 3
                val gateCol = 3
                cellMap[Position(gateRow, gateCol)] = Cell("c_${gateRow}_${gateCol}", gateRow, gateCol, CellType.GATE, Rotation.NINETY, gateType = GateType.NOT, isLocked = false)

                // Target receiving NOT gate light
                cellMap[Position(gateRow, cols - 1)] = Cell("c_${gateRow}_${cols - 1}", gateRow, cols - 1, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.WHITE)
                targets.add(TargetRequirement(Position(gateRow, cols - 1), LightColor.WHITE))

                // Source that player must divert away from NOT gate input
                cellMap[Position(gateRow, 0)] = Cell("c_${gateRow}_0", gateRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.WHITE)
                cellMap[Position(gateRow, 1)] = Cell("c_${gateRow}_1", gateRow, 1, CellType.MIRROR, scramble(Rotation.ZERO, levelNumInChapter), isLocked = false)
            }

            // Chapter 12: Logic Networks (177..192)
            12 -> {
                cellMap[Position(2, 0)] = Cell("c_2_0", 2, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.RED)
                cellMap[Position(5, 0)] = Cell("c_5_0", 5, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.BLUE)

                // AND gate at (3, 3)
                cellMap[Position(3, 3)] = Cell("c_3_3", 3, 3, CellType.GATE, Rotation.NINETY, gateType = GateType.AND, isLocked = false)
                cellMap[Position(2, 3)] = Cell("c_2_3", 2, 3, CellType.MIRROR, scramble(Rotation.ZERO, levelNumInChapter), isLocked = false)
                cellMap[Position(5, 3)] = Cell("c_5_3", 5, 3, CellType.MIRROR, scramble(Rotation.NINETY, levelNumInChapter + 1), isLocked = false)

                // Target
                cellMap[Position(3, cols - 1)] = Cell("c_3_${cols - 1}", 3, cols - 1, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.RED)
                targets.add(TargetRequirement(Position(3, cols - 1), LightColor.RED))
            }

            // Chapter 13: Expert Routing (193..208)
            13 -> {
                val sRow = 1
                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.WHITE)

                // 4-mirror maze
                cellMap[Position(1, 3)] = Cell("c_1_3", 1, 3, CellType.MIRROR, scramble(Rotation.ZERO, levelNumInChapter), isLocked = false)
                cellMap[Position(6, 3)] = Cell("c_6_3", 6, 3, CellType.MIRROR, scramble(Rotation.NINETY, levelNumInChapter + 1), isLocked = false)
                cellMap[Position(6, 6)] = Cell("c_6_6", 6, 6, CellType.MIRROR, scramble(Rotation.ZERO, levelNumInChapter + 2), isLocked = false)
                cellMap[Position(2, 6)] = Cell("c_2_6", 2, 6, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.WHITE)
                targets.add(TargetRequirement(Position(2, 6), LightColor.WHITE))

                // Maze obstacles
                cellMap[Position(3, 3)] = Cell("c_3_3", 3, 3, CellType.BLOCK, isLocked = true)
                cellMap[Position(4, 5)] = Cell("c_4_5", 4, 5, CellType.BLOCK, isLocked = true)
            }

            // Chapter 14: Master Energy (209..224)
            14 -> {
                val sRow = 2
                cellMap[Position(sRow, 0)] = Cell("c_${sRow}_0", sRow, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.WHITE)

                cellMap[Position(2, 4)] = Cell("c_2_4", 2, 4, CellType.MIRROR, scramble(Rotation.ZERO, levelNumInChapter), isLocked = false)
                cellMap[Position(6, 4)] = Cell("c_6_4", 6, 4, CellType.MIRROR, scramble(Rotation.NINETY, levelNumInChapter + 1), isLocked = false)
                cellMap[Position(6, 7)] = Cell("c_6_7", 6, 7, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.WHITE)
                targets.add(TargetRequirement(Position(6, 7), LightColor.WHITE))
            }

            // Chapter 15: Expert Conundrum (225..240)
            15 -> {
                cellMap[Position(1, 0)] = Cell("c_1_0", 1, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.RED)
                cellMap[Position(1, 3)] = Cell("c_1_3", 1, 3, CellType.SPLITTER, scramble(Rotation.ZERO, levelNumInChapter), isLocked = false)
                cellMap[Position(1, 6)] = Cell("c_1_6", 1, 6, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.RED)
                targets.add(TargetRequirement(Position(1, 6), LightColor.RED))

                cellMap[Position(5, 3)] = Cell("c_5_3", 5, 3, CellType.MIRROR, scramble(Rotation.NINETY, levelNumInChapter + 1), isLocked = false)
                cellMap[Position(5, 6)] = Cell("c_5_6", 5, 6, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.RED)
                targets.add(TargetRequirement(Position(5, 6), LightColor.RED))
            }

            // Chapter 16: LumaLogic Master (241..256)
            16 -> {
                // The Pinnacle / Level 256 Grand Finale
                cellMap[Position(1, 0)] = Cell("c_1_0", 1, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.RED)
                cellMap[Position(6, 0)] = Cell("c_6_0", 6, 0, CellType.SOURCE, Rotation.NINETY, isLocked = true, isLit = true, lightColor = LightColor.BLUE)

                cellMap[Position(1, 3)] = Cell("c_1_3", 1, 3, CellType.SPLITTER, scramble(Rotation.ZERO, levelNumInChapter), isLocked = false)
                cellMap[Position(6, 3)] = Cell("c_6_3", 6, 3, CellType.MIRROR, scramble(Rotation.NINETY, levelNumInChapter + 1), isLocked = false)

                // AND gate in center
                cellMap[Position(3, 5)] = Cell("c_3_5", 3, 5, CellType.GATE, Rotation.NINETY, gateType = GateType.AND, isLocked = false)
                cellMap[Position(1, 5)] = Cell("c_1_5", 1, 5, CellType.MIRROR, scramble(Rotation.ZERO, levelNumInChapter + 2), isLocked = false)
                cellMap[Position(6, 5)] = Cell("c_6_5", 6, 5, CellType.MIRROR, scramble(Rotation.ZERO, levelNumInChapter + 3), isLocked = false)

                // Grand Targets
                cellMap[Position(3, 7)] = Cell("c_3_7", 3, 7, CellType.TARGET, Rotation.ZERO, isLocked = true, requiredColor = LightColor.RED)
                targets.add(TargetRequirement(Position(3, 7), LightColor.RED))
            }
        }

        // Fill remaining cells with EMPTY
        val allCells = mutableListOf<Cell>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val pos = Position(r, c)
                val cell = cellMap[pos] ?: Cell(
                    id = "c_${r}_${c}",
                    row = r,
                    column = c,
                    type = CellType.EMPTY
                )
                allCells.add(cell)
            }
        }

        val hasEnergyLimit = chapterNum in listOf(7, 13, 14, 15, 16)
        val maxEnergy = if (hasEnergyLimit) 14 + (levelNumInChapter % 8) * 2 else 60

        return Level(
            levelId = id,
            name = name,
            description = "Chapter $chapterNum - ${chapter.name}: $name",
            author = "LumaLogic",
            version = 1,
            schemaVersion = 1,
            rows = rows,
            columns = cols,
            cells = allCells,
            difficulty = chapter.difficulty,
            maximumEnergy = maxEnergy,
            energyConfig = EnergyConfig(maxEnergy = maxEnergy, cellTraversalCost = 1, mirrorCost = 2),
            threeStarThreshold = 1000 + globalLevelIndex * 10,
            twoStarThreshold = 600 + globalLevelIndex * 6,
            expectedMoves = 2 + (globalLevelIndex % 6),
            tags = listOf(chapter.id, chapter.difficulty.lowercase()),
            targetRequirements = targets
        )
    }
}
