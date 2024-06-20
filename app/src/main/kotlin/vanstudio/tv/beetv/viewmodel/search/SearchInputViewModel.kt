package vanstudio.tv.beetv.viewmodel.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import vanstudio.tv.biliapi.entity.search.Hotword
import vanstudio.tv.biliapi.repositories.SearchRepository
import vanstudio.tv.beetv.BVApp
import vanstudio.tv.beetv.dao.AppDatabase
import vanstudio.tv.beetv.entity.db.SearchHistoryDB
import vanstudio.tv.beetv.util.Prefs
import vanstudio.tv.beetv.util.fInfo
import vanstudio.tv.beetv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class SearchInputViewModel(
    private val searchRepository: SearchRepository,
    private val db: AppDatabase = BVApp.getAppDatabase()
) : ViewModel() {
    private val logger = KotlinLogging.logger { }

    var keyword by mutableStateOf("")
    val hotwords = mutableStateListOf<Hotword>()
    val suggests = mutableStateListOf<String>()
    val searchHistories = mutableStateListOf<SearchHistoryDB>()

    init {
        updateHotwords()
        loadSearchHistories()
    }

    private fun updateHotwords() {
        logger.fInfo { "Update hotwords" }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val hotwordData = searchRepository.getSearchHotwords(
                    limit = 50,
                    preferApiType = Prefs.apiType
                )
                logger.debug { "Find hotwords: $hotwordData" }
                hotwords.addAll(hotwordData)
            }.onFailure {
                withContext(Dispatchers.Main) {
                    "bilibili 热搜加载失败".toast(BVApp.context)
                }
                logger.info { it.stackTraceToString() }
            }
        }
    }

    fun updateSuggests() {
        logger.fInfo { "Update search suggests with '$keyword'" }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val keywordSuggest = searchRepository.getSearchSuggest(
                    keyword = keyword,
                    preferApiType = Prefs.apiType
                )
                logger.debug { "Find suggests: $keywordSuggest" }
                suggests.clear()
                suggests.addAll(keywordSuggest)
            }.onFailure {
                withContext(Dispatchers.Main) {
                    "bilibili 搜索建议加载失败".toast(BVApp.context)
                }
                logger.info { it.stackTraceToString() }
            }
        }
    }

    private fun loadSearchHistories() {
        logger.fInfo { "Load search histories" }
        viewModelScope.launch(Dispatchers.IO) {
            //当第一次调用时，可能会出现异常 IllegalStateException: Reading a state that was created after the snapshot was taken or in a snapshot that has not yet been applied
            runCatching { searchHistories.clear() }
            runCatching {
                searchHistories.addAll(db.searchHistoryDao().getHistories(20))
                logger.fInfo { "Load search histories finish, size: ${searchHistories.size}" }
            }
        }
    }

    fun addSearchHistory(keyword: String) {
        logger.fInfo { "Add search history: $keyword" }
        viewModelScope.launch(Dispatchers.IO) {
            db.searchHistoryDao().findHistory(keyword)?.let { history ->
                logger.fInfo { "Search history $keyword already exist" }
                history.searchDate = Date()
                db.searchHistoryDao().update(history)
            } ?: let {
                logger.fInfo { "Insert new search history $keyword" }
                val history = SearchHistoryDB(keyword = keyword)
                db.searchHistoryDao().insert(history)
            }
            loadSearchHistories()
        }
    }
}