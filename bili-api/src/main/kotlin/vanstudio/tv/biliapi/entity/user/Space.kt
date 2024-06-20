package vanstudio.tv.biliapi.entity.user

data class SpaceVideoData(
    val videos: List<SpaceVideo>,
    val page: SpaceVideoPage
) {
    companion object {
        fun fromWebSpaceVideoData(webSpaceVideoData: vanstudio.tv.biliapi.http.entity.user.WebSpaceVideoData) =
            SpaceVideoData(
                videos = webSpaceVideoData.list?.vlist
                    ?.map { SpaceVideo.fromSpaceVideoItem(it) }
                    ?: emptyList(),
                page = SpaceVideoPage(
                    hasNext = (webSpaceVideoData.page?.count ?: 0)
                            > ((webSpaceVideoData.page?.pageNumber ?: 0)
                            * (webSpaceVideoData.page?.pageSize ?: 0)),
                    nextWebPageSize = webSpaceVideoData.page?.pageSize ?: 0,
                    nextWebPageNumber = (webSpaceVideoData.page?.pageNumber ?: 0) + 1
                )
            )

        fun fromAppSpaceVideoData(appSpaceVideoData: vanstudio.tv.biliapi.http.entity.user.AppSpaceVideoData) =
            SpaceVideoData(
                videos = appSpaceVideoData.item
                    .map { SpaceVideo.fromSpaceVideoItem(it) },
                page = SpaceVideoPage(
                    hasNext = appSpaceVideoData.hasNext,
                    lastAvid = appSpaceVideoData.item.lastOrNull()?.param?.toLong() ?: 0
                )
            )
    }
}

data class SpaceVideo(
    val aid: Long,
    val bvid: String,
    val title: String,
    val cover: String,
    val author: String,
    val duration: Int,
    val play: Int,
    val danmaku: Int
) {
    companion object {
        fun fromSpaceVideoItem(spaceVideoItem: vanstudio.tv.biliapi.http.entity.user.WebSpaceVideoData.SpaceVideoListItem.VListItem) =
            SpaceVideo(
                aid = spaceVideoItem.aid,
                bvid = spaceVideoItem.bvid,
                title = spaceVideoItem.title,
                cover = spaceVideoItem.pic,
                author = spaceVideoItem.author,
                duration = convertMmSsToSeconds(spaceVideoItem.length),
                play = spaceVideoItem.play,
                danmaku = spaceVideoItem.videoReview
            )

        fun fromSpaceVideoItem(spaceVideoItem: vanstudio.tv.biliapi.http.entity.user.AppSpaceVideoData.SpaceVideoItem) =
            SpaceVideo(
                aid = spaceVideoItem.param.toLong(),
                bvid = spaceVideoItem.bvid,
                title = spaceVideoItem.title,
                cover = spaceVideoItem.cover,
                author = spaceVideoItem.author,
                duration = spaceVideoItem.duration,
                play = spaceVideoItem.play,
                danmaku = spaceVideoItem.danmaku
            )
    }
}

private fun convertMmSsToSeconds(time: String): Int {
    val parts = time.split(":")
    val minutes = parts[0].toInt()
    val seconds = parts[1].toInt()
    return (minutes * 60) + seconds
}

enum class SpaceVideoOrder(val value: String) {
    PubDate("pubdate"), Click("click")
}

data class SpaceVideoPage(
    val hasNext: Boolean = true,
    // web
    val nextWebPageSize: Int = 20,
    val nextWebPageNumber: Int = 1,
    // app
    val lastAvid: Long = 0
)