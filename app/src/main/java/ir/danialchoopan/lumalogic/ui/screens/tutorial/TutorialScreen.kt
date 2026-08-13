package ir.danialchoopan.lumalogic.ui.screens.tutorial

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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.danialchoopan.lumalogic.R
import ir.danialchoopan.lumalogic.di.AppContainer
import ir.danialchoopan.lumalogic.ui.components.LumaHeader
import ir.danialchoopan.lumalogic.ui.localization.currentLocalization
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary

data class TutorialStep(
    val titleRes: Int,
    val descRes: Int,
    val icon: ImageVector
)

@Composable
fun TutorialScreen(
    onFinishTutorial: () -> Unit
) {
    val loc = currentLocalization()

    val steps = remember {
        listOf(
            TutorialStep(R.string.tutorial_intro_title, R.string.tutorial_intro_desc, Icons.Default.Lightbulb),
            TutorialStep(R.string.tutorial_mirror_title, R.string.tutorial_mirror_desc, Icons.Default.Lightbulb),
            TutorialStep(R.string.tutorial_filter_title, R.string.tutorial_filter_desc, Icons.Default.FilterList),
            TutorialStep(R.string.tutorial_gate_title, R.string.tutorial_gate_desc, Icons.Default.AccountTree),
            TutorialStep(R.string.tutorial_energy_title, R.string.tutorial_energy_desc, Icons.Default.Bolt)
        )
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }
    val currentStep = steps[currentStepIndex]

    Scaffold(
        topBar = {
            LumaHeader(
                title = stringResource(R.string.title_tutorial),
                onBackClick = onFinishTutorial
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.testTag("tutorial_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .systemBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Step Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                steps.forEachIndexed { idx, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (idx == currentStepIndex) 14.dp else 10.dp)
                            .background(
                                color = if (idx == currentStepIndex) AmberPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Content Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(AmberPrimary.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = currentStep.icon,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(currentStep.titleRes),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        fontSize = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(currentStep.descRes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStepIndex > 0) {
                    OutlinedButton(
                        onClick = { currentStepIndex-- },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.padding(2.dp))
                        Text(if (loc.isPersian) "قبلی" else "Back")
                    }
                } else {
                    TextButton(
                        onClick = {
                            AppContainer.settingsRepository.setTutorialCompleted(true)
                            onFinishTutorial()
                        }
                    ) {
                        Text(if (loc.isPersian) "رد شدن" else "Skip", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Button(
                    onClick = {
                        if (currentStepIndex < steps.size - 1) {
                            currentStepIndex++
                        } else {
                            AppContainer.settingsRepository.setTutorialCompleted(true)
                            onFinishTutorial()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (currentStepIndex < steps.size - 1) {
                        Text(if (loc.isPersian) "بعدی" else "Next", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.padding(2.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.padding(2.dp))
                        Text(if (loc.isPersian) "فهمیدم" else "Got It!", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
