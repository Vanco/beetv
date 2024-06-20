package vanstudio.tv.beetv.viewmodel.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import vanstudio.tv.biliapi.entity.home.RecommendItem
import vanstudio.tv.biliapi.entity.home.RecommendPage
import vanstudio.tv.biliapi.repositories.RecommendVideoRepository
import vanstudio.tv.beetv.BVApp
import vanstudio.tv.beetv.util.Prefs
import vanstudio.tv.beetv.util.fError
import vanstudio.tv.beetv.util.fInfo
import vanstudio.tv.beetv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecommendViewModel(
    private val recommendVideoRepository: RecommendVideoRepository
) : ViewModel() {
    private val logger = KotlinLogging.logger {}
    val recommendVideoList = mutableStateListOf<RecommendItem>()

    private var nextPage = RecommendPage()
    var loading = false

    suspend fun loadMore() {
        var loadCount = 0
        val maxLoadMoreCount = 3
        if (!loading) {
            if (recommendVideoList.size == 0) {
                // first load data
                while (recommendVideoList.size < 14 && loadCount < maxLoadMoreCount) {
                    loadData()
                    if (loadCount != 0) logger.fInfo { "Load more recommend videos because items too less" }
                    loadCount++
                }
            } else {
                loadData()
            }
        }
    }

    private suspend fun loadData() {
        loading = true
        logger.fInfo { "Load more recommend videos" }
        runCatching {
            val recommendData = recommendVideoRepository.getRecommendVideos(
                page = nextPage,
                preferApiType = Prefs.apiType
            )
            nextPage = recommendData.nextPage
            recommendVideoList.addAll(recommendData.items)
        }.onFailure {
            logger.fError { "Load recommend video list failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "加载推荐视频失败: ${it.localizedMessage}".toast(BVApp.context)
            }
        }
        loading = false
    }

    fun clearData() {
        recommendVideoList.clear()
        nextPage = RecommendPage()
        loading = false
    }
}