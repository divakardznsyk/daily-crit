package app.dailycrit.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import kotlin.random.Random

/**
 * Daily artworks come from the Art Institute of Chicago public API (public-domain works only).
 * Today's three picks are cached so the quiz still works if you go offline later in the day.
 */
object ArtRepo {
    private const val IIIF = "https://www.artic.edu/iiif/2/"

    private val terms = listOf(
        "impressionism", "poster", "portrait", "landscape", "still life",
        "japanese print", "cubism", "abstract", "woodblock print", "modern art",
    )

    private val famous = listOf(
        "Claude Monet", "Vincent van Gogh", "Pablo Picasso", "Katsushika Hokusai", "Gustav Klimt",
        "Henri Matisse", "Mary Cassatt", "Edward Hopper", "Rembrandt van Rijn", "Johannes Vermeer",
        "Paul Cézanne", "Georges Seurat", "Paul Gauguin", "Alphonse Mucha", "Henri de Toulouse-Lautrec",
        "Edgar Degas", "Utagawa Hiroshige", "Pierre-Auguste Renoir", "Georgia O'Keeffe", "Wassily Kandinsky",
    )

    suspend fun questions(ctx: Context, day: Long): List<Q>? {
        val arts = load(ctx, day) ?: return null
        val r = Random(day * 7 + 1)
        val pool = arts.map { it.artist } + famous
        return arts.map { a ->
            val wrong = pool.filter { it != a.artist }.distinct().shuffled(r).take(3)
            textQ(
                r,
                "ART DESK",
                "Who made this?",
                a.artist,
                wrong,
                explain(a),
                IIIF + a.imageId + "/full/843,/0/default.jpg",
            )
        }
    }

    private fun explain(a: Art): String {
        val date = if (a.date.isNotBlank()) ", ${a.date}" else ""
        val blurb = if (a.blurb.isNotBlank()) " ${a.blurb}" else ""
        return "${a.title}$date. By ${a.artist}.$blurb"
    }

    private suspend fun load(ctx: Context, day: Long): List<Art>? {
        val sp = ctx.getSharedPreferences("crit_art", Context.MODE_PRIVATE)
        if (sp.getLong("day", -1L) == day) {
            val cached = parse(sp.getString("json", null))
            if (cached != null && cached.size >= 3) return cached
        }
        val term = terms[(day % terms.size).toInt()]
        val url = "https://api.artic.edu/api/v1/artworks/search?q=" +
            URLEncoder.encode(term, "UTF-8") +
            "&query%5Bterm%5D%5Bis_public_domain%5D=true" +
            "&fields=id,title,artist_title,date_display,image_id,short_description,is_public_domain" +
            "&limit=60"
        val body = Net.text(url) ?: return null
        val picked = pick(body, day) ?: return null
        sp.edit().putLong("day", day).putString("json", toJson(picked)).apply()
        return picked
    }

    private fun JSONObject.str(k: String): String = if (isNull(k)) "" else optString(k)

    private fun pick(body: String, day: Long): List<Art>? {
        return try {
            val data = JSONObject(body).optJSONArray("data") ?: return null
            val list = ArrayList<Art>()
            for (i in 0 until data.length()) {
                val o = data.getJSONObject(i)
                val image = o.str("image_id")
                val artist = o.str("artist_title")
                val title = o.str("title")
                if (image.isBlank() || artist.isBlank() || title.isBlank()) continue
                if (!o.isNull("is_public_domain") && !o.optBoolean("is_public_domain", true)) continue
                list.add(Art(title, artist, o.str("date_display"), image, clean(o.str("short_description"))))
            }
            val r = Random(day)
            val ordered = list.filter { it.blurb.isNotEmpty() }.shuffled(r) +
                list.filter { it.blurb.isEmpty() }.shuffled(r)
            val out = ArrayList<Art>()
            val seen = HashSet<String>()
            for (a in ordered) {
                if (seen.add(a.artist)) out.add(a)
                if (out.size == 3) break
            }
            if (out.size == 3) out else null
        } catch (e: Exception) {
            null
        }
    }

    private fun clean(s: String): String {
        val t = s.replace(Regex("<[^>]*>"), "")
            .replace("&amp;", "&")
            .replace("&nbsp;", " ")
            .replace("&#39;", "'")
            .replace("&quot;", "\"")
            .trim()
        if (t.length <= 220) return t
        val cut = t.substring(0, 220)
        val dot = cut.lastIndexOf('.')
        return if (dot > 60) cut.substring(0, dot + 1) else cut.trimEnd() + "…"
    }

    private fun toJson(list: List<Art>): String {
        val arr = JSONArray()
        for (a in list) {
            arr.put(
                JSONObject()
                    .put("title", a.title)
                    .put("artist", a.artist)
                    .put("date", a.date)
                    .put("image", a.imageId)
                    .put("blurb", a.blurb),
            )
        }
        return arr.toString()
    }

    private fun parse(json: String?): List<Art>? {
        if (json == null) return null
        return try {
            val arr = JSONArray(json)
            List(arr.length()) {
                val o = arr.getJSONObject(it)
                Art(o.getString("title"), o.getString("artist"), o.getString("date"), o.getString("image"), o.getString("blurb"))
            }
        } catch (e: Exception) {
            null
        }
    }
}
