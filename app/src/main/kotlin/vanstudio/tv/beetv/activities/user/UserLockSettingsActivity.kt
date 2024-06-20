package vanstudio.tv.beetv.activities.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import vanstudio.tv.beetv.screen.user.lock.UserLockSettingsScreen
import vanstudio.tv.beetv.ui.theme.BVTheme

class UserLockSettingsActivity : ComponentActivity() {

    companion object {
        fun actionStart(
            context: Context,
            uid: Long
        ) {
            context.startActivity(
                Intent(context, UserLockSettingsActivity::class.java).apply {
                    putExtra("uid", uid)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                UserLockSettingsScreen()
            }
        }
    }
}