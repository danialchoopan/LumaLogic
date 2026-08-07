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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.danialchoopan.lumalogic.ui.components.CellComposable
import ir.danialchoopan.lumalogic.ui.components.GlowingCard
import ir.danialchoopan.lumalogic.ui.components.LumaHeader
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary
import ir.danialchoopan.lumalogic.ui.theme.MirrorBlue
import ir.danialchoopan.lumalogic.ui.theme.SourceYellow
import ir.danialchoopan.lumalogic.ui.theme.TargetGreen
import ir.danialchoopan.lumalogic.ui.theme.WireGray

@Composable
fun GameScreen(
    onBackClick: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val gameStatus by viewModel.gameStatus.collectAsState()
    val beamPath by viewModel.beamPath.collectAsState()

    Scaffold(
        topBar = {
            LumaHeader(
                title = when (val state = uiState) {
                    is GameUiState.Success -> state.level.name
                    else -> "Game Grid"
                },
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = { viewModel.resetSimulation() },
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
        bottomBar = {
            if (uiState is GameUiState.Success) {
                SleekBottomControlShell(
                    onResetClick = { viewModel.resetSimulation() },
                    onPlayClick = { viewModel.startSimulation() },
                    onHintClick = { /* Hint placeholder */ }
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
                                        text = state.level.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (gameStatus?.success == true) "LEVEL COMPLETE!" else (gameStatus?.stoppedReason?.name ?: state.level.difficulty.uppercase()),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (gameStatus?.success == true) TargetGreen else AmberPrimary,
                                        letterSpacing = 1.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "TAP CELL TO ROTATE",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
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
                                ir.danialchoopan.lumalogic.ui.components.GameCanvas(
                                    rows = state.level.rows,
                                    columns = state.level.columns,
                                    cells = state.cells,
                                    beamPath = beamPath,
                                    activatedTargets = gameStatus?.activatedTargets ?: emptySet(),
                                    onCellClick = { pos ->
                                        val cell = state.cells.find { it.row == pos.row && it.column == pos.column }
                                        cell?.let { viewModel.onCellClicked(it.id) }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        // Sleek HUD Stats (Energy & Path length counters)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HudStatItem(label = "ENERGY", value = "${gameStatus?.energyUsed ?: 0}")
                            HudStatItem(label = "BEAM PATH", value = "${beamPath.size}")
                            HudStatItem(label = "GRID", value = "${state.level.rows}x${state.level.columns}")
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
                                LegendItem(color = SourceYellow, label = "Source")
                                LegendItem(color = TargetGreen, label = "Target")
                                LegendItem(color = WireGray, label = "Wire")
                                LegendItem(color = MirrorBlue, label = "Mirror")
                            }
                        }
                    }
                }
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
    onResetClick: () -> Unit,
    onPlayClick: () -> Unit,
    onHintClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            )
            .padding(vertical = 16.dp, horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SleekCircularButton(
                icon = Icons.Default.Refresh,
                label = "RESET",
                onClick = onResetClick,
                isPrimary = false
            )

            SleekCircularButton(
                icon = Icons.Default.PlayArrow,
                label = "RELOAD",
                onClick = onPlayClick,
                isPrimary = true
            )

            SleekCircularButton(
                icon = Icons.Default.Lightbulb,
                label = "HINT",
                onClick = onHintClick,
                isPrimary = false
            )
        }
    }
}

@Composable
private fun SleekCircularButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isPrimary: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(if (isPrimary) 60.dp else 50.dp)
                .clip(CircleShape)
                .background(
                    if (isPrimary) AmberPrimary else MaterialTheme.colorScheme.background
                )
                .border(
                    width = 1.dp,
                    color = if (isPrimary) AmberPrimary else Color.White.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(if (isPrimary) 30.dp else 22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPrimary) AmberPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun LegendItem(
    color: androidx.compose.ui.graphics.Color,
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

