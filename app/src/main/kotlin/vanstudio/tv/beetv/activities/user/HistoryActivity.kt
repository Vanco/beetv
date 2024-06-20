package vanstudio.tv.beetv.activities.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import vanstudio.tv.beetv.screen.user.HistoryScreen
import vanstudio.tv.beetv.ui.theme.BVTheme

class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                HistoryScreen()
            }
        }
    }
}
