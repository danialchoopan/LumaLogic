package ir.danialchoopan.lumalogic.ui.screens.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.GateType
import ir.danialchoopan.lumalogic.data.model.Level
import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.data.model.Rotation
import ir.danialchoopan.lumalogic.di.AppContainer
import ir.danialchoopan.lumalogic.domain.command.CommandHistory
import ir.danialchoopan.lumalogic.domain.command.GameCommand
import ir.danialchoopan.lumalogic.domain.level.LevelManager
import ir.danialchoopan.lumalogic.domain.level.LevelValidationResult
import ir.danialchoopan.lumalogic.domain.model.GameSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class EditorTool {
    SELECT,
    ERASE,
    SOURCE,
    TARGET,
    MIRROR,
    SPLITTER,
    FILTER,
    WIRE,
    BLOCK,
    GATE_AND,
    GATE_OR,
    GATE_NOT
}

class LevelEditorViewModel(
    private val levelManager: LevelManager = AppContainer.levelManager
) : ViewModel() {

    private val commandHistory = CommandHistory()

    private val _level = MutableStateFlow<Level>(createNewLevelInternal())
    val level: StateFlow<Level> = _level.asStateFlow()

    private val _selectedTool = MutableStateFlow<EditorTool>(EditorTool.SELECT)
    val selectedTool: StateFlow<EditorTool> = _selectedTool.asStateFlow()

    private val _selectedCellPosition = MutableStateFlow<Position?>(null)
    val selectedCellPosition: StateFlow<Position?> = _selectedCellPosition.asStateFlow()

    private val _validationResult = MutableStateFlow<LevelValidationResult?>(null)
    val validationResult: StateFlow<LevelValidationResult?> = _validationResult.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    fun loadLevel(levelId: String) {
        val existing = levelManager.getLevelById(levelId)
        if (existing != null) {
            _level.value = existing
        } else {
            _level.value = createNewLevelInternal()
        }
        commandHistory.clear()
        updateUndoRedo()
    }

    fun createNewLevel(rows: Int = 7, columns: Int = 7) {
        _level.value = createNewLevelInternal(rows, columns)
        _selectedCellPosition.value = null
        commandHistory.clear()
        updateUndoRedo()
    }

    private fun createNewLevelInternal(rows: Int = 7, columns: Int = 7): Level {
        val cells = mutableListOf<Cell>()
        for (r in 0 until rows) {
            for (c in 0 until columns) {
                cells.add(Cell(id = "c_${r}_${c}", row = r, column = c, type = CellType.EMPTY))
            }
        }
        return Level(
            levelId = levelManager.generateUniqueUserLevelId(),
            name = "Custom Puzzle",
            author = "User Author",
            difficulty = "Medium",
            rows = rows,
            columns = columns,
            cells = cells,
            isUserCreated = true
        )
    }

    fun selectTool(tool: EditorTool) {
        _selectedTool.value = tool
    }

    fun onCellClicked(position: Position) {
        val currentLevel = _level.value
        val currentCells = currentLevel.cells
        val targetIndex = currentCells.indexOfFirst { it.row == position.row && it.column == position.column }
        if (targetIndex == -1) return

        val existingCell = currentCells[targetIndex]
        val activeTool = _selectedTool.value

        when (activeTool) {
            EditorTool.SELECT -> {
                if (_selectedCellPosition.value == position && existingCell.type != CellType.EMPTY) {
                    // Tap selected cell again -> Rotate component
                    rotateCellAt(position)
                } else {
                    _selectedCellPosition.value = position
                }
            }

            EditorTool.ERASE -> {
                if (existingCell.type != CellType.EMPTY) {
                    val emptyCell = Cell(id = existingCell.id, row = position.row, column = position.column, type = CellType.EMPTY)
                    updateCellInGrid(position, emptyCell)
                }
            }

            else -> {
                // Place component
                val newCell = createCellForTool(activeTool, existingCell.id, position)
                updateCellInGrid(position, newCell)
                _selectedCellPosition.value = position
            }
        }
    }

    private fun createCellForTool(tool: EditorTool, id: String, pos: Position): Cell {
        return when (tool) {
            EditorTool.SOURCE -> Cell(id = id, row = pos.row, column = pos.column, type = CellType.SOURCE, rotation = Rotation.NINETY, lightColor = LightColor.WHITE)
            EditorTool.TARGET -> Cell(id = id, row = pos.row, column = pos.column, type = CellType.TARGET, requiredColor = LightColor.WHITE)
            EditorTool.MIRROR -> Cell(id = id, row = pos.row, column = pos.column, type = CellType.MIRROR, rotation = Rotation.ZERO)
            EditorTool.SPLITTER -> Cell(id = id, row = pos.row, column = pos.column, type = CellType.SPLITTER, rotation = Rotation.ZERO)
            EditorTool.FILTER -> Cell(id = id, row = pos.row, column = pos.column, type = CellType.FILTER, acceptedColor = LightColor.RED)
            EditorTool.WIRE -> Cell(id = id, row = pos.row, column = pos.column, type = CellType.WIRE)
            EditorTool.BLOCK -> Cell(id = id, row = pos.row, column = pos.column, type = CellType.BLOCK)
            EditorTool.GATE_AND -> Cell(id = id, row = pos.row, column = pos.column, type = CellType.GATE, gateType = GateType.AND, rotation = Rotation.NINETY)
            EditorTool.GATE_OR -> Cell(id = id, row = pos.row, column = pos.column, type = CellType.GATE, gateType = GateType.OR, rotation = Rotation.NINETY)
            EditorTool.GATE_NOT -> Cell(id = id, row = pos.row, column = pos.column, type = CellType.GATE, gateType = GateType.NOT, rotation = Rotation.NINETY)
            else -> Cell(id = id, row = pos.row, column = pos.column, type = CellType.EMPTY)
        }
    }

    fun rotateCellAt(position: Position) {
        val currentCells = _level.value.cells
        val index = currentCells.indexOfFirst { it.row == position.row && it.column == position.column }
        if (index == -1) return
        val cell = currentCells[index]
        if (cell.type == CellType.EMPTY) return

        val rotatedCell = cell.copy(rotation = cell.rotation.next())
        updateCellInGrid(position, rotatedCell)
    }

    fun updateSelectedCellSourceColor(color: LightColor) {
        val pos = _selectedCellPosition.value ?: return
        val cell = _level.value.cells.find { it.row == pos.row && it.column == pos.column } ?: return
        if (cell.type == CellType.SOURCE) {
            updateCellInGrid(pos, cell.copy(lightColor = color))
        }
    }

    fun updateSelectedCellTargetColor(color: LightColor) {
        val pos = _selectedCellPosition.value ?: return
        val cell = _level.value.cells.find { it.row == pos.row && it.column == pos.column } ?: return
        if (cell.type == CellType.TARGET) {
            updateCellInGrid(pos, cell.copy(requiredColor = color))
        }
    }

    fun toggleSelectedCellOptionalTarget(isOptional: Boolean) {
        val pos = _selectedCellPosition.value ?: return
        val cell = _level.value.cells.find { it.row == pos.row && it.column == pos.column } ?: return
        if (cell.type == CellType.TARGET) {
            updateCellInGrid(pos, cell.copy(isOptionalTarget = isOptional))
        }
    }

    fun updateSelectedCellFilterColor(color: LightColor) {
        val pos = _selectedCellPosition.value ?: return
        val cell = _level.value.cells.find { it.row == pos.row && it.column == pos.column } ?: return
        if (cell.type == CellType.FILTER) {
            updateCellInGrid(pos, cell.copy(acceptedColor = color))
        }
    }

    private fun updateCellInGrid(pos: Position, newCell: Cell) {
        val oldLevel = _level.value
        val updatedCells = oldLevel.cells.map {
            if (it.row == pos.row && it.column == pos.column) newCell else it
        }

        val cmd = EditorGridCommand(oldLevel, oldLevel.copy(cells = updatedCells))
        cmd.execute(oldLevel.cells)
        commandHistory.execute(cmd)

        _level.value = oldLevel.copy(cells = updatedCells)
        updateUndoRedo()
    }

    fun updateGridDimensions(newRows: Int, newCols: Int) {
        if (newRows < 2 || newCols < 2 || newRows > 50 || newCols > 50) return
        val oldLevel = _level.value
        val newCells = mutableListOf<Cell>()

        for (r in 0 until newRows) {
            for (c in 0 until newCols) {
                val existing = oldLevel.cells.find { it.row == r && it.column == c }
                if (existing != null) {
                    newCells.add(existing)
                } else {
                    newCells.add(Cell(id = "c_${r}_${c}", row = r, column = c, type = CellType.EMPTY))
                }
            }
        }

        _level.value = oldLevel.copy(rows = newRows, columns = newCols, cells = newCells)
    }

    fun updateMetadata(name: String, author: String, difficulty: String, description: String) {
        _level.value = _level.value.copy(
            name = name,
            author = author,
            difficulty = difficulty,
            description = description
        )
    }

    fun undo() {
        val cmd = commandHistory.undo() as? EditorGridCommand ?: return
        _level.value = cmd.beforeLevel
        updateUndoRedo()
    }

    fun redo() {
        val cmd = commandHistory.redo() as? EditorGridCommand ?: return
        _level.value = cmd.afterLevel
        updateUndoRedo()
    }

    private fun updateUndoRedo() {
        _canUndo.value = commandHistory.canUndo()
        _canRedo.value = commandHistory.canRedo()
    }

    fun saveLevel(): Boolean {
        val current = _level.value
        val result = levelManager.saveUserLevel(current)
        _validationResult.value = result
        if (result.isValid) {
            _message.value = "Level '${current.name}' saved successfully!"
            return true
        }
        return false
    }

    fun validateCurrentLevel(): LevelValidationResult {
        val result = levelManager.validateLevel(_level.value)
        _validationResult.value = result
        return result
    }

    fun clearValidationResult() {
        _validationResult.value = null
    }

    fun clearMessage() {
        _message.value = null
    }

    private class EditorGridCommand(
        val beforeLevel: Level,
        val afterLevel: Level
    ) : GameCommand {
        override val beforeState: GameSnapshot = GameSnapshot(beforeLevel.cells, beforeLevel.rows, beforeLevel.columns)
        override val afterState: GameSnapshot = GameSnapshot(afterLevel.cells, afterLevel.rows, afterLevel.columns)
        override fun execute(cells: List<Cell>): List<Cell> = afterLevel.cells
        override fun undo(cells: List<Cell>): List<Cell> = beforeLevel.cells
    }
}
