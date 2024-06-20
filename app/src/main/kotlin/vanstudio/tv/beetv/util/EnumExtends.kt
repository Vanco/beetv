package vanstudio.tv.beetv.util

import android.content.Context
import vanstudio.tv.biliapi.entity.season.FollowingSeasonStatus
import vanstudio.tv.biliapi.entity.season.FollowingSeasonType
import vanstudio.tv.beetv.R

fun FollowingSeasonStatus.getDisplayName(context: Context) = when (this) {
    FollowingSeasonStatus.All -> context.getString(R.string.following_season_status_all)
    FollowingSeasonStatus.Want -> context.getString(R.string.following_season_status_want)
    FollowingSeasonStatus.Watching -> context.getString(R.string.following_season_status_watching)
    FollowingSeasonStatus.Watched -> context.getString(R.string.following_season_status_watched)
}

fun FollowingSeasonType.getDisplayName(context: Context) = when (this) {
    FollowingSeasonType.Bangumi -> context.getString(R.string.following_season_type_bangumi)
    FollowingSeasonType.Cinema -> context.getString(R.string.following_season_type_film_and_television)
}