package vanstudio.tv.beetv.entity

import android.content.Context
import vanstudio.tv.biliapi.entity.CodeType
import vanstudio.tv.beetv.R

enum class VideoCodec(private val strRes: Int, val prefix: String) {
    AVC(R.string.video_codec_avc, "avc1"),
    HEVC(R.string.video_codec_hevc, "hev1"),
    AV1(R.string.video_codec_av1, "av01"),
    DVH1(R.string.video_codec_dvh1, "dvh1"),
    HVC1(R.string.video_codec_hvc1, "hvc");

    companion object {
        fun fromCode(code: Int?) = runCatching {
            entries.find { it.ordinal == code }!!
        }.getOrDefault(AVC)

        fun fromCodecString(codec: String) = runCatching {
            entries.forEach {
                if (codec.startsWith(it.prefix)) return@runCatching it
            }
            return@runCatching null
        }.getOrNull()
    }

    fun getDisplayName(context: Context) = context.getString(strRes)

    fun toBiliApiCodeType() = when (this) {
        AVC -> CodeType.Code264
        HEVC -> CodeType.Code265
        AV1 -> CodeType.CodeAv1
        DVH1, HVC1 -> CodeType.NoCode
    }
}