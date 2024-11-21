package vanstudio.tv.beetv.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvGridItemSpan
import androidx.tv.foundation.lazy.grid.TvLazyGridState
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import vanstudio.tv.beetv.R
import vanstudio.tv.beetv.activities.video.SeasonInfoActivity
import vanstudio.tv.beetv.component.videocard.SeasonCard
import vanstudio.tv.beetv.entity.carddata.SeasonCardData
import vanstudio.tv.beetv.entity.proxy.ProxyArea
import vanstudio.tv.beetv.screen.user.FollowingSeasonFilter
import vanstudio.tv.beetv.util.ImageSize
import vanstudio.tv.beetv.util.fInfo
import vanstudio.tv.beetv.util.resizedImageUrl
import vanstudio.tv.beetv.viewmodel.user.FollowingSeasonPViewModel
import vanstudio.tv.biliapi.entity.season.FollowingSeasonStatus
import vanstudio.tv.biliapi.entity.season.FollowingSeasonType

@Composable
fun PartitionScreen(
    modifier: Modifier = Modifier,
    tvLazyGridState: TvLazyGridState,
    onBackNav: () -> Unit,
    followingSeasonViewModel: FollowingSeasonPViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentFocusedIndex by remember { mutableIntStateOf(0) }

    val logger = KotlinLogging.logger { }

    var currentIndex by remember { mutableIntStateOf(0) }

    var showFilter by remember { mutableStateOf(false) }

    val followingSeasons = followingSeasonViewModel.followingSeasons
    var followingSeasonType by remember { mutableStateOf(followingSeasonViewModel.followingSeasonType) }
    var followingSeasonStatus by remember { mutableStateOf(followingSeasonViewModel.followingSeasonStatus) }
    val noMore = followingSeasonViewModel.noMore

    val updateType: (FollowingSeasonType) -> Unit = {
        followingSeasonType = it
        followingSeasonViewModel.followingSeasonType = it
    }

    val updateStatus: (FollowingSeasonStatus) -> Unit = {
        followingSeasonStatus = it
        followingSeasonViewModel.followingSeasonStatus = it
    }

    val onLongClickSeason = {
        showFilter = true
    }

    LaunchedEffect(followingSeasonType, followingSeasonStatus) {
        logger.fInfo { "Start update search result because filter updated" }
        followingSeasonViewModel.clearData()
        followingSeasonViewModel.loadMore()
    }

    TvLazyVerticalGrid(
        modifier = modifier.onPreviewKeyEvent {
            when (it.key) {
                Key.Back -> {
                    if (it.type == KeyEventType.KeyUp) {
                        scope.launch(Dispatchers.Main) {
                            tvLazyGridState.animateScrollToItem(0)
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
        state = tvLazyGridState,
        columns = TvGridCells.Fixed(6),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        itemsIndexed(items = followingSeasons) { index, followingSeason ->
            SeasonCard(
                data = SeasonCardData(
                    seasonId = followingSeason.seasonId,
                    title = followingSeason.title,
                    cover = followingSeason.cover.resizedImageUrl(ImageSize.SeasonCoverThumbnail),
                    rating = null
                ),
                onFocus = {
                    currentIndex = index
                    if (index + 30 > followingSeasons.size) {
                        println("load more by focus")
                        followingSeasonViewModel.loadMore()
                    }
                },
                onClick = {
                    SeasonInfoActivity.actionStart(
                        context = context,
                        seasonId = followingSeason.seasonId,
                        proxyArea = ProxyArea.checkProxyArea(followingSeason.title)
                    )
                },
                onLongClick = onLongClickSeason
            )
        }
        if (followingSeasons.isEmpty() && noMore) {
            item(
                span = { TvGridItemSpan(6) }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = stringResource(R.string.no_data))
                        OutlinedButton(onClick = { showFilter = true }) {
                            Text(text = stringResource(R.string.filter_dialog_open_tip_click))
                        }
                    }
                }
            }
        }
    }

    FollowingSeasonFilter(
        show = showFilter,
        onHideFilter = { showFilter = false },
        selectedType = followingSeasonType,
        selectedStatus = followingSeasonStatus,
        onSelectedTypeChange = updateType,
        onSelectedStatusChange = updateStatus
    )
}