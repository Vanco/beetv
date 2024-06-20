package vanstudio.tv.beetv.activities.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import vanstudio.tv.beetv.screen.settings.MediaCodecScreen
import vanstudio.tv.beetv.ui.theme.BVTheme

class MediaCodecActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                MediaCodecScreen()
            }
        }
    }
}
