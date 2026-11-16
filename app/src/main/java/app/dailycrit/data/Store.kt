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
import org.json.JSONArray
import org.json.JSONObject
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
    private val results = mutableStateMapOf<String, String>()
    private val pressedDays = mutableStateMapOf<Long, String>()
    var saved by mutableStateOf(loadSaved())
        private set
    var exams by mutableStateOf(sp.getStringSet("exams", setOf("CEED", "NID", "NIFT")) ?: setOf("CEED", "NID", "NIFT"))
        private set

    init {
        for ((k, v) in sp.all) {
            if (k.startsWith("done:")) {
                val day = k.removePrefix("done:").toLongOrNull() ?: continue
                done[day] = (v as? Set<*>)?.filterIsInstance<String>()?.toSet() ?: emptySet()
            } else if (k.startsWith("res:")) {
                (v as? String)?.let { results[k.removePrefix("res:")] = it }
            } else if (k.startsWith("press:")) {
                val day = k.removePrefix("press:").toLongOrNull() ?: continue
                (v as? String)?.let { pressedDays[day] = it }
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

    fun complete(b: Block, bonus: Int = 0, result: String? = null): Int {
        val d = today()
        if (isDone(d, b)) return 0
        if (result != null) {
            results["$d:${b.id}"] = result
            sp.edit().putString("res:$d:${b.id}", result).apply()
        }
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

    fun saveName(n: String) {
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


    fun result(day: Long, b: Block): String? = results["$day:${b.id}"]

    fun issueNo(): Int = (today() - firstDay + 1).toInt().coerceAtLeast(1)

    fun issues(): List<Pair<Long, String>> = pressedDays.entries.sortedByDescending { it.key }.map { it.key to it.value }

    fun pressedHeadline(day: Long): String? = pressedDays[day]
    fun isPressed(day: Long): Boolean = pressedDays.containsKey(day)

    /** "Send to press": closes today's edition, stores its headline, small XP reward. */
    fun press(headline: String): Int {
        val d = today()
        if (isPressed(d)) return 0
        pressedDays[d] = headline
        sp.edit().putString("press:$d", headline).apply()
        addXp(20)
        return 20
    }

    fun updateExams(set: Set<String>) {
        exams = if (set.isEmpty()) setOf("CEED") else set
        sp.edit().putStringSet("exams", exams).apply()
    }

    fun recordAnswers(b: Block, right: Int, total: Int) {
        val r = sp.getInt("acc_r:${b.id}", 0) + right
        val t = sp.getInt("acc_t:${b.id}", 0) + total
        sp.edit().putInt("acc_r:${b.id}", r).putInt("acc_t:${b.id}", t).apply()
    }

    fun accuracy(b: Block): Pair<Int, Int> = sp.getInt("acc_r:${b.id}", 0) to sp.getInt("acc_t:${b.id}", 0)

    // ---- saved questions (text questions only) ----
    private fun loadSaved(): List<SavedQ> {
        val raw = sp.getString("saved", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            List(arr.length()) {
                val o = arr.getJSONObject(it)
                val opts = o.getJSONArray("o")
                SavedQ(
                    o.getString("id"), o.getString("k"), o.getString("p"),
                    List(opts.length()) { i -> opts.getString(i) },
                    o.getInt("a"), o.getString("e"),
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun persistSaved() {
        val arr = JSONArray()
        for (q in saved) {
            arr.put(
                JSONObject().put("id", q.id).put("k", q.kicker).put("p", q.prompt)
                    .put("o", JSONArray(q.options)).put("a", q.answer).put("e", q.explain),
            )
        }
        sp.edit().putString("saved", arr.toString()).apply()
    }

    fun isSaved(q: Q): Boolean = saved.any { it.id == savedId(q) }

    fun toggleSaved(q: Q): Boolean {
        val id = savedId(q)
        val txt = q.options.mapNotNull { (it as? Opt.Txt)?.s }
        if (txt.size != q.options.size) return false // shape questions can't be saved
        saved = if (saved.any { it.id == id }) {
            saved.filterNot { it.id == id }
        } else {
            saved + SavedQ(id, q.kicker, q.prompt, txt, q.answer, q.explain)
        }
        persistSaved()
        return true
    }

    fun unsave(id: String) {
        saved = saved.filterNot { it.id == id }
        persistSaved()
    }

    private fun savedId(q: Q): String = (q.prompt + "|" + q.options.joinToString { (it as? Opt.Txt)?.s ?: "?" }).hashCode().toString()

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
