package vanstudio.tv.beetv.viewmodel.search

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import vanstudio.tv.biliapi.repositories.SearchFilterDuration
import vanstudio.tv.biliapi.repositories.SearchFilterOrderType
import vanstudio.tv.biliapi.repositories.SearchRepository
import vanstudio.tv.biliapi.repositories.SearchType
import vanstudio.tv.biliapi.repositories.SearchTypePage
import vanstudio.tv.biliapi.repositories.SearchTypeResult
import vanstudio.tv.beetv.R
import vanstudio.tv.beetv.util.Partition
import vanstudio.tv.beetv.util.Prefs
import vanstudio.tv.beetv.util.fInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchResultViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    var keyword by mutableStateOf("")
    var searchType by mutableStateOf(SearchType.Video)

    var videoSearchResult by mutableStateOf(SearchResult(SearchType.Video))
    var mediaBangumiSearchResult by mutableStateOf(SearchResult(SearchType.MediaBangumi))
    var mediaFtSearchResult by mutableStateOf(SearchResult(SearchType.MediaFt))
    var biliUserSearchResult by mutableStateOf(SearchResult(SearchType.BiliUser))

    var selectedOrder by mutableStateOf(SearchFilterOrderType.MostClicks)
    var selectedDuration by mutableStateOf(SearchFilterDuration.All)
    var selectedPartition: Partition? by mutableStateOf(null)
    var selectedChildPartition: Partition? by mutableStateOf(null)

    private var updating = false
    val hasMore = true
    private var page = SearchTypePage()

    var enableProxySearchResult = false

    fun update() {
        resetPages()
        clearResults()
        SearchType.entries.forEach { loadMore(it, true) }
    }

    private fun resetPages() {
        videoSearchResult.resetPage()
        mediaBangumiSearchResult.resetPage()
        mediaFtSearchResult.resetPage()
        biliUserSearchResult.resetPage()
    }

    private fun clearResults() {
        videoSearchResult.clearResult()
        mediaBangumiSearchResult.clearResult()
        mediaFtSearchResult.clearResult()
        biliUserSearchResult.clearResult()
    }

    fun loadMore(
        searchType: SearchType,
        ignoreUpdating: Boolean = false
    ) {
        if (!hasMore) return
        if (updating && !ignoreUpdating) return

        updating = true
        viewModelScope.launch(Dispatchers.IO) {
            logger.fInfo { "Load search result: [keyword=$keyword, type=$searchType, page=${page}]" }
            runCatching {
                val searchResultResponse = searchRepository.searchType(
                    keyword = keyword,
                    type = searchType,
                    page = page,
                    tid = selectedChildPartition?.tid ?: selectedPartition?.tid,
                    order = selectedOrder,
                    duration = selectedDuration,
                    preferApiType = Prefs.apiType,
                    enableProxy = enableProxySearchResult
                )

                when (searchType) {
                    SearchType.Video -> videoSearchResult =
                        videoSearchResult.appendSearchResultData(searchResultResponse)

                    SearchType.MediaBangumi -> mediaBangumiSearchResult =
                        mediaBangumiSearchResult.appendSearchResultData(searchResultResponse)

                    SearchType.MediaFt -> mediaFtSearchResult =
                        mediaFtSearchResult.appendSearchResultData(searchResultResponse)

                    SearchType.BiliUser -> biliUserSearchResult =
                        biliUserSearchResult.appendSearchResultData(searchResultResponse)
                }

                page = searchResultResponse.page
            }
            updating = false
        }
    }

    data class SearchResult(
        var type: SearchType,
        var videos: List<SearchTypeResult.Video> = emptyList(),
        var mediaBangumis: List<SearchTypeResult.Pgc> = emptyList(),
        var mediaFts: List<SearchTypeResult.Pgc> = emptyList(),
        var biliUsers: List<SearchTypeResult.User> = emptyList(),
        var page: SearchTypePage = SearchTypePage()
    ) {
        val count get() = videos.size + mediaBangumis.size + mediaFts.size + biliUsers.size

        fun resetPage() {
            page = SearchTypePage()
        }

        fun clearResult() {
            videos = emptyList()
            mediaBangumis = emptyList()
            mediaFts = emptyList()
            biliUsers = emptyList()
        }

        fun appendSearchResultData(searchTypeResult: SearchTypeResult): SearchResult {
            return when (type) {
                SearchType.Video -> {
                    SearchResult(type).apply {
                        this.videos = this@SearchResult.videos + searchTypeResult.videos
                    }
                }

                SearchType.MediaBangumi -> {
                    SearchResult(type).apply {
                        this.mediaBangumis = this@SearchResult.mediaBangumis + searchTypeResult.pgcs
                    }
                }

                SearchType.MediaFt -> {
                    SearchResult(type).apply {
                        this.mediaFts = this@SearchResult.mediaFts + searchTypeResult.pgcs
                    }
                }

                SearchType.BiliUser -> {
                    SearchResult(type).apply {
                        this.biliUsers = this@SearchResult.biliUsers + searchTypeResult.users
                    }
                }
            }
        }
    }
}

enum class SearchResultType(
    val type: String,
    private val strRes: Int
) {
    Video(type = "video", strRes = R.string.search_result_type_name_video),
    MediaBangumi(type = "media_bangumi", R.string.search_result_type_name_media_bangumi),
    MediaFt(type = "media_ft", strRes = R.string.search_result_type_name_media_ft),
    BiliUser(type = "bili_user", strRes = R.string.search_result_type_name_bili_user);

    fun getDisplayName(context: Context) = context.getString(strRes)
}
