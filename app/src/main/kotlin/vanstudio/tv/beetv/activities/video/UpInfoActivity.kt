package vanstudio.tv.beetv.activities.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import vanstudio.tv.beetv.screen.user.UpSpaceScreen
import vanstudio.tv.beetv.ui.theme.BVTheme

class UpInfoActivity : ComponentActivity() {
    companion object {
        fun actionStart(context: Context, mid: Long, name: String) {
            context.startActivity(
                Intent(context, UpInfoActivity::class.java).apply {
                    putExtra("mid", mid)
                    putExtra("name", name)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                UpSpaceScreen()
            }
        }
    }
}
