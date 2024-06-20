package vanstudio.tv.beetv.component.controllers

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import vanstudio.tv.beetv.util.formatMinSec

@Composable
fun GoBackHistoryTip(
    modifier: Modifier = Modifier,
    played: Int
) {
    Surface(
        modifier = modifier,
        colors = SurfaceDefaults.colors(
            containerColor = Color.Black.copy(alpha = 0.6f)
        )
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = "上次播放到 “${played.toLong().formatMinSec()}”，按下确认键返回",
            style = MaterialTheme.typography.titleLarge
        )
    }
}