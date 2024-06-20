package vanstudio.tv.beetv.player.factory

import android.content.Context
import vanstudio.tv.beetv.player.AbstractVideoPlayer
import vanstudio.tv.beetv.player.VideoPlayerOptions

abstract class PlayerFactory<T : AbstractVideoPlayer> {
    abstract fun create(context: Context,options: VideoPlayerOptions): T
}