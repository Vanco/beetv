package vanstudio.tv.beetv.viewmodel.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import vanstudio.tv.biliapi.entity.rank.PopularVideo
import vanstudio.tv.biliapi.entity.rank.PopularVideoPage
import vanstudio.tv.biliapi.repositories.RecommendVideoRepository
import vanstudio.tv.beetv.BVApp
import vanstudio.tv.beetv.util.Prefs
import vanstudio.tv.beetv.util.fError
import vanstudio.tv.beetv.util.fInfo
import vanstudio.tv.beetv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PopularViewModel(
    private val recommendVideoRepository: RecommendVideoRepository
) : ViewModel() {
    private val logger = KotlinLogging.logger {}
    val popularVideoList = mutableStateListOf<PopularVideo>()

    private var nextPage = PopularVideoPage()
    var loading = false

    suspend fun loadMore() {
        if (!loading) loadData()
    }

    private suspend fun loadData() {
        loading = true
        logger.fInfo { "Load more popular videos" }
        runCatching {
            val popularVideoData = recommendVideoRepository.getPopularVideos(
                page = nextPage,
                preferApiType = Prefs.apiType
            )
            nextPage = popularVideoData.nextPage
            popularVideoList.addAll(popularVideoData.list)
        }.onFailure {
            logger.fError { "Load popular video list failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "加载热门视频失败: ${it.localizedMessage}".toast(BVApp.context)
            }
        }
        loading = false
    }

    fun clearData() {
        popularVideoList.clear()
        nextPage = PopularVideoPage()
        loading = false
    }
}