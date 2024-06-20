package vanstudio.tv.beetv.entity

import android.content.Context
import vanstudio.tv.beetv.R

enum class VideoAspectRatio(private val strRes: Int) {
    Default(R.string.video_aspect_ratio_default),
    FourToThree(R.string.video_aspect_ratio_four_to_three),
    SixteenToNine(R.string.video_aspect_ratio_sixteen_to_nine);

    fun getDisplayName(context: Context) = context.getString(strRes)
}