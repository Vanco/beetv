package vanstudio.tv.beetv.activities.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import vanstudio.tv.beetv.screen.settings.LogsScreen
import vanstudio.tv.beetv.ui.theme.BVTheme

class LogsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                LogsScreen()
            }
        }
    }
}