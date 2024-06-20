package vanstudio.tv.biliapi.entity.search


data class Hotword(
    val keyword: String,
    val showName: String,
    val icon: String?,
) {
    companion object {
        fun fromHttpWebHotword(hotword: vanstudio.tv.biliapi.http.entity.search.Hotword) =
            Hotword(
                keyword = hotword.keyword,
                showName = hotword.showName,
                icon = hotword.icon
            )

        fun fromHttpAppSquareDataItem(squareDataItem: vanstudio.tv.biliapi.http.entity.search.AppSearchSquareData.SquareData.SquareDataItem) =
            Hotword(
                keyword = squareDataItem.keyword ?: "",
                showName = squareDataItem.showName ?: "",
                icon = squareDataItem.icon
            )

        fun fromHttpAppSearchTrendingHotword(hotword: vanstudio.tv.biliapi.http.entity.search.SearchTendingData.Hotword) =
            Hotword(
                keyword = hotword.keyword,
                showName = hotword.showName,
                icon = hotword.icon
            )
    }
}