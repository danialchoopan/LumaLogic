package ir.danialchoopan.lumalogic.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.danialchoopan.lumalogic.di.AppContainer
import ir.danialchoopan.lumalogic.data.model.Cell
import ir.danialchoopan.lumalogic.data.model.Level
import ir.danialchoopan.lumalogic.data.model.Position
import ir.danialchoopan.lumalogic.domain.engine.LightTraceResult
import ir.danialchoopan.lumalogic.domain.usecase.GetLevelUseCase
import ir.danialchoopan.lumalogic.domain.usecase.ResetLevelUseCase
import ir.danialchoopan.lumalogic.domain.usecase.RotateCellUseCase
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
    private val resetLevelUseCase: ResetLevelUseCase = AppContainer.resetLevelUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _beamPath = MutableStateFlow<List<Position>>(emptyList())
    val beamPath: StateFlow<List<Position>> = _beamPath.asStateFlow()

    private val _litCells = MutableStateFlow<Set<Position>>(emptySet())
    val litCells: StateFlow<Set<Position>> = _litCells.asStateFlow()

    private val _gameStatus = MutableStateFlow<LightTraceResult?>(null)
    val gameStatus: StateFlow<LightTraceResult?> = _gameStatus.asStateFlow()

    init {
        loadLevel()
    }

    fun loadLevel(levelId: String? = null) {
        viewModelScope.launch {
            _uiState.value = GameUiState.Loading
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

    fun simulate() {
        val traceResult = AppContainer.gameEngine.simulate()
        val grid = AppContainer.gameEngine.getGrid()

        _beamPath.value = traceResult.path
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

    fun resetSimulation() {
        resetGame()
    }

    fun onCellClicked(cellId: String) {
        val currentState = _uiState.value
        if (currentState is GameUiState.Success) {
            viewModelScope.launch {
                val updatedGrid = rotateCellUseCase(cellId)
                simulate()
            }
        }
    }

    fun resetGame() {
        viewModelScope.launch {
            val resetGrid = resetLevelUseCase()
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

