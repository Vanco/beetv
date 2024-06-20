package vanstudio.tv.bilisubtitle.entity

data class SubtitleItem(
    val from: Timestamp,
    val to: Timestamp,
    val content: String
) {
    fun isShowing(time: Long) = from.totalMills <= time && to.totalMills >= time
}
