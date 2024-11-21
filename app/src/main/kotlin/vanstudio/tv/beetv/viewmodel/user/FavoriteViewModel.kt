package vanstudio.tv.beetv.viewmodel.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import vanstudio.tv.biliapi.entity.FavoriteFolderMetadata
import vanstudio.tv.biliapi.entity.FavoriteItemType
import vanstudio.tv.biliapi.repositories.FavoriteRepository
import vanstudio.tv.beetv.entity.carddata.VideoCardData
import vanstudio.tv.beetv.util.Prefs
import vanstudio.tv.beetv.util.fInfo
import vanstudio.tv.beetv.util.fWarn
import vanstudio.tv.beetv.util.swapList
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    var favoriteFolderMetadataList = mutableStateListOf<FavoriteFolderMetadata>()
    var favorites = mutableStateListOf<VideoCardData>()

    var currentFavoriteFolderMetadata: FavoriteFolderMetadata? by mutableStateOf(null)

    private var pageSize = 20
    private var pageNumber = 1
    private var hasMore = true

    private var updatingFolders = false
    private var updatingFolderItems = false

    init {
        updateFoldersInfo()
    }

    private fun updateFoldersInfo() {
        if (updatingFolders) return
        updatingFolders = true
        logger.fInfo { "Updating favorite folders" }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val favoriteFolderMetadataList =
                    favoriteRepository.getAllFavoriteFolderMetadataList(
                        mid = Prefs.uid,
                        preferApiType = Prefs.apiType
                    )
                this@FavoriteViewModel.favoriteFolderMetadataList.swapList(
                    favoriteFolderMetadataList
                )
                currentFavoriteFolderMetadata = favoriteFolderMetadataList.firstOrNull()
                logger.fInfo { "Update favorite folders success: ${favoriteFolderMetadataList.map { it.id }}" }
            }.onFailure {
                logger.fWarn { "Update favorite folders failed: ${it.stackTraceToString()}" }
                //这里返回的数据并不会有用户认证失败的错误返回，没必要做身份验证失败提示
            }.onSuccess {
                updateFolderItems()
            }
            updatingFolders = false
        }
    }

    fun updateFolderItems() {
        if (updatingFolderItems || !hasMore) return
        updatingFolderItems = true
        logger.fInfo { "Updating favorite folder items with media id: ${currentFavoriteFolderMetadata?.id}" }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val favoriteFolderData = favoriteRepository.getFavoriteFolderData(
                    mediaId = currentFavoriteFolderMetadata!!.id,
                    pageSize = pageSize,
                    pageNumber = pageNumber,
                    preferApiType = Prefs.apiType
                )
                favoriteFolderData.medias.forEach { favoriteItem ->
                    if (favoriteItem.type != FavoriteItemType.Video) return@forEach
                    favorites.add(
                        VideoCardData(
                            avid = favoriteItem.id,
                            title = favoriteItem.title,
                            cover = favoriteItem.cover,
                            upName = favoriteItem.upper.name,
                            time = favoriteItem.duration * 1000L
                        )
                    )
                }
                hasMore = favoriteFolderData.hasMore
                logger.fInfo { "Update favorite items success" }
            }.onFailure {
                logger.fInfo { "Update favorite items failed: ${it.stackTraceToString()}" }
            }.onSuccess {
                pageNumber++
            }
            updatingFolderItems = false
        }
    }

    fun resetPageNumber() {
        pageNumber = 1
        hasMore = true
    }
}