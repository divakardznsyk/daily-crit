package app.dailycrit.data

import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object Net {
    private val cache = LruCache<String, ImageBitmap>(12)

    /** Last failure, shown in the Art desk so problems can be diagnosed from a screenshot. */
    @Volatile
    var err: String = ""

    private fun open(url: String): HttpURLConnection {
        val c = URL(url).openConnection() as HttpURLConnection
        c.connectTimeout = 10000
        c.readTimeout = 20000
        c.instanceFollowRedirects = true
        c.setRequestProperty(
            "User-Agent",
            "DailyCrit/1.0 (personal study app; https://github.com/divakardznsyk/daily-crit)",
        )
        c.setRequestProperty("Accept", "application/json, application/xml, text/xml, image/*, */*")
        return c
    }

    private fun host(url: String): String = try { URL(url).host } catch (e: Exception) { url.take(30) }

    suspend fun text(url: String): String? = withContext(Dispatchers.IO) {
        try {
            val c = open(url)
            val code = c.responseCode
            if (code != 200) {
                err = "${host(url)}: HTTP $code"
                c.disconnect()
                null
            } else {
                c.inputStream.bufferedReader().use { it.readText() }
            }
        } catch (e: Exception) {
            err = "${host(url)}: ${e.javaClass.simpleName}"
            null
        }
    }

    suspend fun bitmap(url: String): ImageBitmap? {
        cache.get(url)?.let { return it }
        return withContext(Dispatchers.IO) {
            try {
                val c = open(url)
                val code = c.responseCode
                if (code != 200) {
                    err = "${host(url)}: HTTP $code"
                    c.disconnect()
                    null
                } else {
                    val bmp = c.inputStream.use { BitmapFactory.decodeStream(it) }
                    val img = bmp?.asImageBitmap()
                    if (img != null) cache.put(url, img) else err = "${host(url)}: not an image"
                    img
                }
            } catch (e: Exception) {
                err = "${host(url)}: ${e.javaClass.simpleName}"
                null
            }
        }
    }
}
