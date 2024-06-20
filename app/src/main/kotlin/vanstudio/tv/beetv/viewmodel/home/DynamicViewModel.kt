package vanstudio.tv.beetv.viewmodel.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import vanstudio.tv.biliapi.entity.user.DynamicVideo
import vanstudio.tv.biliapi.http.entity.AuthFailureException
import vanstudio.tv.biliapi.repositories.UserRepository
import vanstudio.tv.beetv.BVApp
import vanstudio.tv.beetv.BuildConfig
import vanstudio.tv.beetv.R
import vanstudio.tv.beetv.util.Prefs
import vanstudio.tv.beetv.util.fInfo
import vanstudio.tv.beetv.util.fWarn
import vanstudio.tv.beetv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vanstudio.tv.beetv.repository.UserRepository as BvUserRepository

class DynamicViewModel(
    private val bvUserRepository: BvUserRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val logger = KotlinLogging.logger {}
    val dynamicList = mutableStateListOf<DynamicVideo>()

    private var currentPage = 0
    var loading = false
    var hasMore = true
    private var historyOffset: String? = null
    private var updateBaseline: String? = null
    val isLogin get() = bvUserRepository.isLogin

    suspend fun loadMore() {
        if (!loading) loadData()
    }

    private suspend fun loadData() {
        if (!hasMore || !bvUserRepository.isLogin) return
        loading = true
        logger.fInfo { "Load more dynamic videos [apiType=${Prefs.apiType}, offset=$historyOffset, page=${currentPage + 1}]" }
        runCatching {
            val dynamicVideoData = userRepository.getDynamicVideos(
                page = ++currentPage,
                offset = historyOffset ?: "",
                updateBaseline = updateBaseline ?: "",
                preferApiType = Prefs.apiType
            )
            dynamicList.addAll(dynamicVideoData.videos)
            historyOffset = dynamicVideoData.historyOffset
            updateBaseline = dynamicVideoData.updateBaseline
            hasMore = dynamicVideoData.hasMore

            logger.fInfo { "Load dynamic list page: ${currentPage},size: ${dynamicVideoData.videos.size}" }
            val avList = dynamicVideoData.videos.map {
                it.aid
            }
            logger.fInfo { "Load dynamic size: ${avList.size}" }
            logger.info { "Load dynamic list ${avList}}" }
        }.onFailure {
            logger.fWarn { "Load dynamic list failed: ${it.stackTraceToString()}" }
            when (it) {
                is AuthFailureException -> {
                    withContext(Dispatchers.Main) {
                        BVApp.context.getString(R.string.exception_auth_failure)
                            .toast(BVApp.context)
                    }
                    logger.fInfo { "User auth failure" }
                    if (!BuildConfig.DEBUG) bvUserRepository.logout()
                }

                else -> {
                    withContext(Dispatchers.Main) {
                        "加载动态失败: ${it.localizedMessage}".toast(BVApp.context)
                    }
                }
            }
        }
        loading = false
    }

    fun clearData() {
        dynamicList.clear()
        currentPage = 0
        loading = false
        hasMore = true
        historyOffset = null
    }
}