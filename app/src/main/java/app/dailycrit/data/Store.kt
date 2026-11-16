package app.dailycrit.data

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate

const val PREFS = "crit"
const val DEFAULT_MOCK = "https://www.ceed.iitb.ac.in/"
const val QUALIFY = 4
const val ALL_BONUS = 50
const val XP_PER_LEVEL = 200

val LEVEL_TITLES = listOf(
    "Doodler", "Sketcher", "Observer", "Composer", "Art Director", "Creative Director", "Legend",
)

class Store(ctx: Context) {
    private val appCtx: Context = ctx.applicationContext
    private val sp = appCtx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    var name by mutableStateOf(sp.getString("name", "") ?: "")
        private set
    var xp by mutableIntStateOf(sp.getInt("xp", 0))
        private set
    var mockUrl by mutableStateOf(sp.getString("mock", DEFAULT_MOCK) ?: DEFAULT_MOCK)
        private set
    var remindOn by mutableStateOf(sp.getBoolean("remind_on", false))
        private set
    var remindH by mutableIntStateOf(sp.getInt("remind_h", 23))
        private set
    var remindM by mutableIntStateOf(sp.getInt("remind_m", 0))
        private set
    var lastReset by mutableLongStateOf(sp.getLong("last_reset", -1L))
        private set
    var pyqTotal by mutableIntStateOf(sp.getInt("pyq_total", 0))
        private set
    var pyqRight by mutableIntStateOf(sp.getInt("pyq_right", 0))
        private set

    val firstDay: Long = run {
        val saved = sp.getLong("first_day", -1L)
        if (saved >= 0) saved else today().also { sp.edit().putLong("first_day", it).apply() }
    }

    private val done = mutableStateMapOf<Long, Set<String>>()
    private val photos = mutableStateMapOf<Long, String>()

    init {
        for ((k, v) in sp.all) {
            if (k.startsWith("done:")) {
                val day = k.removePrefix("done:").toLongOrNull() ?: continue
                done[day] = (v as? Set<*>)?.filterIsInstance<String>()?.toSet() ?: emptySet()
            } else if (k.startsWith("photo:")) {
                val day = k.removePrefix("photo:").toLongOrNull() ?: continue
                (v as? String)?.let { photos[day] = it }
            }
        }
    }

    val level: Int get() = 1 + xp / XP_PER_LEVEL
    val xpInLevel: Int get() = xp % XP_PER_LEVEL
    val levelTitle: String get() = LEVEL_TITLES[(level - 1).coerceAtMost(LEVEL_TITLES.size - 1)]

    fun isDone(day: Long, b: Block): Boolean = done[day]?.contains(b.id) == true
    fun doneCount(day: Long): Int = done[day]?.size ?: 0

    fun complete(b: Block, bonus: Int = 0): Int {
        val d = today()
        if (isDone(d, b)) return 0
        var gain = b.xp + bonus
        val set = (done[d] ?: emptySet()) + b.id
        done[d] = set
        sp.edit().putStringSet("done:$d", set).apply()
        if (set.size == Block.values().size) gain += ALL_BONUS
        addXp(gain)
        return gain
    }

    fun xpPreview(b: Block, bonus: Int): Int {
        val d = today()
        if (isDone(d, b)) return 0
        var gain = b.xp + bonus
        if (doneCount(d) + 1 == Block.values().size) gain += ALL_BONUS
        return gain
    }

    private fun addXp(n: Int) {
        xp += n
        sp.edit().putInt("xp", xp).apply()
    }

    fun streak(): Int {
        var d = today()
        if (doneCount(d) < QUALIFY) d -= 1
        var n = 0
        while (doneCount(d) >= QUALIFY) {
            n++
            d--
        }
        return n
    }

    fun daysQualified(): Int = done.count { it.value.size >= QUALIFY }
    fun blocksTotal(): Int = done.values.sumOf { it.size }

    fun setName(n: String) {
        name = n.trim()
        sp.edit().putString("name", name).apply()
    }

    fun setMock(u: String) {
        mockUrl = u.trim().ifEmpty { DEFAULT_MOCK }
        sp.edit().putString("mock", mockUrl).apply()
    }

    fun setRemind(on: Boolean, h: Int, m: Int) {
        remindOn = on
        remindH = h
        remindM = m
        sp.edit().putBoolean("remind_on", on).putInt("remind_h", h).putInt("remind_m", m).apply()
    }

    fun markReset(): Int {
        val d = today()
        if (lastReset == d) return 0
        lastReset = d
        sp.edit().putLong("last_reset", d).apply()
        addXp(15)
        return 15
    }

    fun logPyq(result: Int): Int {
        // result: 0 = missed, 1 = got it, 2 = skipped
        if (result != 2) {
            pyqTotal += 1
            if (result == 1) pyqRight += 1
            sp.edit().putInt("pyq_total", pyqTotal).putInt("pyq_right", pyqRight).apply()
        }
        return when (result) {
            1 -> 10
            0 -> 5
            else -> 0
        }
    }

    fun photoDays(): List<Long> = photos.keys.sortedDescending()

    fun photoFile(day: Long): File? {
        val n = photos[day] ?: return null
        val f = File(File(appCtx.filesDir, "studio"), n)
        return if (f.exists()) f else null
    }

    suspend fun savePhoto(uri: Uri) = withContext(Dispatchers.IO) {
        try {
            val d = today()
            val dir = File(appCtx.filesDir, "studio").also { it.mkdirs() }
            val name = "$d-${System.currentTimeMillis()}.jpg"
            appCtx.contentResolver.openInputStream(uri)?.use { input ->
                File(dir, name).outputStream().use { out -> input.copyTo(out) }
            }
            sp.edit().putString("photo:$d", name).apply()
            withContext(Dispatchers.Main) { photos[d] = name }
        } catch (e: Exception) {
            // ignore, photo is optional
        }
    }

    companion object {
        fun today(): Long = LocalDate.now().toEpochDay()
    }
}
