package vanstudio.tv.biliapi.entity.user

data class FollowedUser(
    val mid: Long,
    val name: String,
    val avatar: String,
    val sign: String
) {
    companion object {
        fun fromHttpFollowedUser(followedUser: vanstudio.tv.biliapi.http.entity.user.UserFollowData.FollowedUser) =
            FollowedUser(
                mid = followedUser.mid,
                name = followedUser.uname,
                avatar = followedUser.face,
                sign = followedUser.sign
            )
    }
}