package vanstudio.tv.biliapi.repositories

import bilibili.app.show.v1.PopularGrpcKt
import bilibili.app.show.v1.popularResultReq
import vanstudio.tv.biliapi.entity.ApiType
import vanstudio.tv.biliapi.entity.home.RecommendData
import vanstudio.tv.biliapi.entity.home.RecommendItem
import vanstudio.tv.biliapi.entity.home.RecommendPage
import vanstudio.tv.biliapi.entity.rank.PopularVideo
import vanstudio.tv.biliapi.entity.rank.PopularVideoData
import vanstudio.tv.biliapi.entity.rank.PopularVideoPage
import vanstudio.tv.biliapi.http.BiliHttpApi

class RecommendVideoRepository(
    private val authRepository: AuthRepository,
    private val channelRepository: ChannelRepository
) {
    private val popularStub
        get() = runCatching {
            PopularGrpcKt.PopularCoroutineStub(channelRepository.defaultChannel!!)
        }.getOrNull()

    suspend fun getPopularVideos(
        page: PopularVideoPage,
        preferApiType: ApiType = ApiType.Web
    ): PopularVideoData {
        return when (preferApiType) {
            ApiType.Web -> {
                val response = BiliHttpApi.getPopularVideoData(
                    pageSize = page.nextWebPageSize,
                    pageNumber = page.nextWebPageNumber,
                    sessData = authRepository.sessionData ?: ""
                ).getResponseData()
                val list = response.list.map { PopularVideo.fromVideoInfo(it) }
                val nextPage = PopularVideoPage(
                    nextWebPageSize = page.nextWebPageSize,
                    nextWebPageNumber = page.nextWebPageNumber + 1
                )
                PopularVideoData(
                    list = list,
                    nextPage = nextPage,
                    noMore = response.noMore
                )
            }

            ApiType.App -> {
                val reply = popularStub?.index(popularResultReq {
                    idx = page.nextAppIndex.toLong()
                })
                val list = reply?.itemsList
                    ?.filter { it.itemCase == bilibili.app.card.v1.Card.ItemCase.SMALL_COVER_V5 }
                    ?.map { PopularVideo.fromSmallCoverV5(it.smallCoverV5) }
                    ?: emptyList()
                val nextPage = PopularVideoPage(
                    nextAppIndex = list.lastOrNull()?.idx ?: -1
                )
                PopularVideoData(
                    list = list,
                    nextPage = nextPage,
                    noMore = nextPage.nextAppIndex == -1
                )
            }
        }
    }

    suspend fun getRecommendVideos(
        page: RecommendPage = RecommendPage(),
        preferApiType: ApiType = ApiType.Web
    ): RecommendData {
        val items = when (preferApiType) {
            ApiType.Web -> BiliHttpApi.getFeedRcmd(
                idx = page.nextWebIdx,
                sessData = authRepository.sessionData
            )
                .getResponseData().item
                .map { RecommendItem.fromRcmdItem(it) }

            ApiType.App -> BiliHttpApi.getFeedIndex(
                idx = page.nextAppIdx,
                accessKey = authRepository.accessToken
            )
                .getResponseData().items
                .filter { it.cardGoto == "av" }
                .map { RecommendItem.fromRcmdItem(it) }
        }
        val nextPage = when (preferApiType) {
            ApiType.Web -> RecommendPage(
                nextWebIdx = page.nextWebIdx + 1
            )

            ApiType.App -> RecommendPage(
                nextAppIdx = items.first().idx + 1
            )
        }
        return RecommendData(
            items = items,
            nextPage = nextPage
        )
    }
}
