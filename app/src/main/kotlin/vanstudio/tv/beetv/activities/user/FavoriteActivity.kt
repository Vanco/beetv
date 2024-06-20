package vanstudio.tv.beetv.activities.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import vanstudio.tv.beetv.screen.user.FavoriteScreen
import vanstudio.tv.beetv.ui.theme.BVTheme

class FavoriteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                FavoriteScreen()
            }
        }
    }
}
