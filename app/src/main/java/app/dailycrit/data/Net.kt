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
    private val cache = LruCache<String, ImageBitmap>(8)

    private fun open(url: String): HttpURLConnection {
        val c = URL(url).openConnection() as HttpURLConnection
        c.connectTimeout = 8000
        c.readTimeout = 12000
        c.setRequestProperty("User-Agent", "DailyCrit/1.0 (personal study app)")
        c.setRequestProperty("Accept", "application/json, application/xml, text/xml, image/*, */*")
        return c
    }

    suspend fun text(url: String): String? = withContext(Dispatchers.IO) {
        try {
            val c = open(url)
            if (c.responseCode != 200) {
                c.disconnect()
                null
            } else {
                c.inputStream.bufferedReader().use { it.readText() }
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun bitmap(url: String): ImageBitmap? {
        cache.get(url)?.let { return it }
        return withContext(Dispatchers.IO) {
            try {
                val c = open(url)
                if (c.responseCode != 200) {
                    c.disconnect()
                    null
                } else {
                    val bmp = c.inputStream.use { BitmapFactory.decodeStream(it) }
                    val img = bmp?.asImageBitmap()
                    if (img != null) cache.put(url, img)
                    img
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
