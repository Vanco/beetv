package vanstudio.tv.biliapi.entity.video

data class Tag(
    val id: Int,
    val name: String
) {
    companion object {
        fun fromTag(tag: vanstudio.tv.biliapi.http.entity.video.Tag) = Tag(
            id = tag.tagId,
            name = tag.tagName
        )

        fun fromTag(tag: bilibili.app.view.v1.Tag) = Tag(
            id = tag.id.toInt(),
            name = tag.name
        )

        fun fromTag(tag: vanstudio.tv.biliapi.http.entity.video.VideoDetail.Tag) = Tag(
            id = tag.tagId,
            name = tag.tagName
        )
    }
}
