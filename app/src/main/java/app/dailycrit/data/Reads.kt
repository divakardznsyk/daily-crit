package app.dailycrit.data

import android.content.Context
import android.util.Xml
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.json.JSONArray
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader

/**
 * Front page stories: headlines only, from public design feeds. Each card opens the original
 * article in the browser. Cached per day; if the feeds can't be reached the list is just empty
 * and the Classics shelf still works.
 */
object Reads {
    private val feeds = listOf(
        "Dezeen" to "https://www.dezeen.com/feed/",
        "Designboom" to "https://www.designboom.com/feed/",
        "Eye on Design" to "https://eyeondesign.aiga.org/feed/",
    )

    suspend fun today(ctx: Context, day: Long): List<Story> {
        val sp = ctx.getSharedPreferences("crit_reads", Context.MODE_PRIVATE)
        if (sp.getLong("day", -1L) == day) {
            val cached = parse(sp.getString("json", null))
            if (!cached.isNullOrEmpty()) return cached
        }
        val fetched = coroutineScope {
            feeds.map { (source, url) ->
                async { Net.text(url)?.let { xml -> parseFeed(xml, source, 3) } ?: emptyList() }
            }.map { it.await() }
        }
        // interleave so every source shows up near the top
        val merged = ArrayList<Story>()
        val longest = fetched.maxOfOrNull { it.size } ?: 0
        for (i in 0 until longest) {
            for (list in fetched) if (i < list.size) merged.add(list[i])
        }
        if (merged.isNotEmpty()) {
            sp.edit().putLong("day", day).putString("json", toJson(merged)).apply()
        }
        return merged
    }

    private fun parseFeed(xml: String, source: String, max: Int): List<Story> {
        val out = ArrayList<Story>()
        try {
            val p = Xml.newPullParser()
            p.setInput(StringReader(xml))
            var ev = p.eventType
            var inItem = false
            var title = ""
            var link = ""
            while (ev != XmlPullParser.END_DOCUMENT && out.size < max) {
                if (ev == XmlPullParser.START_TAG) {
                    val tag = p.name
                    if (tag == "item") {
                        inItem = true
                        title = ""
                        link = ""
                    } else if (inItem && tag == "title") {
                        title = p.nextText().trim()
                    } else if (inItem && tag == "link") {
                        link = p.nextText().trim()
                    }
                } else if (ev == XmlPullParser.END_TAG && p.name == "item") {
                    if (title.isNotEmpty() && link.startsWith("http")) out.add(Story(title, link, source))
                    inItem = false
                }
                ev = p.next()
            }
        } catch (e: Exception) {
            // return whatever was parsed
        }
        return out
    }

    private fun toJson(list: List<Story>): String {
        val arr = JSONArray()
        for (s in list) arr.put(JSONObject().put("t", s.title).put("u", s.url).put("s", s.source))
        return arr.toString()
    }

    private fun parse(json: String?): List<Story>? {
        if (json == null) return null
        return try {
            val arr = JSONArray(json)
            List(arr.length()) {
                val o = arr.getJSONObject(it)
                Story(o.getString("t"), o.getString("u"), o.getString("s"))
            }
        } catch (e: Exception) {
            null
        }
    }
}
