package vanstudio.tv.beetv.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
import io.github.oshai.kotlinlogging.KotlinLogging
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
import vanstudio.tv.beetv.viewmodel.user.FollowingSeasonViewModel
import vanstudio.tv.biliapi.entity.season.FollowingSeasonStatus
import vanstudio.tv.biliapi.entity.season.FollowingSeasonType

/**
 * this screen copy from FollowingSeasonScreen without title
 */
@Composable
fun PartitionScreen (
    modifier: Modifier = Modifier,
    followingSeasonViewModel: FollowingSeasonViewModel = koinViewModel()
) {
    val context = LocalContext.current
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

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(6),
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
                span = { GridItemSpan(6) }
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