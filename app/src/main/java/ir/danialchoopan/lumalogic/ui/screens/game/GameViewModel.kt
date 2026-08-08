package ir.danialchoopan.lumalogic.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.danialchoopan.lumalogic.di.AppContainer
import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.Level
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.domain.engine.LightTraceResult
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
    private val resetLevelUseCase: ResetLevelUseCase = AppContainer.resetLevelUseCase
) : ViewModel() {

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

    init {
        loadLevel()
    }

    fun loadLevel(levelId: String? = null) {
        viewModelScope.launch {
            _uiState.value = GameUiState.Loading
            _selectedCell.value = null
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

    fun refreshSimulation() {
        simulate()
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
        } else if (AppContainer.gameEngine.getGrid().isNotEmpty()) {
            val level = AppContainer.levelRepository.getDemoLevel()
            _uiState.value = GameUiState.Success(level = level, cells = grid)
        }
    }

    fun selectCell(position: Position) {
        val currentSelected = _selectedCell.value
        if (currentSelected == position) {
            // Tapping selected cell again rotates it
            rotateSelectedCell()
        } else if (currentSelected != null) {
            // Attempt moving from previously selected cell to new position
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
            rotateCellUseCase(position)
            simulate()
            if (_gameStatus.value?.success == true) {
                _userMessage.value = "Target activated! Level Complete!"
            }
        }
    }

    fun moveCell(from: Position, to: Position) {
        viewModelScope.launch {
            val result = moveCellUseCase(from, to)
            when (result) {
                is MoveResult.Success -> {
                    _selectedCell.value = null
                    simulate()
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

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun setUserMessage(message: String) {
        _userMessage.value = message
    }

    fun resetSimulation() {
        resetGame()
    }

    fun onCellClicked(cellId: String) {
        val grid = AppContainer.gameEngine.getGrid()
        val cell = grid.find { it.id == cellId }
        if (cell != null) {
            selectCell(Position(cell.row, cell.column))
        }
    }

    fun resetGame() {
        viewModelScope.launch {
            _selectedCell.value = null
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

