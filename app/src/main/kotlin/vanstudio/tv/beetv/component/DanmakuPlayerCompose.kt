package vanstudio.tv.beetv.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import com.kuaishou.akdanmaku.ui.DanmakuView

@Composable
fun DanmakuPlayerCompose(
    modifier: Modifier = Modifier,
    danmakuPlayer: DanmakuPlayer?
) {
    val context = LocalContext.current
    var danmakuView: DanmakuView? by remember { mutableStateOf(null) }

    DisposableEffect(key1 = Unit) {
        onDispose {
            danmakuPlayer?.release()
        }
    }

    LaunchedEffect(danmakuPlayer) {
        if (danmakuView != null) {
            danmakuPlayer?.bindView(danmakuView!!)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                danmakuView = DanmakuView(context)
                danmakuView!!
            }
        )
    }
}
