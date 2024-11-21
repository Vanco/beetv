package vanstudio.tv.beetv.screen.user

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvGridItemSpan
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
import vanstudio.tv.biliapi.entity.season.FollowingSeasonStatus
import vanstudio.tv.biliapi.entity.season.FollowingSeasonType
import vanstudio.tv.beetv.R
import vanstudio.tv.beetv.activities.video.SeasonInfoActivity
import vanstudio.tv.beetv.component.videocard.SeasonCard
import vanstudio.tv.beetv.entity.carddata.SeasonCardData
import vanstudio.tv.beetv.entity.proxy.ProxyArea
import vanstudio.tv.beetv.util.ImageSize
import vanstudio.tv.beetv.util.fInfo
import vanstudio.tv.beetv.util.getDisplayName
import vanstudio.tv.beetv.util.resizedImageUrl
import vanstudio.tv.beetv.viewmodel.user.FollowingSeasonViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.androidx.compose.koinViewModel

@Composable
fun FollowingSeasonScreen(
    modifier: Modifier = Modifier,
    followingSeasonViewModel: FollowingSeasonViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val logger = KotlinLogging.logger { }

    var currentIndex by remember { mutableIntStateOf(0) }
    val showLargeTitle by remember { derivedStateOf { currentIndex < 6 } }
    val titleFontSize by animateFloatAsState(
        targetValue = if (showLargeTitle) 48f else 24f,
        label = "title font size"
    )
    val subtitleFontSize by animateFloatAsState(
        targetValue = if (showLargeTitle) 36f else 24f,
        label = "subtitle font size"
    )

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

    Scaffold(
        modifier = modifier,
        topBar = {
            Box(
                modifier = Modifier.padding(
                    start = 48.dp,
                    top = 24.dp,
                    bottom = 8.dp,
                    end = 48.dp
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = stringResource(R.string.title_activity_following_season),
                            fontSize = titleFontSize.sp
                        )
                        Text(
                            text = followingSeasonType.getDisplayName(context),
                            fontSize = subtitleFontSize.sp
                        )
                        Text(
                            text = "(${followingSeasonStatus.getDisplayName(context)})",
                            fontSize = subtitleFontSize.sp
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                    ) {
                        Text(
                            text = stringResource(R.string.filter_dialog_open_tip),
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        if (noMore) {
                            Text(
                                text = stringResource(
                                    R.string.load_data_count_no_more,
                                    followingSeasonViewModel.followingSeasons.size
                                ),
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        } else {
                            Text(
                                text = stringResource(
                                    R.string.load_data_count,
                                    followingSeasonViewModel.followingSeasons.size
                                ),
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        TvLazyVerticalGrid(
            modifier = Modifier.padding(innerPadding),
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