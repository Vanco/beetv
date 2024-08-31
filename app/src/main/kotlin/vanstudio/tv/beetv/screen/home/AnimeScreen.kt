package vanstudio.tv.beetv.screen.home

import android.content.Intent
import android.view.KeyEvent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Carousel
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import vanstudio.tv.biliapi.http.entity.anime.AnimeFeedData
import vanstudio.tv.biliapi.http.entity.anime.CarouselItem
import vanstudio.tv.biliapi.http.entity.web.Hover
import vanstudio.tv.beetv.R
import vanstudio.tv.beetv.activities.anime.AnimeIndexActivity
import vanstudio.tv.beetv.activities.anime.AnimeTimelineActivity
import vanstudio.tv.beetv.activities.user.FollowingSeasonActivity
import vanstudio.tv.beetv.activities.video.SeasonInfoActivity
import vanstudio.tv.beetv.component.videocard.SeasonCard
import vanstudio.tv.beetv.entity.carddata.SeasonCardData
import vanstudio.tv.beetv.entity.proxy.ProxyArea
import vanstudio.tv.beetv.ui.theme.BVTheme
import vanstudio.tv.beetv.util.ImageSize
import vanstudio.tv.beetv.util.focusedBorder
import vanstudio.tv.beetv.util.resizedImageUrl
import vanstudio.tv.beetv.util.toast
import vanstudio.tv.beetv.viewmodel.home.AnimeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnimeScreen(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    onBackNav: () -> Unit,
    animeViewModel: AnimeViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val carouselItems = animeViewModel.carouselItems
    val animeFeeds = animeViewModel.feedItems

    LazyColumn(
        modifier = modifier
            .onPreviewKeyEvent {
                when (it.key) {
                    Key.Back -> {
                        if (it.type == KeyEventType.KeyUp) {
                            scope.launch(Dispatchers.Main) {
                                lazyListState.animateScrollToItem(0)
                            }
                            onBackNav()
                        }
                        return@onPreviewKeyEvent true
                    }
                }
                return@onPreviewKeyEvent false
            },
        state = lazyListState
    ) {
        item {
            AnimeCarousel(
                modifier = Modifier.padding(32.dp, 0.dp),
                data = carouselItems
            )
        }
        item {
            AnimeFeatureButtons(
                modifier = Modifier.padding(32.dp, 24.dp),
                onOpenTimeline = {
                    context.startActivity(Intent(context, AnimeTimelineActivity::class.java))
                },
                onOpenFollowing = {
                    context.startActivity(Intent(context, FollowingSeasonActivity::class.java))
                },
                onOpenIndex = {
                    context.startActivity(Intent(context, AnimeIndexActivity::class.java))
                },
                onOpenGamerAni = {
                    val packageManager = context.packageManager
                    val gamerAniPackageName = "tw.com.gamer.android.animad"
                    packageManager.getLeanbackLaunchIntentForPackage(gamerAniPackageName)?.let {
                        context.startActivity(it)
                    } ?: let {
                        R.string.anime_home_button_gamer_ani_launch_failed.toast(context)
                    }
                }
            )
        }
        itemsIndexed(items = animeFeeds) { index, feedItems ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .onFocusChanged {
                        if (it.hasFocus) {
                            if (index + 10 > animeFeeds.size) {
                                animeViewModel.loadMore()
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                when (feedItems.firstOrNull()?.cardStyle) {
                    "v_card" -> AnimeFeedVideoRow(
                        data = feedItems
                    )

                    "rank" -> AnimeFeedRankRow(
                        data = feedItems
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AnimeCarousel(
    modifier: Modifier = Modifier,
    data: List<CarouselItem>
) {
    val context = LocalContext.current

    Carousel(
        itemCount = data.size,
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(MaterialTheme.shapes.large)
            .focusedBorder(),
        contentTransformEndToStart =
        fadeIn(tween(1000)).togetherWith(fadeOut(tween(1000))),
        contentTransformStartToEnd =
        fadeIn(tween(1000)).togetherWith(fadeOut(tween(1000)))
    ) { itemIndex ->
        AnimeCarouselCard(
            data = data[itemIndex],
            onClick = {
                SeasonInfoActivity.actionStart(
                    context = context,
                    epId = data[itemIndex].episodeId,
                    seasonId = data[itemIndex].seasonId,
                    proxyArea = ProxyArea.checkProxyArea(data[itemIndex].title)
                )
            }
        )
    }
}

@Composable
fun AnimeCarouselCard(
    modifier: Modifier = Modifier,
    data: CarouselItem,
    onClick: () -> Unit = {}
) {
    AsyncImage(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable { onClick() },
        model = data.cover,
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        alignment = Alignment.TopCenter
    )
}

@Composable
private fun AnimeFeatureButtons(
    modifier: Modifier = Modifier,
    onOpenTimeline: () -> Unit,
    onOpenFollowing: () -> Unit,
    onOpenIndex: () -> Unit,
    onOpenGamerAni: () -> Unit = {}
) {
    val buttons = listOf(
        Triple(
            stringResource(R.string.anime_home_button_timeline),
            Icons.Rounded.Alarm,
            onOpenTimeline
        ),
        Triple(
            stringResource(R.string.anime_home_button_following),
            Icons.Rounded.Favorite,
            onOpenFollowing
        ),
        Triple(
            stringResource(R.string.anime_home_button_index),
            Icons.AutoMirrored.Rounded.List,
            onOpenIndex
        ),
        Triple(
            stringResource(R.string.anime_home_button_gamer_ani),
            painterResource(R.drawable.ic_gamer_ani),
            onOpenGamerAni
        )
    )

    Row(
        modifier = modifier.height(80.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        buttons.forEach { (title, icon, onClick) ->
            when (icon) {
                is ImageVector -> AnimeFeatureButton(
                    modifier = Modifier.weight(1f),
                    title = title,
                    icon = icon,
                    onClick = onClick
                )

                is Painter -> AnimeFeatureButton(
                    modifier = Modifier.weight(1f),
                    title = title,
                    icon = icon,
                    onClick = onClick
                )

                else -> {}
            }
        }
    }
}

@Composable
fun AnimeFeatureButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
            pressedContainerColor = MaterialTheme.colorScheme.inverseSurface
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.large),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null)
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
fun AnimeFeatureButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: Painter,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
            pressedContainerColor = MaterialTheme.colorScheme.inverseSurface
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.large),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = icon,
                    contentDescription = null
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
fun AnimeFeedVideoRow(
    modifier: Modifier = Modifier,
    data: List<AnimeFeedData.FeedItem.FeedSubItem>
) {
    val context = LocalContext.current
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        data.forEachIndexed { index, feedItem ->
            val cardModifier = if (index == data.lastIndex) {
                Modifier.onPreviewKeyEvent {
                    when (it.key) {
                        Key.DirectionRight -> return@onPreviewKeyEvent true
                    }
                    false
                }
            } else {
                Modifier
            }

            item {
                SeasonCard(
                    modifier = cardModifier,
                    coverHeight = 180.dp,
                    data = SeasonCardData(
                        seasonId = feedItem.seasonId ?: 0,
                        title = feedItem.title,
                        subTitle = feedItem.subTitle,
                        cover = feedItem.cover.resizedImageUrl(ImageSize.SeasonCoverThumbnail),
                        rating = feedItem.rating ?: ""
                    ),
                    onClick = {
                        SeasonInfoActivity.actionStart(
                            context = context,
                            seasonId = feedItem.seasonId,
                            proxyArea = ProxyArea.checkProxyArea(feedItem.title)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun AnimeFeedRankRow(
    modifier: Modifier = Modifier,
    data: List<AnimeFeedData.FeedItem.FeedSubItem>
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .height(300.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            // light theme color: Color(250, 222, 214)
                            Color(20, 18, 17),
                            Color(20, 18, 17).copy(alpha = 0.298f)
                        )
                    )
                )
        ) {}
        BoxWithConstraints {
            AsyncImage(
                modifier = Modifier
                    .fillMaxHeight()
                    .offset(x = (-1 * (0.25 * 1.6 * this.maxHeight.value)).dp)
                    .graphicsLayer { alpha = 0.99f }
                    .drawWithContent {
                        val colors = listOf(
                            Color.Black,
                            Color.Transparent
                        )
                        drawContent()
                        drawRect(
                            brush = Brush.horizontalGradient(colors),
                            blendMode = BlendMode.DstIn
                        )
                        drawRect(
                            brush = Brush.verticalGradient(colors),
                            blendMode = BlendMode.DstIn
                        )
                    },
                model = data.first().cover,
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                alpha = 1f
            )
        }
        Row(
            modifier = Modifier
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(240.dp)
                    .padding(32.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = data.first().title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Text(
                    text = data.first().subTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            LazyRow(
                modifier = modifier,
                contentPadding = PaddingValues(horizontal = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                data.first().subItems?.forEachIndexed { index, feedItem ->
                    val cardModifier = if (index == data.first().subItems?.lastIndex) {
                        Modifier.onPreviewKeyEvent {
                            when (it.nativeKeyEvent.keyCode) {
                                KeyEvent.KEYCODE_DPAD_RIGHT -> return@onPreviewKeyEvent true
                            }
                            false
                        }
                    } else {
                        Modifier
                    }

                    item {
                        SeasonCard(
                            modifier = cardModifier,
                            coverHeight = 180.dp,
                            data = SeasonCardData(
                                seasonId = feedItem.seasonId ?: 0,
                                title = feedItem.title,
                                subTitle = feedItem.subTitle,
                                cover = feedItem.cover.resizedImageUrl(ImageSize.SeasonCoverThumbnail),
                                rating = feedItem.rating ?: ""
                            ),
                            onClick = {
                                SeasonInfoActivity.actionStart(
                                    context = context,
                                    seasonId = feedItem.seasonId,
                                    proxyArea = ProxyArea.checkProxyArea(feedItem.title)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}


@Preview(device = "id:tv_1080p")
@Composable
fun AnimeFeatureButtonsPreview() {
    BVTheme {
        AnimeFeatureButtons(
            modifier = Modifier,
            onOpenTimeline = {},
            onOpenFollowing = {},
            onOpenIndex = {},
            onOpenGamerAni = {}
        )
    }
}

@Preview(device = "id:tv_1080p")
@Composable
fun AnimeFeedRankRowPreview() {
    val data = listOf(
        AnimeFeedData.FeedItem.FeedSubItem(
            cardStyle = "rank",
            rankId = 126,
            cover = "http://i0.hdslb.com/bfs/archive/aae451dabf64ead2e983f92be76039a8ba233ade.png",
            title = "热门热血番剧榜",
            subTitle = "每小时更新",
            report = AnimeFeedData.FeedItem.FeedSubItem.Report(),
            subItems = List(8) {
                AnimeFeedData.FeedItem.FeedSubItem(
                    cardStyle = "v_card",
                    rankId = 0,
                    cover = "https://i0.hdslb.com/bfs/bangumi/image/f610305ad3922bee9d51748ab38da0c54e785b44.png",
                    hover = Hover(
                        img = "http://i0.hdslb.com/bfs/archive/aae451dabf64ead2e983f92be76039a8ba233ade.png",
                        text = listOf("漫画改", "热血", "更新至第6话")
                    ),
                    title = "解雇后走上人生巅峰",
                    subTitle = "被解雇的暗黑士兵慢生活的第二人生",
                    report = AnimeFeedData.FeedItem.FeedSubItem.Report()
                )
            }
        )
    )
    BVTheme {
        AnimeFeedRankRow(data = data)
    }
}