package ir.danialchoopan.lumalogic.domain.level

import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.Level

/**
 * Validates level structural integrity before saving, playing, or importing.
 */
class LevelValidator {

    companion object {
        const val MIN_GRID_SIZE = 2
        const val MAX_GRID_SIZE = 50
    }

    fun validate(level: Level): LevelValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        // 1. Grid Dimension Checks
        if (level.rows < MIN_GRID_SIZE || level.columns < MIN_GRID_SIZE) {
            errors.add("Grid dimensions must be at least ${MIN_GRID_SIZE}x${MIN_GRID_SIZE}. Current: ${level.rows}x${level.columns}.")
        }
        if (level.rows > MAX_GRID_SIZE || level.columns > MAX_GRID_SIZE) {
            errors.add("Grid dimensions exceed maximum allowed (${MAX_GRID_SIZE}x${MAX_GRID_SIZE}). Current: ${level.rows}x${level.columns}.")
        }

        val totalGridCapacity = level.rows * level.columns

        // 2. Component Extraction
        val nonEmptyCells = level.cells.filter { it.type != CellType.EMPTY }
        val sources = level.cells.filter { it.type == CellType.SOURCE }
        val targets = level.cells.filter { it.type == CellType.TARGET }
        val requiredTargets = targets.filter { !it.isOptionalTarget }

        // 3. Source & Target Presence Checks
        if (sources.isEmpty()) {
            errors.add("No Source exists. At least one Light Source is required.")
        }
        if (targets.isEmpty()) {
            errors.add("No Target exists. At least one Target is required.")
        } else if (requiredTargets.isEmpty()) {
            errors.add("At least one required Target is needed.")
        }

        // 4. Duplicate Position & Bounds Checks
        val positionSet = HashSet<Pair<Int, Int>>()
        var duplicateCount = 0

        for (cell in level.cells) {
            if (cell.row !in 0 until level.rows || cell.column !in 0 until level.columns) {
                errors.add("Component '${cell.type}' at (${cell.row}, ${cell.column}) is out of grid bounds.")
            }

            val posKey = Pair(cell.row, cell.column)
            if (positionSet.contains(posKey)) {
                duplicateCount++
            } else {
                positionSet.add(posKey)
            }
        }

        if (duplicateCount > 0) {
            errors.add("Duplicate component positions found on grid ($duplicateCount overlaps).")
        }

        // 5. Component Configuration Validation
        for (cell in nonEmptyCells) {
            when (cell.type) {
                CellType.FILTER -> {
                    if (cell.acceptedColor == null) {
                        warnings.add("Filter at (${cell.row}, ${cell.column}) has no accepted color configured.")
                    }
                }
                CellType.GATE -> {
                    if (cell.gateType == null) {
                        errors.add("Gate at (${cell.row}, ${cell.column}) has no gate type specified.")
                    }
                }
                CellType.SOURCE -> {
                    if (cell.lightColor == null) {
                        warnings.add("Source at (${cell.row}, ${cell.column}) has no explicit color, defaulting to WHITE.")
                    }
                }
                else -> {}
            }
        }

        // 6. Solvability & Layout Warnings
        val mirrors = level.cells.filter { it.type == CellType.MIRROR }
        val splitters = level.cells.filter { it.type == CellType.SPLITTER }
        val gates = level.cells.filter { it.type == CellType.GATE }

        if (mirrors.isEmpty() && splitters.isEmpty() && gates.isEmpty() && requiredTargets.size > 1) {
            warnings.add("This level has no optical components (mirrors/splitters/gates) and may be difficult or impossible to solve.")
        }

        if (level.name.isBlank()) {
            warnings.add("Level name is empty. A descriptive name is recommended.")
        }

        return if (errors.isEmpty()) {
            LevelValidationResult.valid(warnings)
        } else {
            LevelValidationResult.invalid(errors, warnings)
        }
    }
}
