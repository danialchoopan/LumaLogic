package ir.danialchoopan.lumalogic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ir.danialchoopan.lumalogic.di.AppContainer
import ir.danialchoopan.lumalogic.ui.navigation.LumaNavGraph
import ir.danialchoopan.lumalogic.ui.theme.LumaLogicTheme

/**
 * Main Activity for LumaLogic application.
 *
 * Package Name: ir.danialchoopan.lumalogic
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppContainer.initialize(this)
        enableEdgeToEdge()
        setContent {
            val settingsState by AppContainer.settingsRepository.settings.collectAsState()
            LumaLogicTheme(settingsData = settingsState) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LumaNavGraph()
                }
            }
        }
    }
}
