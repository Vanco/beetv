package vanstudio.tv.beetv.activities.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import vanstudio.tv.beetv.screen.user.UserSwitchScreen
import vanstudio.tv.beetv.ui.theme.BVTheme

class UserSwitchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                UserSwitchScreen()
            }
        }
    }
}