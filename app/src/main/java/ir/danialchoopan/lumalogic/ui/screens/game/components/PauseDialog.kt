package ir.danialchoopan.lumalogic.ui.screens.game.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ir.danialchoopan.lumalogic.R
import ir.danialchoopan.lumalogic.ui.localization.currentLocalization
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary

@Composable
fun PauseDialog(
    levelName: String,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onSettings: () -> Unit,
    onExitLevel: () -> Unit
) {
    val loc = currentLocalization()

    Dialog(
        onDismissRequest = onResume
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, AmberPrimary, RoundedCornerShape(28.dp))
                .padding(4.dp)
                .testTag("pause_menu")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.pause_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = AmberPrimary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )

                Text(
                    text = levelName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onResume,
                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pause_resume_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(stringResource(R.string.pause_resume), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.surface)
                }

                OutlinedButton(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pause_restart_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(stringResource(R.string.pause_restart))
                }

                OutlinedButton(
                    onClick = onSettings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pause_settings_button")
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(stringResource(R.string.title_settings))
                }

                OutlinedButton(
                    onClick = onExitLevel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pause_exit_button")
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(stringResource(R.string.pause_exit))
                }
            }
        }
    }
}
