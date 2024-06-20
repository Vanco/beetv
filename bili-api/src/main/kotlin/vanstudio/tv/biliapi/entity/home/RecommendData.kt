package vanstudio.tv.biliapi.entity.home

data class RecommendData(
    val items: List<RecommendItem>,
    val nextPage: RecommendPage
)

data class RecommendPage(
    val nextWebIdx: Int = 1,
    val nextAppIdx: Int = 0
)
