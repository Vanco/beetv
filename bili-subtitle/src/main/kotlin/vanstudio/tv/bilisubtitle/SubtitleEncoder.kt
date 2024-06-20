package vanstudio.tv.bilisubtitle

import vanstudio.tv.bilisubtitle.entity.BiliSubtitle
import vanstudio.tv.bilisubtitle.entity.BiliSubtitleItem
import vanstudio.tv.bilisubtitle.entity.SrtSubtitleItem
import vanstudio.tv.bilisubtitle.entity.SubtitleItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object SubtitleEncoder {
    fun encodeToBcc(subtitles: List<SubtitleItem>): String {
        val bccSubtitles = mutableListOf<BiliSubtitleItem>()
        subtitles.forEach {
            bccSubtitles.add(
                BiliSubtitleItem(
                    from = it.from.getBccTime(),
                    to = it.to.getBccTime(),
                    location = 2,
                    content = it.content
                )
            )
        }
        val bccSubtitle = BiliSubtitle(
            fontSize = 0.4f,
            fontColor = "#FFFFFF",
            backgroundAlpha = 0.5f,
            backgroundColor = "#9C27B0",
            stroke = "none",
            body = bccSubtitles
        )
        return Json.encodeToString(bccSubtitle)
    }

    fun encodeToSrt(subtitles: List<SubtitleItem>): String {
        var result = ""
        subtitles.forEachIndexed { index, data ->
            val srtSubtitleItem = SrtSubtitleItem(
                index = index + 1,
                from = data.from.getSrtTime(),
                to = data.to.getSrtTime(),
                content = data.content.replace("\n","\\n")
            )
            if (index != 0) result += "\n"
            result += srtSubtitleItem.toRaw()
        }
        return result
    }
}