package vanstudio.tv.beetv.util

import android.content.Context
import vanstudio.tv.beetv.activities.video.RemoteControllerPanelDemoActivity
import vanstudio.tv.beetv.activities.video.VideoPlayerActivity
import vanstudio.tv.beetv.activities.video.VideoPlayerV3Activity
import vanstudio.tv.beetv.entity.proxy.ProxyArea

fun launchPlayerActivity(
    context: Context,
    avid: Long,
    cid: Long,
    title: String,
    partTitle: String,
    played: Int,
    fromSeason: Boolean,
    subType: Int? = null,
    epid: Int? = null,
    seasonId: Int? = null,
    isVerticalVideo: Boolean = false,
    proxyArea: ProxyArea = ProxyArea.MainLand,
    playerIconIdle: String = "",
    playerIconMoving: String = ""
) {
    if (Prefs.useOldPlayer) {
        VideoPlayerActivity.actionStart(
            context, avid, cid, title, partTitle, played, fromSeason, subType, epid, seasonId
        )
    } else {
        if (Prefs.showedRemoteControllerPanelDemo) {
            VideoPlayerV3Activity.actionStart(
                context, avid, cid, title, partTitle, played, fromSeason, subType, epid, seasonId,
                isVerticalVideo, proxyArea, playerIconIdle, playerIconMoving
            )
        } else {
            RemoteControllerPanelDemoActivity.actionStart(
                context, avid, cid, title, partTitle, played, fromSeason, subType, epid, seasonId,
                isVerticalVideo, proxyArea, playerIconIdle, playerIconMoving
            )
        }
    }
}