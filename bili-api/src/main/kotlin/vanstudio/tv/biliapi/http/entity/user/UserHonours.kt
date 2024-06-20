package vanstudio.tv.biliapi.http.entity.user

import kotlinx.serialization.Serializable

@Serializable
data class UserHonours(
    val mid: Long,
    val colour: Colour? = null,
    val tags: List<String> = emptyList()
) {
    @Serializable
    data class Colour(
        val dark: String,
        val normal: String
    )
}