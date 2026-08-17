package ir.danialchoopan.lumalogic.ui.screens.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.danialchoopan.lumalogic.R
import ir.danialchoopan.lumalogic.data.model.CellType
import ir.danialchoopan.lumalogic.data.model.LightColor
import ir.danialchoopan.lumalogic.domain.level.LevelValidationResult
import ir.danialchoopan.lumalogic.ui.components.GameCanvas
import ir.danialchoopan.lumalogic.ui.components.GlowingCard
import ir.danialchoopan.lumalogic.ui.components.LumaHeader
import ir.danialchoopan.lumalogic.ui.localization.currentLocalization
import ir.danialchoopan.lumalogic.ui.localization.toPersianDigits
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary
import ir.danialchoopan.lumalogic.ui.theme.MirrorBlue
import ir.danialchoopan.lumalogic.ui.theme.SourceYellow
import ir.danialchoopan.lumalogic.ui.theme.TargetGreen

@Composable
fun LevelEditorScreen(
    levelId: String? = null,
    onBackClick: () -> Unit,
    onTestLevel: (String) -> Unit,
    viewModel: LevelEditorViewModel = viewModel()
) {
    val level by viewModel.level.collectAsState()
    val selectedTool by viewModel.selectedTool.collectAsState()
    val selectedCellPosition by viewModel.selectedCellPosition.collectAsState()
    val validationResult by viewModel.validationResult.collectAsState()
    val userMessage by viewModel.message.collectAsState()
    val canUndo by viewModel.canUndo.collectAsState()
    val canRedo by viewModel.canRedo.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showMetadataDialog by remember { mutableStateOf(false) }
    val loc = currentLocalization()

    LaunchedEffect(levelId) {
        if (!levelId.isNullOrBlank()) {
            viewModel.loadLevel(levelId)
        }
    }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            LumaHeader(
                title = "${stringResource(R.string.title_editor)}: ${loc.getLevelName(level)}",
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = { viewModel.undo() },
                        enabled = canUndo,
                        modifier = Modifier.testTag("editor_undo_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if (canUndo) AmberPrimary else Color.Gray.copy(alpha = 0.4f)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.redo() },
                        enabled = canRedo,
                        modifier = Modifier.testTag("editor_redo_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            tint = if (canRedo) AmberPrimary else Color.Gray.copy(alpha = 0.4f)
                        )
                    }

                    IconButton(
                        onClick = { showMetadataDialog = true },
                        modifier = Modifier.testTag("editor_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Level Metadata",
                            tint = AmberPrimary
                        )
                    }

                    IconButton(
                        onClick = {
                            val res = viewModel.validateCurrentLevel()
                            if (res.isValid) {
                                viewModel.saveLevel()
                                onTestLevel(level.levelId)
                            }
                        },
                        modifier = Modifier.testTag("editor_test_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Test Level",
                            tint = TargetGreen
                        )
                    }

                    IconButton(
                        onClick = { viewModel.saveLevel() },
                        modifier = Modifier.testTag("editor_save_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save Level",
                            tint = AmberPrimary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.testTag("level_editor_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Validation Results Banner
            validationResult?.let { res ->
                ValidationResultCard(
                    result = res,
                    onDismiss = { viewModel.clearValidationResult() }
                )
            }

            // Canvas Grid Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                GameCanvas(
                    rows = level.rows,
                    columns = level.columns,
                    cells = level.cells,
                    beamPath = emptyList(),
                    beamSegments = emptyList(),
                    activatedTargets = emptySet(),
                    selectedPosition = selectedCellPosition,
                    onCellClick = { pos -> viewModel.onCellClicked(pos) },
                    onMoveCell = { from, to -> viewModel.onCellClicked(to) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Editor Tool Palette
            EditorToolPalette(
                selectedTool = selectedTool,
                onToolSelect = { viewModel.selectTool(it) }
            )

            // Property Inspector for Selected Component
            selectedCellPosition?.let { pos ->
                val cell = level.cells.find { it.row == pos.row && it.column == pos.column }
                cell?.let {
                    PropertyInspectorCard(
                        cell = cell,
                        onRotate = { viewModel.rotateCellAt(pos) },
                        onSourceColorSelect = { viewModel.updateSelectedCellSourceColor(it) },
                        onTargetColorSelect = { viewModel.updateSelectedCellTargetColor(it) },
                        onOptionalTargetToggle = { viewModel.toggleSelectedCellOptionalTarget(it) },
                        onFilterColorSelect = { viewModel.updateSelectedCellFilterColor(it) }
                    )
                }
            }
        }
    }

    // Metadata & Settings Dialog
    if (showMetadataDialog) {
        LevelMetadataDialog(
            currentLevel = level,
            onDismiss = { showMetadataDialog = false },
            onConfirm = { name, author, difficulty, desc, rows, cols ->
                viewModel.updateMetadata(name, author, difficulty, desc)
                viewModel.updateGridDimensions(rows, cols)
                showMetadataDialog = false
            }
        )
    }
}

@Composable
private fun EditorToolPalette(
    selectedTool: EditorTool,
    onToolSelect: (EditorTool) -> Unit
) {
    val loc = currentLocalization()

    GlowingCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = AmberPrimary.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = if (loc.isPersian) "جعبه ابزار" else "TOOL PALETTE",
                style = MaterialTheme.typography.labelMedium,
                color = AmberPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PaletteToolButton(tool = EditorTool.SELECT, label = if (loc.isPersian) "انتخاب" else "Select", isSelected = selectedTool == EditorTool.SELECT, onToolSelect = onToolSelect)
                PaletteToolButton(tool = EditorTool.ERASE, label = if (loc.isPersian) "پاک‌کن" else "Erase", isSelected = selectedTool == EditorTool.ERASE, onToolSelect = onToolSelect)
                PaletteToolButton(tool = EditorTool.SOURCE, label = if (loc.isPersian) "منبع نور" else "Source", isSelected = selectedTool == EditorTool.SOURCE, onToolSelect = onToolSelect)
                PaletteToolButton(tool = EditorTool.TARGET, label = if (loc.isPersian) "هدف" else "Target", isSelected = selectedTool == EditorTool.TARGET, onToolSelect = onToolSelect)
                PaletteToolButton(tool = EditorTool.MIRROR, label = if (loc.isPersian) "آینه" else "Mirror", isSelected = selectedTool == EditorTool.MIRROR, onToolSelect = onToolSelect)
                PaletteToolButton(tool = EditorTool.SPLITTER, label = if (loc.isPersian) "شکافنده" else "Splitter", isSelected = selectedTool == EditorTool.SPLITTER, onToolSelect = onToolSelect)
                PaletteToolButton(tool = EditorTool.FILTER, label = if (loc.isPersian) "فیلتر" else "Filter", isSelected = selectedTool == EditorTool.FILTER, onToolSelect = onToolSelect)
                PaletteToolButton(tool = EditorTool.WIRE, label = if (loc.isPersian) "سیم" else "Wire", isSelected = selectedTool == EditorTool.WIRE, onToolSelect = onToolSelect)
                PaletteToolButton(tool = EditorTool.BLOCK, label = if (loc.isPersian) "مانع" else "Block", isSelected = selectedTool == EditorTool.BLOCK, onToolSelect = onToolSelect)
                PaletteToolButton(tool = EditorTool.GATE_AND, label = "AND", isSelected = selectedTool == EditorTool.GATE_AND, onToolSelect = onToolSelect)
                PaletteToolButton(tool = EditorTool.GATE_OR, label = "OR", isSelected = selectedTool == EditorTool.GATE_OR, onToolSelect = onToolSelect)
                PaletteToolButton(tool = EditorTool.GATE_NOT, label = "NOT", isSelected = selectedTool == EditorTool.GATE_NOT, onToolSelect = onToolSelect)
            }
        }
    }
}

@Composable
private fun PaletteToolButton(
    tool: EditorTool,
    label: String,
    isSelected: Boolean,
    onToolSelect: (EditorTool) -> Unit
) {
    Surface(
        onClick = { onToolSelect(tool) },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) AmberPrimary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.testTag("tool_button_${tool.name.lowercase()}")
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun PropertyInspectorCard(
    cell: ir.danialchoopan.lumalogic.data.model.Cell,
    onRotate: () -> Unit,
    onSourceColorSelect: (LightColor) -> Unit,
    onTargetColorSelect: (LightColor) -> Unit,
    onOptionalTargetToggle: (Boolean) -> Unit,
    onFilterColorSelect: (LightColor) -> Unit
) {
    val loc = currentLocalization()

    GlowingCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("property_inspector_card"),
        borderColor = AmberPrimary
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (loc.isPersian) "ویژگی قطعه" else "PROPERTIES"} (${loc.getCellTypeName(cell.type)} ${loc.formatNumber(cell.row)},${loc.formatNumber(cell.column)})",
                    style = MaterialTheme.typography.titleSmall,
                    color = AmberPrimary,
                    fontWeight = FontWeight.Bold
                )

                if (cell.type != CellType.EMPTY) {
                    Button(
                        onClick = onRotate,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.testTag("rotate_component_button")
                    ) {
                        Text("${if (loc.isPersian) "چرخش" else "Rotate"} (${loc.formatNumber(cell.rotation.degrees)}°)", fontSize = 11.sp, color = AmberPrimary)
                    }
                }
            }

            when (cell.type) {
                CellType.SOURCE -> {
                    Text(if (loc.isPersian) "رنگ نور منبع:" else "Source Light Color:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    ColorPickerRow(
                        selectedColor = cell.lightColor ?: LightColor.WHITE,
                        onColorSelect = onSourceColorSelect
                    )
                }

                CellType.TARGET -> {
                    Text(if (loc.isPersian) "رنگ مورد نیاز هدف:" else "Target Required Color:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    ColorPickerRow(
                        selectedColor = cell.requiredColor ?: LightColor.WHITE,
                        onColorSelect = onTargetColorSelect
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = cell.isOptionalTarget,
                            onCheckedChange = onOptionalTargetToggle,
                            colors = CheckboxDefaults.colors(checkedColor = AmberPrimary)
                        )
                        Text(if (loc.isPersian) "هدف اختیاری (امتیاز بونوس)" else "Optional Target (for bonus score)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                CellType.FILTER -> {
                    Text(if (loc.isPersian) "رنگ فیلتر عبوری:" else "Accepted Filter Color:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    ColorPickerRow(
                        selectedColor = cell.acceptedColor ?: LightColor.RED,
                        onColorSelect = onFilterColorSelect
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun ColorPickerRow(
    selectedColor: LightColor,
    onColorSelect: (LightColor) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LightColor.values().forEach { color ->
            val colorVal = when (color) {
                LightColor.WHITE -> Color.White
                LightColor.RED -> Color.Red
                LightColor.BLUE -> MirrorBlue
                LightColor.GREEN -> TargetGreen
                LightColor.YELLOW -> SourceYellow
            }
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(colorVal)
                    .border(
                        width = if (selectedColor == color) 3.dp else 1.dp,
                        color = if (selectedColor == color) AmberPrimary else Color.Gray,
                        shape = CircleShape
                    )
                    .clickable { onColorSelect(color) }
            )
        }
    }
}

@Composable
private fun ValidationResultCard(
    result: LevelValidationResult,
    onDismiss: () -> Unit
) {
    val loc = currentLocalization()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (result.isValid) TargetGreen.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (result.isValid) TargetGreen else Color.Red,
                RoundedCornerShape(12.dp)
            )
            .testTag("validation_result_card")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (result.isValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (result.isValid) TargetGreen else Color.Red
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (result.isValid) (if (loc.isPersian) "مرحله معتبر است" else "Level Valid") else (if (loc.isPersian) "خطاهای اعتبارسنجی" else "Level Validation Errors"),
                        fontWeight = FontWeight.Bold,
                        color = if (result.isValid) TargetGreen else Color.Red
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Clear, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
                }
            }

            result.errors.forEach { err ->
                Text("• ${loc.getValidationMessage(err)}", fontSize = 12.sp, color = Color.Red, fontWeight = FontWeight.SemiBold)
            }
            result.warnings.forEach { warn ->
                Text("• ${loc.getValidationMessage(warn)}", fontSize = 12.sp, color = AmberPrimary)
            }
        }
    }
}

@Composable
private fun LevelMetadataDialog(
    currentLevel: ir.danialchoopan.lumalogic.data.model.Level,
    onDismiss: () -> Unit,
    onConfirm: (name: String, author: String, difficulty: String, desc: String, rows: Int, cols: Int) -> Unit
) {
    var name by remember { mutableStateOf(currentLevel.name) }
    var author by remember { mutableStateOf(currentLevel.author) }
    var difficulty by remember { mutableStateOf(currentLevel.difficulty) }
    var desc by remember { mutableStateOf(currentLevel.description) }
    var rowsStr by remember { mutableStateOf(currentLevel.rows.toString()) }
    var colsStr by remember { mutableStateOf(currentLevel.columns.toString()) }
    val loc = currentLocalization()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (loc.isPersian) "تنظیمات مرحله" else "Level Settings", fontWeight = FontWeight.Bold, color = AmberPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (loc.isPersian) "نام مرحله" else "Level Name") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AmberPrimary)
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text(if (loc.isPersian) "سازنده" else "Author") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AmberPrimary)
                )
                OutlinedTextField(
                    value = difficulty,
                    onValueChange = { difficulty = it },
                    label = { Text(if (loc.isPersian) "درجه سختی" else "Difficulty (Easy/Medium/Hard)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AmberPrimary)
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text(if (loc.isPersian) "توضیحات" else "Description") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AmberPrimary)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = rowsStr,
                        onValueChange = { rowsStr = it },
                        label = { Text(if (loc.isPersian) "سطرها" else "Rows") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AmberPrimary)
                    )
                    OutlinedTextField(
                        value = colsStr,
                        onValueChange = { colsStr = it },
                        label = { Text(if (loc.isPersian) "ستون‌ها" else "Columns") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AmberPrimary)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val r = rowsStr.toIntOrNull() ?: currentLevel.rows
                    val c = colsStr.toIntOrNull() ?: currentLevel.columns
                    onConfirm(name, author, difficulty, desc, r, c)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary)
            ) {
                Text(if (loc.isPersian) "اعمال" else "Apply", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
}
