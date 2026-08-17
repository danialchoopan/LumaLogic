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
import ir.danialchoopan.lumalogic.domain.engine.StopReason
import ir.danialchoopan.lumalogic.domain.hint.Hint
import ir.danialchoopan.lumalogic.domain.hint.HintEngine
import ir.danialchoopan.lumalogic.domain.model.EnergyState
import ir.danialchoopan.lumalogic.domain.model.GameCompletionResult
import ir.danialchoopan.lumalogic.domain.model.GameSnapshot
import ir.danialchoopan.lumalogic.domain.score.ScoreCalculator
import ir.danialchoopan.lumalogic.domain.usecase.GetLevelUseCase
import ir.danialchoopan.lumalogic.domain.usecase.MoveCellUseCase
import ir.danialchoopan.lumalogic.domain.usecase.MoveResult
import ir.danialchoopan.lumalogic.domain.usecase.ResetLevelUseCase
import ir.danialchoopan.lumalogic.domain.usecase.RotateCellUseCase
import ir.danialchoopan.lumalogic.domain.usecase.UpdateSimulationUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
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

    // Energy & Gameplay Stats
    private val _energyState = MutableStateFlow(EnergyState())
    val energyState: StateFlow<EnergyState> = _energyState.asStateFlow()

    private val _movesCount = MutableStateFlow(0)
    val movesCount: StateFlow<Int> = _movesCount.asStateFlow()

    private val _timeSeconds = MutableStateFlow(0L)
    val timeSeconds: StateFlow<Long> = _timeSeconds.asStateFlow()

    // Game Control States
    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _isWin = MutableStateFlow(false)
    val isWin: StateFlow<Boolean> = _isWin.asStateFlow()

    private val _isCelebrating = MutableStateFlow(false)
    val isCelebrating: StateFlow<Boolean> = _isCelebrating.asStateFlow()

    private val _isLose = MutableStateFlow(false)
    val isLose: StateFlow<Boolean> = _isLose.asStateFlow()

    private val _completionResult = MutableStateFlow<GameCompletionResult?>(null)
    val completionResult: StateFlow<GameCompletionResult?> = _completionResult.asStateFlow()

    // Debug Mode
    private val _debugState = MutableStateFlow(DebugSimulationState())
    val debugState: StateFlow<DebugSimulationState> = _debugState.asStateFlow()

    private val _isDebugModeActive = MutableStateFlow(false)
    val isDebugModeActive: StateFlow<Boolean> = _isDebugModeActive.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadLevel()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                if (!_isPaused.value && !_isWin.value && !_isLose.value) {
                    _timeSeconds.value += 1L
                }
            }
        }
    }

    /**
     * Loads a level by ID and initializes game state.
     *
     * DEVELOPER NOTE FOR FUTURE MAINTAINERS:
     * - Resets all transient session state (undo history, moves count, hints, timers).
     * - `simulate(isInitialLoad = true)` runs light tracing so the initial laser beams
     *   are immediately visible on screen without triggering a false victory screen.
     */
    fun loadLevel(levelId: String? = null) {
        viewModelScope.launch {
            _uiState.value = GameUiState.Loading
            _selectedCell.value = null
            commandHistory.clear()
            _remainingHints.value = 3
            _activeHint.value = null
            _movesCount.value = 0
            _timeSeconds.value = 0L
            _isPaused.value = false
            _isWin.value = false
            _isCelebrating.value = false
            _isLose.value = false
            _completionResult.value = null
            updateUndoRedoStates()
            try {
                val level = getLevelUseCase(levelId)
                AppContainer.levelProgressManager.recordAttempt(level.levelId)
                AppContainer.gameEngine.loadLevel(level)
                simulate(isInitialLoad = true)
                startTimer()
            } catch (e: Exception) {
                _uiState.value = GameUiState.Error(e.message ?: "Failed to load level")
            }
        }
    }

    fun pauseGame() {
        _isPaused.value = true
    }

    fun resumeGame() {
        _isPaused.value = false
    }

    fun togglePause() {
        _isPaused.value = !_isPaused.value
        AppContainer.audioManager.playButtonClick()
    }

    /**
     * Core Simulation Loop:
     * Executes ray-tracing across the grid, computes energy consumption,
     * updates reactive StateFlows for Compose rendering, and evaluates win/loss states.
     *
     * DEVELOPER NOTE:
     * - `isInitialLoad` prevents auto-win popups from firing instantly on initial frame.
     * - Win state is triggered only when all required targets are satisfied and user has engaged.
     */
    fun simulate(isInitialLoad: Boolean = false) {
        val traceResult = updateSimulationUseCase()
        val grid = AppContainer.gameEngine.getGrid()

        _beamPath.value = traceResult.path
        _beamSegments.value = traceResult.beamSegments
        _litCells.value = traceResult.visitedCells
        _gameStatus.value = traceResult

        val currentLevel = (uiState.value as? GameUiState.Success)?.level ?: AppContainer.levelRepository.getDemoLevel()
        val effectiveEnergyState = traceResult.energyState.copy(maximum = currentLevel.maximumEnergy)
        _energyState.value = effectiveEnergyState

        val currentState = _uiState.value
        if (currentState !is GameUiState.Success && grid.isNotEmpty()) {
            _uiState.value = GameUiState.Success(level = currentLevel, cells = grid)
        } else if (currentState is GameUiState.Success) {
            _uiState.value = currentState.copy(cells = grid)
        }

        // Check Win state: Puzzle targets satisfied (guarded against zero-move initial load)
        val canTriggerWin = !isInitialLoad || _movesCount.value > 0
        if (traceResult.success && canTriggerWin && !_isWin.value && !_isCelebrating.value) {
            _isCelebrating.value = true
            timerJob?.cancel() // Stop timer immediately on victory

            val hintsUsed = 3 - _remainingHints.value
            val optActivated = traceResult.activatedTargets.size - (currentLevel.cells.count { it.type == ir.danialchoopan.lumalogic.data.model.CellType.TARGET && !it.isOptionalTarget })
            val scoreResult = ScoreCalculator.calculateScore(
                level = currentLevel,
                levelCompleted = true,
                energyRemaining = effectiveEnergyState.remaining,
                movesCount = _movesCount.value,
                timeSeconds = _timeSeconds.value,
                hintsUsed = hintsUsed,
                optionalTargetsActivated = optActivated.coerceAtLeast(0)
            )
            val stars = ScoreCalculator.calculateStars(currentLevel, scoreResult, true)

            AppContainer.levelProgressManager.recordLevelCompletion(
                levelId = currentLevel.levelId,
                stars = stars,
                score = scoreResult.totalScore,
                timeSeconds = _timeSeconds.value,
                moves = _movesCount.value,
                hintsUsed = hintsUsed
            )

            val updatedProgress = AppContainer.levelProgressManager.getProgress(currentLevel.levelId)
            val updatedStats = AppContainer.levelProgressManager.getPlayerStats()
            AppContainer.achievementRepository.checkAndUnlockAchievements(updatedStats, updatedProgress)

            val completion = GameCompletionResult(
                levelId = currentLevel.levelId,
                levelName = currentLevel.name,
                isWin = true,
                scoreResult = scoreResult,
                stars = stars,
                energyState = effectiveEnergyState,
                movesCount = _movesCount.value,
                timeSeconds = _timeSeconds.value,
                hintsUsed = hintsUsed,
                optionalTargetsActivated = optActivated.coerceAtLeast(0)
            )
            _completionResult.value = completion

            AppContainer.audioManager.playLevelComplete()
            AppContainer.hapticManager.performLevelComplete()

            // Wait 2 seconds with fanfare and particle celebration before presenting completion dialog
            viewModelScope.launch {
                kotlinx.coroutines.delay(2000L)
                _isCelebrating.value = false
                _isWin.value = true
            }
        }
        // Check Lose state (Energy depleted without achieving targets)
        else if (!traceResult.success && (effectiveEnergyState.isDepleted || traceResult.stoppedReason == StopReason.OUT_OF_ENERGY) && !_isLose.value && !_isWin.value && !_isCelebrating.value) {
            _isLose.value = true
            AppContainer.audioManager.playLevelFailed()
            AppContainer.hapticManager.performLevelFailed()
        }

        // Sync debug controller
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
        if (_isPaused.value || _isWin.value || _isLose.value) return
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
        if (_isPaused.value || _isWin.value || _isLose.value) return
        viewModelScope.launch {
            val gridBefore = AppContainer.gameEngine.getGrid()
            val cellBefore = gridBefore.find { it.row == position.row && it.column == position.column } ?: return@launch
            if (cellBefore.isLocked) return@launch

            val prevRotation = cellBefore.rotation
            val beforeSnap = createSnapshot()

            rotateCellUseCase(position)
            _movesCount.value += 1
            AppContainer.audioManager.playMirrorRotate()
            AppContainer.hapticManager.performRotate()

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
        }
    }

    fun moveCell(from: Position, to: Position) {
        if (_isPaused.value || _isWin.value || _isLose.value) return
        viewModelScope.launch {
            val gridBefore = AppContainer.gameEngine.getGrid()
            val fromCell = gridBefore.find { it.row == from.row && it.column == from.column } ?: return@launch
            val toCell = gridBefore.find { it.row == to.row && it.column == to.column } ?: return@launch

            val beforeSnap = createSnapshot()

            val result = moveCellUseCase(from, to)
            when (result) {
                is MoveResult.Success -> {
                    _selectedCell.value = null
                    _movesCount.value += 1
                    AppContainer.audioManager.playComponentMove()
                    AppContainer.hapticManager.performRotate()

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
                }
                is MoveResult.Failure -> {
                    _userMessage.value = result.reason
                    AppContainer.audioManager.playError()
                    AppContainer.hapticManager.performInvalidMove()
                }
            }
        }
    }

    fun undo() {
        if (_isPaused.value || _isWin.value || _isLose.value) return
        viewModelScope.launch {
            val cmd = commandHistory.undo() ?: return@launch
            val gridBefore = AppContainer.gameEngine.getGrid()
            val gridUndone = cmd.undo(gridBefore)
            AppContainer.gameEngine.setGrid(gridUndone)
            AppContainer.audioManager.playButtonClick()
            simulate()
            updateUndoRedoStates()
            _userMessage.value = "Undo performed"
        }
    }

    fun redo() {
        if (_isPaused.value || _isWin.value || _isLose.value) return
        viewModelScope.launch {
            val cmd = commandHistory.redo() ?: return@launch
            val gridBefore = AppContainer.gameEngine.getGrid()
            val gridRedone = cmd.execute(gridBefore)
            AppContainer.gameEngine.setGrid(gridRedone)
            AppContainer.audioManager.playButtonClick()
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
            AppContainer.audioManager.playError()
            return
        }

        val hint = hintEngine.analyzeLevel(
            rows = state.level.rows,
            columns = state.level.columns,
            cells = state.cells
        )

        _remainingHints.value -= 1
        _activeHint.value = hint
        AppContainer.audioManager.playButtonClick()
    }

    fun dismissHint() {
        _activeHint.value = null
    }

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
            _movesCount.value = 0
            _timeSeconds.value = 0L
            _isPaused.value = false
            _isWin.value = false
            _isCelebrating.value = false
            _isLose.value = false
            _completionResult.value = null
            updateUndoRedoStates()
            resetLevelUseCase()
            AppContainer.audioManager.playButtonClick()
            simulate()
            startTimer()
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

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

