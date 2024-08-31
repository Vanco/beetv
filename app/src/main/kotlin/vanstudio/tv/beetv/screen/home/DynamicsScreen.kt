package vanstudio.tv.beetv.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import vanstudio.tv.beetv.activities.video.VideoInfoActivity
import vanstudio.tv.beetv.component.LoadingTip
import vanstudio.tv.beetv.component.videocard.SmallVideoCard
import vanstudio.tv.beetv.entity.carddata.VideoCardData
import vanstudio.tv.beetv.entity.proxy.ProxyArea
import vanstudio.tv.beetv.viewmodel.home.DynamicViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun DynamicsScreen(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState,
    onBackNav: () -> Unit,
    dynamicViewModel: DynamicViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentFocusedIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentFocusedIndex) {
        if (currentFocusedIndex + 24 > dynamicViewModel.dynamicList.size) {
            scope.launch(Dispatchers.Default) { dynamicViewModel.loadMore() }
        }
    }

    if (dynamicViewModel.isLogin) {
        LazyVerticalGrid(
            modifier = modifier
                .onPreviewKeyEvent {
                    when (it.key) {
                        Key.Back -> {
                            if (it.type == KeyEventType.KeyUp) {
                                scope.launch(Dispatchers.Main) {
                                    lazyGridState.animateScrollToItem(0)
                                }
                                onBackNav()
                            }
                            return@onPreviewKeyEvent true
                        }

                        Key.DirectionRight -> {
                            if (currentFocusedIndex % 4 == 3) {
                                return@onPreviewKeyEvent true
                            }
                        }
                    }
                    return@onPreviewKeyEvent false
                },
            state = lazyGridState,
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            itemsIndexed(dynamicViewModel.dynamicList) { index, dynamic ->
                SmallVideoCard(
                    data = VideoCardData(
                        avid = dynamic.aid,
                        title = dynamic.title,
                        cover = dynamic.cover,
                        play = dynamic.play,
                        danmaku = dynamic.danmaku,
                        upName = dynamic.author,
                        time = dynamic.duration * 1000L
                    ),
                    onClick = {
                        VideoInfoActivity.actionStart(
                            context = context,
                            aid = dynamic.aid,
                            proxyArea = ProxyArea.checkProxyArea(dynamic.title)
                        )
                    },
                    onFocus = { currentFocusedIndex = index }
                )
            }
            if (dynamicViewModel.loading)
                item(
                    span = { GridItemSpan(4) }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingTip()
                    }
                }

            if (!dynamicViewModel.hasMore)
                item(
                    span = { GridItemSpan(4) }
                ) {
                    Text(
                        text = "没有更多了捏",
                        color = Color.White
                    )
                }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "请先登录")
        }
    }
}
