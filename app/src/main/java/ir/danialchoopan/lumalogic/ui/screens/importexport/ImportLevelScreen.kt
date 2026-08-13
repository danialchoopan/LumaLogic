package ir.danialchoopan.lumalogic.ui.screens.importexport

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import ir.danialchoopan.lumalogic.R
import ir.danialchoopan.lumalogic.data.model.Level
import ir.danialchoopan.lumalogic.di.AppContainer
import ir.danialchoopan.lumalogic.domain.level.LevelValidationResult
import ir.danialchoopan.lumalogic.ui.components.GlowingCard
import ir.danialchoopan.lumalogic.ui.components.LumaButton
import ir.danialchoopan.lumalogic.ui.components.LumaHeader
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary
import ir.danialchoopan.lumalogic.ui.theme.TargetGreen

@Composable
fun ImportLevelScreen(
    onBackClick: () -> Unit,
    onImportSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    var jsonText by remember { mutableStateOf("") }
    var validationResult by remember { mutableStateOf<LevelValidationResult?>(null) }
    var importedLevel by remember { mutableStateOf<Level?>(null) }

    fun pasteFromClipboard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        if (clip != null && clip.itemCount > 0) {
            jsonText = clip.getItemAt(0).text.toString()
        }
    }

    fun processImport() {
        if (jsonText.isBlank()) return
        val (level, result) = AppContainer.levelManager.importLevel(jsonText)
        validationResult = result
        importedLevel = level

        if (result.isValid && level != null) {
            Toast.makeText(context, "Level '${level.name}' imported successfully!", Toast.LENGTH_SHORT).show()
            onImportSuccess(level.levelId)
        }
    }

    Scaffold(
        topBar = {
            LumaHeader(
                title = stringResource(R.string.title_import_level),
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.testTag("import_level_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.import_prompt),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                LumaButton(
                    text = stringResource(R.string.paste_clipboard),
                    onClick = { pasteFromClipboard() },
                    isPrimary = false,
                    testTag = "paste_clipboard_button"
                )
            }

            OutlinedTextField(
                value = jsonText,
                onValueChange = {
                    jsonText = it
                    validationResult = null
                    importedLevel = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .testTag("import_json_input_field"),
                placeholder = { Text("{\n  \"name\": \"My Level\",\n  \"rows\": 7,\n  ...\n}") },
                textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            LumaButton(
                text = stringResource(R.string.validate_and_import),
                onClick = { processImport() },
                icon = Icons.Default.FileDownload,
                modifier = Modifier.fillMaxWidth(),
                testTag = "submit_import_button"
            )

            validationResult?.let { res ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (res.isValid) TargetGreen.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (res.isValid) TargetGreen else Color.Red,
                            RoundedCornerShape(12.dp)
                        )
                        .testTag("import_validation_result_card")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (res.isValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (res.isValid) TargetGreen else Color.Red
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (res.isValid) stringResource(R.string.import_success) else stringResource(R.string.import_failed),
                                fontWeight = FontWeight.Bold,
                                color = if (res.isValid) TargetGreen else Color.Red
                            )
                        }

                        res.errors.forEach { err ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• $err", fontSize = 12.sp, color = Color.Red)
                        }
                        res.warnings.forEach { warn ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• $warn", fontSize = 12.sp, color = AmberPrimary)
                        }
                    }
                }
            }
        }
    }
}
