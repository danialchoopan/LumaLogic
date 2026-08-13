package ir.danialchoopan.lumalogic.ui.screens.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.danialchoopan.lumalogic.R
import ir.danialchoopan.lumalogic.domain.hint.Hint
import ir.danialchoopan.lumalogic.ui.components.GameCanvas
import ir.danialchoopan.lumalogic.ui.components.GlowingCard
import ir.danialchoopan.lumalogic.ui.components.LumaHeader
import ir.danialchoopan.lumalogic.ui.localization.currentLocalization
import ir.danialchoopan.lumalogic.ui.localization.toPersianDigits
import ir.danialchoopan.lumalogic.ui.screens.game.components.EnergyBar
import ir.danialchoopan.lumalogic.ui.screens.game.components.LoseDialog
import ir.danialchoopan.lumalogic.ui.screens.game.components.PauseDialog
import ir.danialchoopan.lumalogic.ui.screens.game.components.WinDialog
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary
import ir.danialchoopan.lumalogic.ui.theme.MirrorBlue
import ir.danialchoopan.lumalogic.ui.theme.SourceYellow
import ir.danialchoopan.lumalogic.ui.theme.TargetGreen
import ir.danialchoopan.lumalogic.ui.theme.WireGray

@Composable
fun GameScreen(
    onBackClick: () -> Unit,
    onNextLevelClick: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onDebugClick: () -> Unit = {},
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val gameStatus by viewModel.gameStatus.collectAsState()
    val beamPath by viewModel.beamPath.collectAsState()
    val beamSegments by viewModel.beamSegments.collectAsState()
    val selectedCell by viewModel.selectedCell.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val canUndo by viewModel.canUndo.collectAsState()
    val canRedo by viewModel.canRedo.collectAsState()
    val remainingHints by viewModel.remainingHints.collectAsState()
    val activeHint by viewModel.activeHint.collectAsState()

    val energyState by viewModel.energyState.collectAsState()
    val movesCount by viewModel.movesCount.collectAsState()
    val timeSeconds by viewModel.timeSeconds.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val isWin by viewModel.isWin.collectAsState()
    val isLose by viewModel.isLose.collectAsState()
    val completionResult by viewModel.completionResult.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val loc = currentLocalization()

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        topBar = {
            LumaHeader(
                title = when (val state = uiState) {
                    is GameUiState.Success -> loc.getLevelName(state.level)
                    else -> if (loc.isPersian) "صفحه بازی" else "Game Grid"
                },
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = { viewModel.togglePause() },
                        modifier = Modifier.testTag("pause_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = AmberPrimary
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.toggleDebugMode(true)
                            onDebugClick()
                        },
                        modifier = Modifier.testTag("debug_mode_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = "Debug Simulation",
                            tint = AmberPrimary
                        )
                    }

                    IconButton(
                        onClick = { viewModel.resetGame() },
                        modifier = Modifier.testTag("reset_game_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Level",
                            tint = AmberPrimary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState is GameUiState.Success) {
                SleekBottomControlShell(
                    canUndo = canUndo,
                    canRedo = canRedo,
                    remainingHints = remainingHints,
                    onUndoClick = { viewModel.undo() },
                    onRedoClick = { viewModel.redo() },
                    onHintClick = { viewModel.requestHint() },
                    onResetClick = { viewModel.resetGame() }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.testTag("game_screen")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is GameUiState.Loading -> {
                    CircularProgressIndicator(
                        color = AmberPrimary,
                        modifier = Modifier.testTag("game_loading")
                    )
                }

                is GameUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        IconButton(onClick = { viewModel.reloadGame() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = AmberPrimary)
                        }
                    }
                }

                is GameUiState.Success -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Level Info Banner
                        GlowingCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = if (gameStatus?.success == true) TargetGreen else AmberPrimary.copy(alpha = 0.3f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = loc.getLevelName(state.level),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (gameStatus?.success == true) (if (loc.isPersian) "مرحله تکمیل شد!" else "LEVEL COMPLETE!") else (gameStatus?.stoppedReason?.name ?: loc.getDifficultyLabel(state.level.difficulty).uppercase()),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (gameStatus?.success == true) TargetGreen else AmberPrimary,
                                        letterSpacing = 1.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = if (loc.isPersian) "لمس: چرخش | کشیدن: جابجایی" else "TAP ROTATE | DRAG MOVE",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Energy Bar
                        EnergyBar(energyState = energyState)

                        // Active Hint Overlay Card
                        activeHint?.let { hint ->
                            HintOverlayCard(
                                hint = hint,
                                onDismiss = { viewModel.dismissHint() }
                            )
                        }

                        // Centered Grid Container with Canvas Renderer
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(500)) + scaleIn(initialScale = 0.9f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .border(
                                        width = 1.dp,
                                        color = Color.White.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                    .background(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                GameCanvas(
                                    rows = state.level.rows,
                                    columns = state.level.columns,
                                    cells = state.cells,
                                    beamPath = beamPath,
                                    beamSegments = beamSegments,
                                    activatedTargets = gameStatus?.activatedTargets ?: emptySet(),
                                    selectedPosition = selectedCell,
                                    onCellClick = { pos ->
                                        viewModel.selectCell(pos)
                                    },
                                    onMoveCell = { from, to ->
                                        viewModel.moveCell(from, to)
                                    },
                                    onInvalidMoveAttempt = { reason ->
                                        viewModel.setUserMessage(reason)
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        // Sleek HUD Stats
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HudStatItem(label = if (loc.isPersian) "حرکات" else "MOVES", value = loc.formatNumber(movesCount))
                            HudStatItem(label = if (loc.isPersian) "زمان" else "TIME", value = "${String.format("%02d", timeSeconds / 60).toPersianDigits(loc.isPersian)}:${String.format("%02d", timeSeconds % 60).toPersianDigits(loc.isPersian)}")
                            HudStatItem(label = if (loc.isPersian) "شبکه" else "GRID", value = "${loc.formatNumber(state.level.rows)}x${loc.formatNumber(state.level.columns)}")
                        }

                        // Dialog Overlays
                        if (isPaused) {
                            PauseDialog(
                                levelName = loc.getLevelName(state.level),
                                onResume = { viewModel.resumeGame() },
                                onRestart = { viewModel.resetGame() },
                                onSettings = onSettingsClick,
                                onExitLevel = onBackClick
                            )
                        }

                        if (isWin && completionResult != null) {
                            WinDialog(
                                result = completionResult!!,
                                onRetry = { viewModel.resetGame() },
                                onNextLevel = {
                                    val nextId = "level_${state.level.levelId.removePrefix("level_").toIntOrNull()?.plus(1) ?: 1}"
                                    onNextLevelClick(nextId)
                                },
                                onLevelSelect = onBackClick,
                                onHome = onBackClick
                            )
                        }

                        if (isLose) {
                            LoseDialog(
                                levelName = loc.getLevelName(state.level),
                                energyUsed = energyState.used,
                                movesCount = movesCount,
                                onRetry = { viewModel.resetGame() },
                                onLevelSelect = onBackClick,
                                onHome = onBackClick
                            )
                        }

                        // Legend Indicator
                        GlowingCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            borderColor = Color.White.copy(alpha = 0.05f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LegendItem(color = SourceYellow, label = if (loc.isPersian) "منبع" else "Source")
                                LegendItem(color = TargetGreen, label = if (loc.isPersian) "هدف" else "Target")
                                LegendItem(color = WireGray, label = if (loc.isPersian) "سیم" else "Wire")
                                LegendItem(color = MirrorBlue, label = if (loc.isPersian) "آینه" else "Mirror")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HintOverlayCard(
    hint: Hint,
    onDismiss: () -> Unit
) {
    val loc = currentLocalization()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, AmberPrimary, RoundedCornerShape(16.dp))
            .testTag("hint_overlay")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Hint",
                        tint = AmberPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "${if (loc.isPersian) "راهنمایی" else "HINT"} (${hint.type.name})",
                        style = MaterialTheme.typography.titleSmall,
                        color = AmberPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("dismiss_hint_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Hint",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = hint.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            hint.suggestedAction?.let { action ->
                Text(
                    text = "${if (loc.isPersian) "عمل پیشنهادی" else "Action"}: $action",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Cyan,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun HudStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = AmberPrimary
        )
    }
}

@Composable
private fun SleekBottomControlShell(
    canUndo: Boolean,
    canRedo: Boolean,
    remainingHints: Int,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onHintClick: () -> Unit,
    onResetClick: () -> Unit
) {
    val loc = currentLocalization()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            )
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Undo Button
            SleekCircularButton(
                icon = Icons.AutoMirrored.Filled.Undo,
                label = if (loc.isPersian) "بازگشت" else "UNDO",
                onClick = onUndoClick,
                enabled = canUndo,
                testTag = "undo_button"
            )

            // Redo Button
            SleekCircularButton(
                icon = Icons.AutoMirrored.Filled.Redo,
                label = if (loc.isPersian) "انجام مجدد" else "REDO",
                onClick = onRedoClick,
                enabled = canRedo,
                testTag = "redo_button"
            )

            // Reset Button
            SleekCircularButton(
                icon = Icons.Default.Refresh,
                label = if (loc.isPersian) "بازنشانی" else "RESET",
                onClick = onResetClick,
                enabled = true,
                testTag = "reset_button"
            )

            // Hint Button
            BadgedBox(
                badge = {
                    if (remainingHints > 0) {
                        Badge(containerColor = AmberPrimary, contentColor = Color.Black) {
                            Text(loc.formatNumber(remainingHints), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            ) {
                SleekCircularButton(
                    icon = Icons.Default.Lightbulb,
                    label = if (loc.isPersian) "راهنمایی" else "HINT",
                    onClick = onHintClick,
                    enabled = remainingHints > 0,
                    testTag = "hint_button"
                )
            }
        }
    }
}

@Composable
private fun SleekCircularButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    testTag: String = ""
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .testTag(testTag)
            .clickable(enabled = enabled) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    if (enabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
                .border(
                    width = 1.dp,
                    color = if (enabled) AmberPrimary.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (enabled) AmberPrimary else Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) AmberPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .padding(end = 6.dp)
                .background(color = color, shape = MaterialTheme.shapes.small)
                .padding(6.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
