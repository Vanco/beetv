package vanstudio.tv.beetv.component.controllers

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import vanstudio.tv.beetv.ui.theme.BVTheme

@Composable
fun BufferingTip(
    modifier: Modifier = Modifier,
    speed: String
) {
    Surface(
        modifier = modifier,
        colors = SurfaceDefaults.colors(
            containerColor = Color.Black.copy(0.5f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(36.dp)
                    .padding(8.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
            Text(
                modifier = Modifier,
                text = "缓冲中...$speed",
                fontSize = 24.sp
            )
        }
    }
}

@Preview
@Composable
fun BufferingTipPreview() {
    BVTheme {
        BufferingTip(
            modifier = Modifier.padding(10.dp),
            speed = ""
        )
    }
}