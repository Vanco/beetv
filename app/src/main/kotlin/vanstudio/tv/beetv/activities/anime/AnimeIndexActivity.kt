package vanstudio.tv.beetv.activities.anime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import vanstudio.tv.beetv.screen.home.anime.AnimeIndexScreen
import vanstudio.tv.beetv.ui.theme.BVTheme

class AnimeIndexActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                AnimeIndexScreen()
            }
        }
    }
}