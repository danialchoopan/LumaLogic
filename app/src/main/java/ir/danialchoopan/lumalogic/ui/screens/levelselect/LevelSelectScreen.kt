package ir.danialchoopan.lumalogic.ui.screens.levelselect

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.danialchoopan.lumalogic.data.model.Level
import ir.danialchoopan.lumalogic.data.model.LevelProgress
import ir.danialchoopan.lumalogic.di.AppContainer
import ir.danialchoopan.lumalogic.ui.components.GlowingCard
import ir.danialchoopan.lumalogic.ui.components.LumaButton
import ir.danialchoopan.lumalogic.ui.components.LumaHeader
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary
import ir.danialchoopan.lumalogic.ui.theme.TargetGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectScreen(
    chapterId: String? = null,
    onBackClick: () -> Unit,
    onLevelSelected: (String) -> Unit,
    onCreateNewLevel: () -> Unit,
    onEditLevel: (String) -> Unit,
    onImportLevel: () -> Unit,
    onExportLevel: (String) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDifficultyFilter by remember { mutableStateOf("ALL") }

    var levels by remember { mutableStateOf<List<Level>>(emptyList()) }
    var progressMap by remember { mutableStateOf<Map<String, LevelProgress>>(emptyMap()) }
    var favoritesSet by remember { mutableStateOf<Set<String>>(emptySet()) }
    var levelToDelete by remember { mutableStateOf<Level?>(null) }
    var previewLevel by remember { mutableStateOf<Level?>(null) }

    val favRepo = remember { AppContainer.favoriteLevelRepository }
    val chapterRepo = remember { AppContainer.chapterRepository }

    val chapter = remember(chapterId) {
        if (chapterId != null) chapterRepo.getChapter(chapterId) else null
    }

    fun refreshLevels() {
        levels = if (chapterId != null) {
            chapterRepo.getLevelsForChapter(chapterId)
        } else {
            AppContainer.levelManager.getAllLevels()
        }
        progressMap = AppContainer.levelProgressManager.getAllProgress().associateBy { it.levelId }
        favoritesSet = favRepo.getFavoriteLevelIds()
    }

    LaunchedEffect(chapterId) {
        refreshLevels()
    }

    val builtInLevels = levels.filter { !it.isUserCreated }
    val userLevels = levels.filter { it.isUserCreated }
    val completedLevels = levels.filter { progressMap[it.levelId]?.completed == true }

    Scaffold(
        topBar = {
            LumaHeader(
                title = chapter?.let { "Chapter ${it.number}: ${it.name}" } ?: "Select Puzzle Level",
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = onImportLevel,
                        modifier = Modifier.testTag("import_level_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Import Level",
                            tint = AmberPrimary
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.testTag("level_select_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search levels...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AmberPrimary) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("level_search_input")
            )

            // Difficulty Filter Bar
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val filters = listOf("ALL", "FAVORITES", "BEGINNER", "EASY", "NORMAL", "HARD", "EXPERT", "MASTER")
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedDifficultyFilter == filter,
                        onClick = { selectedDifficultyFilter = filter },
                        label = { Text(filter, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("filter_chip_$filter")
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Section Tabs
            if (chapterId == null) {
                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = AmberPrimary
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Built-in (${builtInLevels.size})", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_builtin_levels")
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("My Levels (${userLevels.size})", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_user_levels")
                    )
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        text = { Text("Completed (${completedLevels.size})", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_completed_levels")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Toolbar for Custom Levels
            if (selectedTabIndex == 1 && chapterId == null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LumaButton(
                        text = "New Level",
                        onClick = onCreateNewLevel,
                        icon = Icons.Default.Add,
                        modifier = Modifier.weight(1f),
                        testTag = "create_level_button"
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    LumaButton(
                        text = "Import",
                        onClick = onImportLevel,
                        icon = Icons.Default.FileDownload,
                        modifier = Modifier.weight(1f),
                        isPrimary = false,
                        testTag = "import_action_button"
                    )
                }
            }

            // Level List Filter
            val rawList = when {
                chapterId != null -> levels
                selectedTabIndex == 1 -> userLevels
                selectedTabIndex == 2 -> completedLevels
                else -> builtInLevels
            }

            val filteredList = rawList.filter { lvl ->
                val matchesSearch = searchQuery.isBlank() || lvl.name.contains(searchQuery, ignoreCase = true)
                val matchesDifficulty = when (selectedDifficultyFilter) {
                    "ALL" -> true
                    "FAVORITES" -> favoritesSet.contains(lvl.levelId)
                    else -> lvl.difficulty.equals(selectedDifficultyFilter, ignoreCase = true)
                }
                matchesSearch && matchesDifficulty
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No levels found matching criteria.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList, key = { it.levelId }) { level ->
                        val progress = progressMap[level.levelId]
                        val isFav = favoritesSet.contains(level.levelId)
                        LevelCard(
                            level = level,
                            progress = progress,
                            isFavorite = isFav,
                            onFavoriteToggle = {
                                favRepo.toggleFavorite(level.levelId)
                                refreshLevels()
                            },
                            onPlayClick = { previewLevel = level },
                            onEditClick = { onEditLevel(level.levelId) },
                            onExportClick = { onExportLevel(level.levelId) },
                            onDeleteClick = { levelToDelete = level }
                        )
                    }
                }
            }
        }
    }

    // Level Preview Dialog
    previewLevel?.let { level ->
        val progress = progressMap[level.levelId]
        AlertDialog(
            onDismissRequest = { previewLevel = null },
            title = {
                Text(
                    text = level.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = AmberPrimary
                )
            },
            text = {
                Column {
                    Text(text = level.description.ifBlank { "Optical puzzle challenge." }, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Difficulty: ${level.difficulty}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(text = "Grid Size: ${level.rows}x${level.columns}", fontSize = 13.sp)
                    Text(text = "Max Energy: ${level.maximumEnergy}", fontSize = 13.sp)
                    Text(text = "Expected Moves: ${level.expectedMoves}", fontSize = 13.sp)
                    if (progress?.completed == true) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Best Score: ${progress.bestScore}", fontWeight = FontWeight.Bold, color = TargetGreen, fontSize = 13.sp)
                        Text(text = "Stars Earned: ${progress.stars} / 3 ⭐", fontWeight = FontWeight.Bold, color = Color(0xFFFFC107), fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val selectedId = level.levelId
                        previewLevel = null
                        onLevelSelected(selectedId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary)
                ) {
                    Text("START PUZZLE", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { previewLevel = null }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.testTag("preview_level_dialog")
        )
    }

    // Delete Level Confirmation Dialog
    levelToDelete?.let { level ->
        AlertDialog(
            onDismissRequest = { levelToDelete = null },
            title = { Text("Delete Custom Level?", fontWeight = FontWeight.Bold, color = AmberPrimary) },
            text = {
                Text("Are you sure you want to delete '${level.name}'? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        AppContainer.levelManager.deleteUserLevel(level.levelId)
                        levelToDelete = null
                        refreshLevels()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { levelToDelete = null }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.testTag("delete_level_dialog")
        )
    }
}

@Composable
private fun LevelCard(
    level: Level,
    progress: LevelProgress?,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onPlayClick: () -> Unit,
    onEditClick: () -> Unit,
    onExportClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isCompleted = progress?.completed == true

    GlowingCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("level_card_${level.levelId}"),
        borderColor = if (isCompleted) TargetGreen.copy(alpha = 0.6f) else AmberPrimary.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier.size(28.dp).testTag("fav_button_${level.levelId}")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = level.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isCompleted) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = TargetGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(
                            color = when (level.difficulty.lowercase()) {
                                "easy", "beginner" -> TargetGreen.copy(alpha = 0.2f)
                                "hard", "master", "expert" -> Color.Red.copy(alpha = 0.2f)
                                else -> AmberPrimary.copy(alpha = 0.2f)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = level.difficulty.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (level.difficulty.lowercase()) {
                            "easy", "beginner" -> TargetGreen
                            "hard", "master", "expert" -> Color.Red
                            else -> AmberPrimary
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = level.description.ifBlank { "Grid size: ${level.rows}x${level.columns} • Author: ${level.author}" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            if (isCompleted && progress != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        for (i in 1..3) {
                            Icon(
                                imageVector = if (i <= progress.stars) Icons.Default.Star else Icons.Outlined.Star,
                                contentDescription = null,
                                tint = if (i <= progress.stars) AmberPrimary else Color.Gray.copy(alpha = 0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Score: ${progress.bestScore}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Best Time: ${progress.bestTimeSeconds}s",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    if (level.isUserCreated) {
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("edit_level_button_${level.levelId}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Level",
                                tint = AmberPrimary
                            )
                        }

                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("delete_level_button_${level.levelId}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Level",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    IconButton(
                        onClick = onExportClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("export_level_button_${level.levelId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = "Export JSON",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = onPlayClick,
                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("play_level_button_${level.levelId}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PLAY", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
