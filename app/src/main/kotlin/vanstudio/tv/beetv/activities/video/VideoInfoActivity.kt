package vanstudio.tv.beetv.activities.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import vanstudio.tv.beetv.entity.proxy.ProxyArea
import vanstudio.tv.beetv.screen.VideoInfoScreen
import vanstudio.tv.beetv.ui.theme.BVTheme

class VideoInfoActivity : ComponentActivity() {
    companion object {
        fun actionStart(
            context: Context, aid: Long,
            fromSeason: Boolean = false,
            proxyArea: ProxyArea = ProxyArea.MainLand
        ) {
            context.startActivity(
                Intent(context, VideoInfoActivity::class.java).apply {
                    putExtra("aid", aid)
                    putExtra("fromSeason", fromSeason)
                    putExtra("proxy_area", proxyArea.ordinal)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BVTheme {
                VideoInfoScreen()
            }
        }
    }
}
