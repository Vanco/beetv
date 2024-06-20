package vanstudio.tv.beetv.player.impl.exo

import android.content.Context
import vanstudio.tv.beetv.player.VideoPlayerOptions
import vanstudio.tv.beetv.player.factory.PlayerFactory

class ExoPlayerFactory : PlayerFactory<ExoMediaPlayer>() {
    override fun create(context: Context, options: VideoPlayerOptions): ExoMediaPlayer {
        return ExoMediaPlayer(context, options)
    }
}