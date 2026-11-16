package app.dailycrit.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import kotlin.random.Random

private fun wiki(file: String) = "https://commons.wikimedia.org/wiki/Special:FilePath/" + Uri.encode(file) + "?width=900"

private val curated = listOf(
    Art("The Great Wave off Kanagawa", "Katsushika Hokusai", "c. 1831", wiki("Tsunami_by_hokusai_19th_century.jpg"), "A woodblock print from Thirty-six Views of Mount Fuji. Tiny Fuji sits in the gap under the wave."),
    Art("The Starry Night", "Vincent van Gogh", "1889", wiki("Van_Gogh_-_Starry_Night_-_Google_Art_Project.jpg"), "Painted from memory of the view from his asylum window. The sky is built from thick directional strokes."),
    Art("Girl with a Pearl Earring", "Johannes Vermeer", "c. 1665", wiki("Meisje_met_de_parel.jpg"), "A tronie, a character study. The dark background makes the light on her face pop."),
    Art("Impression, Sunrise", "Claude Monet", "1872", wiki("Claude_Monet,_Impression,_soleil_levant.jpg"), "A critic mocked its title in 1874 and the name Impressionism was born."),
    Art("The Kiss", "Gustav Klimt", "1907-08", wiki("The_Kiss_-_Gustav_Klimt_-_Google_Cultural_Institute.jpg"), "Flat gold-leaf pattern around softly painted faces and hands."),
    Art("A Sunday on La Grande Jatte", "Georges Seurat", "1884-86", wiki("Georges_Seurat_-_A_Sunday_on_La_Grande_Jatte_--_1884_-_Google_Art_Project.jpg"), "Built from tiny dots of pure colour that your eye mixes. This is pointillism."),
    Art("Mona Lisa", "Leonardo da Vinci", "c. 1503-06", wiki("Mona_Lisa,_by_Leonardo_da_Vinci,_from_C2RMF_retouched.jpg"), "Famous for sfumato: soft, smoky transitions with no hard outlines."),
    Art("The Birth of Venus", "Sandro Botticelli", "c. 1485", wiki("Sandro_Botticelli_-_La_nascita_di_Venere_-_Google_Art_Project_-_edited.jpg"), "Flowing line matters more than realistic depth here."),
    Art("Sunflowers", "Vincent van Gogh", "1889", wiki("Vincent_van_Gogh_-_Sunflowers_-_VGM_F458.jpg"), "Almost all yellow, yet never flat: many shades and thick paint."),
    Art("The Arnolfini Portrait", "Jan van Eyck", "1434", wiki("Van_Eyck_-_Arnolfini_Portrait.jpg"), "The round mirror at the back shows the whole room."),
    Art("The Night Watch", "Rembrandt van Rijn", "1642", wiki("The_Night_Watch_-_HD.jpg"), "Drama and light turn a group portrait into a scene."),
    Art("Water Lilies", "Claude Monet", "1906", wiki("Claude_Monet_-_Water_Lilies_-_1906,_Ryerson.jpg"), "From a long series painted in his garden at Giverny."),
    Art("Bal du moulin de la Galette", "Pierre-Auguste Renoir", "1876", wiki("Pierre-Auguste_Renoir,_Le_Moulin_de_la_Galette.jpg"), "Dappled sunlight painted as spots of colour on the dancers."),
    Art("Liberty Leading the People", "Eugene Delacroix", "1830", wiki("Eugène_Delacroix_-_Le_28_Juillet._La_Liberté_guidant_le_peuple.jpg"), "A triangle composition with the flag at the top pulls the eye up."),
    Art("Wanderer above the Sea of Fog", "Caspar David Friedrich", "c. 1818", wiki("Caspar_David_Friedrich_-_Wanderer_above_the_sea_of_fog.jpg"), "A figure seen from behind invites you to stand in his place."),
    Art("The Death of Marat", "Jacques-Louis David", "1793", wiki("Death_of_Marat_by_David.jpg"), "Calm empty space above the figure makes the moment heavy and quiet."),
    Art("The Creation of Adam", "Michelangelo", "c. 1512", wiki("Michelangelo,_Creation_of_Adam_(cropped).jpg"), "The small gap between the two fingers is the most famous space in painting."),
    Art("View of Delft", "Johannes Vermeer", "c. 1660-61", wiki("Vermeer-view-of-delft.jpg"), "Calm light on a town, with a huge cloudy sky taking most of the canvas."),
    Art("The Milkmaid", "Johannes Vermeer", "c. 1658", wiki("Johannes_Vermeer_-_Het_melkmeisje_-_Google_Art_Project.jpg"), "An everyday task painted with window light and total calm."),
    Art("Composition VII", "Wassily Kandinsky", "1913", wiki("Wassily_Kandinsky,_1913_-_Composition_7.jpg"), "No objects at all: the rhythm of colour and line is the subject."),
    Art("Composition II in Red, Blue, and Yellow", "Piet Mondrian", "1930", wiki("Piet_Mondriaan,_1930_-_Mondrian_Composition_II_in_Red,_Blue,_and_Yellow.jpg"), "Primary colours, black lines and white. A masterclass in grid layout."),
    Art("Woman with a Hat", "Henri Matisse", "1905", wiki("Matisse-Woman-with-a-Hat.jpg"), "Fauvism: wild, non-natural colour on a face."),
    Art("The Garden of Earthly Delights", "Hieronymus Bosch", "c. 1490-1510", wiki("The_Garden_of_earthly_delights.jpg"), "A triptych crowded with tiny strange scenes. Zoom in for details."),
    Art("The Tower of Babel", "Pieter Bruegel the Elder", "1563", wiki("Pieter_Bruegel_the_Elder_-_The_Tower_of_Babel_(Vienna)_-_Google_Art_Project_-_edited.jpg"), "Tiny people show the impossible scale of the tower."),
)

private val famous = listOf(
    "Claude Monet", "Vincent van Gogh", "Pablo Picasso", "Katsushika Hokusai", "Gustav Klimt", "Henri Matisse",
    "Mary Cassatt", "Edward Hopper", "Rembrandt van Rijn", "Johannes Vermeer", "Paul Cezanne", "Georges Seurat",
    "Paul Gauguin", "Alphonse Mucha", "Edgar Degas", "Utagawa Hiroshige", "Georgia O'Keeffe", "Edvard Munch",
    "Francisco Goya", "Raja Ravi Varma", "Amrita Sher-Gil", "Egon Schiele", "Joan Miro", "Salvador Dali",
)

/** Today's paintings: curated public-domain works first, Art Institute of Chicago as a bonus. Only images that load are kept. */
object ArtRepo {
    var note: String = ""

    suspend fun questions(ctx: Context, day: Long, level: Int): List<Q>? {
        val arts = today(ctx, day) ?: return null
        val r = Random(day * 7 + 1)
        val pool = (curated.map { it.artist } + famous + arts.map { it.artist }).distinct()
        return arts.map { a ->
            val wrong = pool.filter { it != a.artist }.shuffled(r).take(3)
            textQ(r, "ART DESK", "Who made this?", a.artist, wrong, "${a.title}, ${a.date}. By ${a.artist}. ${a.blurb}", a.url)
        }
    }

    suspend fun today(ctx: Context, day: Long): List<Art>? {
        val sp = ctx.getSharedPreferences("crit_art2", Context.MODE_PRIVATE)
        if (sp.getLong("day", -1L) == day) parse(sp.getString("json", null))?.let { if (it.size >= 3) return it }
        val r = Random(day)
        val cands = curated.shuffled(r).take(9) + chicago(day).take(4)
        val ok = coroutineScope { cands.map { a -> async { if (Net.bitmap(a.url) != null) a else null } }.map { it.await() } }
            .filterNotNull().distinctBy { it.artist }.take(3)
        note = Net.err
        if (ok.size < 3) return null
        sp.edit().putLong("day", day).putString("json", toJson(ok)).apply()
        return ok
    }

    private suspend fun chicago(day: Long): List<Art> {
        val url = "https://api.artic.edu/api/v1/artworks/search?q=" + URLEncoder.encode(listOf("impressionism", "poster", "japanese print", "portrait", "landscape")[(day % 5).toInt()], "UTF-8") +
            "&query%5Bterm%5D%5Bis_public_domain%5D=true&fields=title,artist_title,date_display,image_id,short_description&limit=30"
        val body = Net.text(url) ?: return emptyList()
        return try {
            val data = JSONObject(body).getJSONArray("data")
            (0 until data.length()).mapNotNull {
                val o = data.getJSONObject(it)
                val img = o.optString("image_id", "")
                val artist = o.optString("artist_title", "")
                if (img.isBlank() || artist.isBlank() || o.isNull("image_id")) null
                else Art(o.optString("title"), artist.substringBefore("\n"), o.optString("date_display"), "https://www.artic.edu/iiif/2/$img/full/843,/0/default.jpg", "")
            }.shuffled(Random(day))
        } catch (e: Exception) { emptyList() }
    }

    private fun toJson(l: List<Art>) = JSONArray().also { arr ->
        l.forEach { arr.put(JSONObject().put("t", it.title).put("a", it.artist).put("d", it.date).put("u", it.url).put("b", it.blurb)) }
    }.toString()

    private fun parse(j: String?): List<Art>? = try {
        val arr = JSONArray(j ?: return null)
        List(arr.length()) { val o = arr.getJSONObject(it); Art(o.getString("t"), o.getString("a"), o.getString("d"), o.getString("u"), o.getString("b")) }
    } catch (e: Exception) { null }
}
