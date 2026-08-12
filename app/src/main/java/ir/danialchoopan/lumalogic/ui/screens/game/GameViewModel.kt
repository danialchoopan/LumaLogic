package ir.danialchoopan.lumalogic.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.Level
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.di.AppContainer
import ir.danialchoopan.lumalogic.domain.command.CommandHistory
import ir.danialchoopan.lumalogic.domain.command.MoveComponentCommand
import ir.danialchoopan.lumalogic.domain.command.RotateComponentCommand
import ir.danialchoopan.lumalogic.domain.debug.DebugSimulationController
import ir.danialchoopan.lumalogic.domain.debug.DebugSimulationState
import ir.danialchoopan.lumalogic.domain.engine.LightTraceResult
import ir.danialchoopan.lumalogic.domain.hint.Hint
import ir.danialchoopan.lumalogic.domain.hint.HintEngine
import ir.danialchoopan.lumalogic.domain.model.GameSnapshot
import ir.danialchoopan.lumalogic.domain.usecase.GetLevelUseCase
import ir.danialchoopan.lumalogic.domain.usecase.MoveCellUseCase
import ir.danialchoopan.lumalogic.domain.usecase.MoveResult
import ir.danialchoopan.lumalogic.domain.usecase.ResetLevelUseCase
import ir.danialchoopan.lumalogic.domain.usecase.RotateCellUseCase
import ir.danialchoopan.lumalogic.domain.usecase.UpdateSimulationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface GameUiState {
    object Loading : GameUiState
    data class Success(
        val level: Level,
        val cells: List<Cell>
    ) : GameUiState
    data class Error(val message: String) : GameUiState
}

class GameViewModel(
    private val getLevelUseCase: GetLevelUseCase = AppContainer.getLevelUseCase,
    private val rotateCellUseCase: RotateCellUseCase = AppContainer.rotateCellUseCase,
    private val moveCellUseCase: MoveCellUseCase = AppContainer.moveCellUseCase,
    private val updateSimulationUseCase: UpdateSimulationUseCase = AppContainer.updateSimulationUseCase,
    private val resetLevelUseCase: ResetLevelUseCase = AppContainer.resetLevelUseCase,
    private val hintEngine: HintEngine = HintEngine(),
    val debugController: DebugSimulationController = DebugSimulationController()
) : ViewModel() {

    private val commandHistory = CommandHistory()

    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _beamPath = MutableStateFlow<List<Position>>(emptyList())
    val beamPath: StateFlow<List<Position>> = _beamPath.asStateFlow()

    private val _beamSegments = MutableStateFlow<List<ir.danialchoopan.lumalogic.domain.model.BeamSegment>>(emptyList())
    val beamSegments: StateFlow<List<ir.danialchoopan.lumalogic.domain.model.BeamSegment>> = _beamSegments.asStateFlow()

    private val _litCells = MutableStateFlow<Set<Position>>(emptySet())
    val litCells: StateFlow<Set<Position>> = _litCells.asStateFlow()

    private val _gameStatus = MutableStateFlow<LightTraceResult?>(null)
    val gameStatus: StateFlow<LightTraceResult?> = _gameStatus.asStateFlow()

    private val _selectedCell = MutableStateFlow<Position?>(null)
    val selectedCell: StateFlow<Position?> = _selectedCell.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Undo / Redo
    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    // Hint System
    private val _remainingHints = MutableStateFlow(3)
    val remainingHints: StateFlow<Int> = _remainingHints.asStateFlow()

    private val _activeHint = MutableStateFlow<Hint?>(null)
    val activeHint: StateFlow<Hint?> = _activeHint.asStateFlow()

    // Debug Mode
    private val _debugState = MutableStateFlow(DebugSimulationState())
    val debugState: StateFlow<DebugSimulationState> = _debugState.asStateFlow()

    private val _isDebugModeActive = MutableStateFlow(false)
    val isDebugModeActive: StateFlow<Boolean> = _isDebugModeActive.asStateFlow()

    init {
        loadLevel()
    }

    fun loadLevel(levelId: String? = null) {
        viewModelScope.launch {
            _uiState.value = GameUiState.Loading
            _selectedCell.value = null
            commandHistory.clear()
            _remainingHints.value = 3
            _activeHint.value = null
            updateUndoRedoStates()
            try {
                val level = getLevelUseCase(levelId)
                AppContainer.gameEngine.loadLevel(level)
                simulate()
            } catch (e: Exception) {
                _uiState.value = GameUiState.Error(e.message ?: "Failed to load level")
            }
        }
    }

    fun startSimulation() {
        simulate()
    }

    fun resetSimulation() {
        resetGame()
    }

    fun simulate() {
        val traceResult = updateSimulationUseCase()
        val grid = AppContainer.gameEngine.getGrid()

        _beamPath.value = traceResult.path
        _beamSegments.value = traceResult.beamSegments
        _litCells.value = traceResult.visitedCells
        _gameStatus.value = traceResult

        val currentState = _uiState.value
        if (currentState is GameUiState.Success) {
            _uiState.value = currentState.copy(cells = grid)
            if (traceResult.success) {
                AppContainer.levelProgressManager.recordLevelCompletion(
                    levelId = currentState.level.levelId,
                    score = 1000,
                    timeSeconds = 30L,
                    hintsUsed = 3 - _remainingHints.value
                )
            }
        } else if (AppContainer.gameEngine.getGrid().isNotEmpty()) {
            val level = AppContainer.levelRepository.getDemoLevel()
            _uiState.value = GameUiState.Success(level = level, cells = grid)
            if (traceResult.success) {
                AppContainer.levelProgressManager.recordLevelCompletion(
                    levelId = level.levelId,
                    score = 1000,
                    timeSeconds = 30L,
                    hintsUsed = 3 - _remainingHints.value
                )
            }
        }

        // Keep debug controller in sync with current trace
        debugController.loadTraceResult(traceResult)
        if (_isDebugModeActive.value) {
            _debugState.value = debugController.getCurrentState()
        }
    }

    private fun createSnapshot(): GameSnapshot {
        val grid = AppContainer.gameEngine.getGrid()
        val level = (uiState.value as? GameUiState.Success)?.level
        val rows = level?.rows ?: 9
        val cols = level?.columns ?: 9
        return GameSnapshot(
            cells = grid,
            rows = rows,
            columns = cols,
            lightTraceResult = _gameStatus.value,
            activatedTargets = _gameStatus.value?.activatedTargets ?: emptySet()
        )
    }

    fun selectCell(position: Position) {
        val currentSelected = _selectedCell.value
        if (currentSelected == position) {
            rotateSelectedCell()
        } else if (currentSelected != null) {
            val grid = AppContainer.gameEngine.getGrid()
            val fromCell = grid.find { it.row == currentSelected.row && it.column == currentSelected.column }
            if (fromCell != null && AppContainer.gameEngine.isMovable(fromCell)) {
                moveCell(currentSelected, position)
            } else {
                _selectedCell.value = position
                rotateCellAt(position)
            }
        } else {
            _selectedCell.value = position
            rotateCellAt(position)
        }
    }

    fun rotateSelectedCell() {
        val pos = _selectedCell.value ?: return
        rotateCellAt(pos)
    }

    fun rotateCellAt(position: Position) {
        viewModelScope.launch {
            val gridBefore = AppContainer.gameEngine.getGrid()
            val cellBefore = gridBefore.find { it.row == position.row && it.column == position.column } ?: return@launch
            if (cellBefore.isLocked) return@launch

            val prevRotation = cellBefore.rotation
            val beforeSnap = createSnapshot()

            rotateCellUseCase(position)
            simulate()

            val gridAfter = AppContainer.gameEngine.getGrid()
            val cellAfter = gridAfter.find { it.row == position.row && it.column == position.column } ?: return@launch
            val afterSnap = createSnapshot()

            val cmd = RotateComponentCommand(
                position = position,
                previousRotation = prevRotation,
                newRotation = cellAfter.rotation,
                beforeState = beforeSnap,
                afterState = afterSnap
            )
            commandHistory.execute(cmd)
            updateUndoRedoStates()

            if (_gameStatus.value?.success == true) {
                _userMessage.value = "Target activated! Level Complete!"
            }
        }
    }

    fun moveCell(from: Position, to: Position) {
        viewModelScope.launch {
            val gridBefore = AppContainer.gameEngine.getGrid()
            val fromCell = gridBefore.find { it.row == from.row && it.column == from.column } ?: return@launch
            val toCell = gridBefore.find { it.row == to.row && it.column == to.column } ?: return@launch

            val beforeSnap = createSnapshot()

            val result = moveCellUseCase(from, to)
            when (result) {
                is MoveResult.Success -> {
                    _selectedCell.value = null
                    simulate()
                    val afterSnap = createSnapshot()

                    val cmd = MoveComponentCommand(
                        fromPosition = from,
                        toPosition = to,
                        sourceCellBefore = fromCell,
                        targetCellBefore = toCell,
                        beforeState = beforeSnap,
                        afterState = afterSnap
                    )
                    commandHistory.execute(cmd)
                    updateUndoRedoStates()

                    if (_gameStatus.value?.success == true) {
                        _userMessage.value = "Target activated! Level Complete!"
                    }
                }
                is MoveResult.Failure -> {
                    _userMessage.value = result.reason
                }
            }
        }
    }

    fun undo() {
        viewModelScope.launch {
            val cmd = commandHistory.undo() ?: return@launch
            val gridBefore = AppContainer.gameEngine.getGrid()
            val gridUndone = cmd.undo(gridBefore)
            AppContainer.gameEngine.setGrid(gridUndone)
            simulate()
            updateUndoRedoStates()
            _userMessage.value = "Undo performed"
        }
    }

    fun redo() {
        viewModelScope.launch {
            val cmd = commandHistory.redo() ?: return@launch
            val gridBefore = AppContainer.gameEngine.getGrid()
            val gridRedone = cmd.execute(gridBefore)
            AppContainer.gameEngine.setGrid(gridRedone)
            simulate()
            updateUndoRedoStates()
            _userMessage.value = "Redo performed"
        }
    }

    private fun updateUndoRedoStates() {
        _canUndo.value = commandHistory.canUndo()
        _canRedo.value = commandHistory.canRedo()
    }

    fun requestHint() {
        val state = uiState.value as? GameUiState.Success ?: return
        if (_remainingHints.value <= 0) {
            _userMessage.value = "No hints remaining for this level (0/3)."
            return
        }

        val hint = hintEngine.analyzeLevel(
            rows = state.level.rows,
            columns = state.level.columns,
            cells = state.cells
        )

        _remainingHints.value -= 1
        _activeHint.value = hint
    }

    fun dismissHint() {
        _activeHint.value = null
    }

    // Debug Simulation Mode
    fun toggleDebugMode(active: Boolean) {
        _isDebugModeActive.value = active
        if (active) {
            _gameStatus.value?.let { trace ->
                debugController.loadTraceResult(trace)
                _debugState.value = debugController.start()
            }
        }
    }

    fun stepDebugForward() {
        _debugState.value = debugController.stepForward()
    }

    fun stepDebugBackward() {
        _debugState.value = debugController.stepBackward()
    }

    fun pauseDebug() {
        _debugState.value = debugController.pause()
    }

    fun resumeDebug() {
        _debugState.value = debugController.resume()
    }

    fun resetDebug() {
        _debugState.value = debugController.reset()
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun setUserMessage(message: String) {
        _userMessage.value = message
    }

    fun resetGame() {
        viewModelScope.launch {
            _selectedCell.value = null
            commandHistory.clear()
            _remainingHints.value = 3
            _activeHint.value = null
            updateUndoRedoStates()
            resetLevelUseCase()
            simulate()
        }
    }

    fun reloadGame() {
        val currentState = _uiState.value
        if (currentState is GameUiState.Success) {
            loadLevel(currentState.level.levelId)
        } else {
            loadLevel()
        }
    }
}
