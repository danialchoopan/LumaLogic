package ir.danialchoopan.lumalogic.ui.screens.debug

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.danialchoopan.lumalogic.ui.components.GameCanvas
import ir.danialchoopan.lumalogic.ui.components.GlowingCard
import ir.danialchoopan.lumalogic.ui.components.LumaHeader
import ir.danialchoopan.lumalogic.ui.screens.game.GameUiState
import ir.danialchoopan.lumalogic.ui.screens.game.GameViewModel
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary

/**
 * DebugScreen providing developer step-by-step Light Trace Engine inspection and controls.
 */
@Composable
fun DebugScreen(
    viewModel: GameViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val debugState by viewModel.debugState.collectAsState()

    Scaffold(
        topBar = {
            LumaHeader(
                title = "Engine Debugger",
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.testTag("debug_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (val state = uiState) {
                is GameUiState.Success -> {
                    // Step Counter & Event Info Card
                    GlowingCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Step ${debugState.currentStep} / ${debugState.totalSteps}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = AmberPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = debugState.currentEvent?.action?.name ?: "INITIAL",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Cyan,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            debugState.currentEvent?.let { evt ->
                                Text(
                                    text = "Pos: (${evt.position.row + 1}, ${evt.position.column + 1}) | Dir: ${evt.direction} | Color: ${evt.color}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } ?: Text(
                                text = "Press Start or Step Forward to begin trace inspection.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Canvas Grid Preview
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        GameCanvas(
                            rows = state.level.rows,
                            columns = state.level.columns,
                            cells = state.cells,
                            beamPath = emptyList(),
                            beamSegments = emptyList(),
                            activatedTargets = emptySet(),
                            onCellClick = {},
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Step Controller Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.resetDebug() },
                            modifier = Modifier.testTag("debug_reset_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                tint = AmberPrimary
                            )
                        }

                        IconButton(
                            onClick = { viewModel.stepDebugBackward() },
                            enabled = debugState.currentStep > 1,
                            modifier = Modifier.testTag("debug_prev_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastRewind,
                                contentDescription = "Previous Step",
                                tint = if (debugState.currentStep > 1) AmberPrimary else Color.Gray
                            )
                        }

                        IconButton(
                            onClick = {
                                if (debugState.isRunning) viewModel.pauseDebug() else viewModel.resumeDebug()
                            },
                            modifier = Modifier.testTag("debug_play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (debugState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (debugState.isRunning) "Pause" else "Play",
                                tint = AmberPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.stepDebugForward() },
                            enabled = debugState.currentStep < debugState.totalSteps,
                            modifier = Modifier.testTag("debug_next_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastForward,
                                contentDescription = "Next Step",
                                tint = if (debugState.currentStep < debugState.totalSteps) AmberPrimary else Color.Gray
                            )
                        }
                    }
                }
                else -> {
                    Text("Loading simulation trace...", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}
