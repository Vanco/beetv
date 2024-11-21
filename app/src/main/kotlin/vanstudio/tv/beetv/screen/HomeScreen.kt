package vanstudio.tv.beetv.screen

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.grid.rememberTvLazyGridState
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import vanstudio.tv.beetv.R
import vanstudio.tv.beetv.activities.search.SearchInputActivity
import vanstudio.tv.beetv.activities.user.FavoriteActivity
import vanstudio.tv.beetv.activities.user.FollowingSeasonActivity
import vanstudio.tv.beetv.activities.user.HistoryActivity
import vanstudio.tv.beetv.activities.user.UserInfoActivity
import vanstudio.tv.beetv.component.TopNav
import vanstudio.tv.beetv.component.TopNavItem
import vanstudio.tv.beetv.component.UserPanel
import vanstudio.tv.beetv.screen.home.AnimeScreen
import vanstudio.tv.beetv.screen.home.DynamicsScreen
import vanstudio.tv.beetv.screen.home.PartitionScreen
import vanstudio.tv.beetv.screen.home.PopularScreen
import vanstudio.tv.beetv.screen.home.RecommendScreen
import vanstudio.tv.beetv.util.fInfo
import vanstudio.tv.beetv.util.requestFocus
import vanstudio.tv.beetv.util.toast
import vanstudio.tv.beetv.viewmodel.UserViewModel
import vanstudio.tv.beetv.viewmodel.home.DynamicViewModel
import vanstudio.tv.beetv.viewmodel.home.PopularViewModel
import vanstudio.tv.beetv.viewmodel.home.RecommendViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    recommendViewModel: RecommendViewModel = koinViewModel(),
    popularViewModel: PopularViewModel = koinViewModel(),
    dynamicViewModel: DynamicViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger { }

    val recommendState = rememberTvLazyGridState()
    val popularState = rememberTvLazyGridState()
    val partitionState = rememberTvLazyGridState()
    val animeState = rememberTvLazyListState()
    val dynamicState = rememberTvLazyGridState()

    var selectedTab by remember { mutableStateOf(TopNavItem.Popular) }
    var showUserPanel by remember { mutableStateOf(false) }
    var lastPressBack: Long by remember { mutableLongStateOf(0L) }

    val settingsButtonFocusRequester = remember { FocusRequester() }
    val navFocusRequester = remember { FocusRequester() }

    val onFocusBackToNav: () -> Unit = {
        logger.fInfo { "onFocusBackToNav" }
        navFocusRequester.requestFocus(scope)
    }

    //启动时刷新数据
    LaunchedEffect(Unit) {
        navFocusRequester.requestFocus()
        scope.launch(Dispatchers.IO) {
            recommendViewModel.loadMore()
        }
        scope.launch(Dispatchers.IO) {
            popularViewModel.loadMore()
        }
        scope.launch(Dispatchers.IO) {
            dynamicViewModel.loadMore()
        }
        scope.launch(Dispatchers.IO) {
            userViewModel.updateUserInfo()
        }
    }

    //监听登录变化
    LaunchedEffect(userViewModel.isLogin) {
        if (userViewModel.isLogin) {
            //login
            userViewModel.updateUserInfo()
        } else {
            //logout
            userViewModel.clearUserInfo()
        }
    }

    val handleBack = {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastPressBack < 1000 * 3) {
            logger.fInfo { "Exiting bug video" }
            (context as Activity).finish()
        } else {
            lastPressBack = currentTime
            R.string.home_press_back_again_to_exit.toast(context)
        }
    }

    BackHandler(!showUserPanel) {
        handleBack()
    }

    Box(
        modifier = modifier
    ) {
        Scaffold(
            modifier = Modifier,
            topBar = {
                TopNav(
                    modifier = Modifier.focusRequester(navFocusRequester),
                    isLogin = userViewModel.isLogin,
                    username = userViewModel.username,
                    face = userViewModel.face,
                    settingsButtonFocusRequester = settingsButtonFocusRequester,
                    onSelectedChange = { nav ->
                        selectedTab = nav
                        when (nav) {
                            TopNavItem.Recommend -> {

                            }

                            TopNavItem.Popular -> {
                                //scope.launch(Dispatchers.Default) { popularState.scrollToItem(0, 0) }
                            }

                            TopNavItem.Partition -> {

                            }

                            TopNavItem.Anime -> {

                            }

                            TopNavItem.Dynamics -> {
                                //scope.launch(Dispatchers.Default) { dynamicState.scrollToItem(0, 0) }
                                if (!dynamicViewModel.loading && dynamicViewModel.isLogin && dynamicViewModel.dynamicList.isEmpty()) {
                                    scope.launch(Dispatchers.Default) { dynamicViewModel.loadMore() }
                                }
                            }

                            TopNavItem.Search -> {

                            }
                        }
                    },
                    onClick = { nav ->
                        when (nav) {
                            TopNavItem.Recommend -> {
                                logger.fInfo { "clear recommend data" }
                                recommendViewModel.clearData()
                                logger.fInfo { "reload recommend data" }
                                scope.launch(Dispatchers.IO) { recommendViewModel.loadMore() }
                            }

                            TopNavItem.Popular -> {
                                //scope.launch(Dispatchers.Default) { popularState.scrollToItem(0, 0) }
                                logger.fInfo { "clear popular data" }
                                popularViewModel.clearData()
                                logger.fInfo { "reload popular data" }
                                scope.launch(Dispatchers.IO) { popularViewModel.loadMore() }
                            }

                            TopNavItem.Partition -> {

                            }

                            TopNavItem.Anime -> {

                            }

                            TopNavItem.Dynamics -> {
                                //scope.launch(Dispatchers.Default) { dynamicState.scrollToItem(0, 0) }
                                dynamicViewModel.clearData()
                                scope.launch(Dispatchers.IO) { dynamicViewModel.loadMore() }
                            }

                            TopNavItem.Search -> {
                                context.startActivity(
                                    Intent(context, SearchInputActivity::class.java)
                                )
                            }
                        }
                    },
                    onShowUserPanel = { showUserPanel = true }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding)
            ) {
                Crossfade(
                    targetState = selectedTab,
                    label = "home content cross fade"
                ) { screen ->
                    when (screen) {
                        TopNavItem.Recommend -> RecommendScreen(
                            tvLazyGridState = recommendState,
                            onBackNav = onFocusBackToNav
                        )

                        TopNavItem.Popular -> PopularScreen(
                            tvLazyGridState = popularState,
                            onBackNav = onFocusBackToNav
                        )

                        TopNavItem.Partition -> PartitionScreen(
                            tvLazyGridState = partitionState,
                            onBackNav = onFocusBackToNav
                        ) //FollowingSeasonScreen() //PartitionScreen()
                        TopNavItem.Anime -> AnimeScreen(
                            tvLazyListState = animeState,
                            onBackNav = onFocusBackToNav
                        )

                        TopNavItem.Dynamics -> DynamicsScreen(
                            tvLazyGridState = dynamicState,
                            onBackNav = onFocusBackToNav
                        )

                        else -> PopularScreen(
                            tvLazyGridState = popularState,
                            onBackNav = onFocusBackToNav
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showUserPanel,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                AnimatedVisibility(
                    modifier = Modifier
                        .align(Alignment.TopEnd),
                    visible = showUserPanel,
                    enter = fadeIn() + scaleIn(),
                    exit = shrinkHorizontally()
                ) {
                    UserPanel(
                        modifier = Modifier
                            .padding(12.dp)
                            .onFocusChanged {
                                if (!it.hasFocus) {
                                    settingsButtonFocusRequester.requestFocus()
                                }
                            },
                        username = userViewModel.username,
                        face = userViewModel.face,
                        onHide = { showUserPanel = false },
                        onGoMy = {
                            context.startActivity(Intent(context, UserInfoActivity::class.java))
                        },
                        onGoHistory = {
                            context.startActivity(Intent(context, HistoryActivity::class.java))
                        },
                        onGoFavorite = {
                            context.startActivity(Intent(context, FavoriteActivity::class.java))
                        },
                        onGoFollowing = {
                            context.startActivity(
                                Intent(
                                    context,
                                    FollowingSeasonActivity::class.java
                                )
                            )
                        },
                        onGoLater = {
                            "按钮放在这只是拿来当摆设的！".toast(context)
                        }
                    )
                }
            }
        }
    }
}
