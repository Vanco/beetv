package vanstudio.tv.beetv.entity.carddata

import vanstudio.tv.biliapi.http.entity.search.SearchMediaResult

data class SeasonCardData(
    val seasonId: Int,
    val title: String,
    val subTitle: String? = null,
    val cover: String,
    val rating: String? = null,
    val badge: SearchMediaResult.Badge? = null,
)
