package app.dailycrit.data

import kotlin.random.Random

object Maths {

    fun day(level: Int, day: Long): List<Q> {
        val r = Random(day * 13 + 5)
        val kinds = (0..4).toMutableList()
        if (level >= 3) kinds.addAll(listOf(5, 6, 7))
        kinds.shuffle(r)
        return kinds.take(5).map { make(it, r) }
    }

    private fun mcq(
        r: Random,
        prompt: String,
        ans: Int,
        wrongs: List<Int>,
        explain: String,
        pre: String = "",
        suf: String = "",
    ): Q {
        val set = LinkedHashSet<Int>()
        set.add(ans)
        for (w in wrongs) if (w > 0 && w != ans) set.add(w)
        var bump = 1
        while (set.size < 4) {
            set.add(ans + bump * (if (r.nextBoolean()) 1 else 2))
            bump++
        }
        val opts = set.take(4).shuffled(r)
        return Q("MATHS", prompt, opts.map { Opt.Txt("$pre$it$suf") }, opts.indexOf(ans), explain)
    }

    private fun make(kind: Int, r: Random): Q = when (kind) {
        0 -> {
            val p = listOf(10, 15, 20, 25, 30, 40, 50, 60, 75).random(r)
            val n = r.nextInt(2, 16) * 20
            val a = n * p / 100
            mcq(
                r, "What is $p% of $n?", a,
                listOf(n * p / 10, n / p, n * (100 - p) / 100, a + 10),
                "$p% of $n = $n × $p ÷ 100 = $a.",
            )
        }
        1 -> {
            val a = r.nextInt(1, 6)
            var b = r.nextInt(1, 6)
            if (b == a) b = a + 1
            val unit = r.nextInt(4, 16) * 5
            val total = (a + b) * unit
            val big = maxOf(a, b) * unit
            val small = minOf(a, b) * unit
            mcq(
                r, "Share ₹$total between two friends in the ratio $a:$b. How much does the bigger share get?", big,
                listOf(small, total / 2, big + unit, big - unit),
                "Total parts = ${a + b}. One part = $total ÷ ${a + b} = $unit. Bigger share = ${maxOf(a, b)} × $unit = $big.",
                pre = "₹",
            )
        }
        2 -> {
            val avg = r.nextInt(45, 71)
            val xs = List(4) { avg + r.nextInt(-10, 11) }
            val fifth = avg * 5 - xs.sum()
            mcq(
                r, "The average of five numbers is $avg. Four of them are ${xs.joinToString(", ")}. What is the fifth?", fifth,
                listOf(avg, fifth + 5, fifth + 10, xs.sum() / 4),
                "Total of all five = 5 × $avg = ${avg * 5}. The four known numbers add up to ${xs.sum()}. So the fifth = ${avg * 5} − ${xs.sum()} = $fifth.",
            )
        }
        3 -> {
            val s = listOf(30, 40, 45, 50, 60, 72, 80).random(r)
            val t = r.nextInt(2, 7)
            val d = s * t
            mcq(
                r, "A bus covers $d km in $t hours at a steady speed. What is its speed?", s,
                listOf(d * t, s + 10, s - 5, d - t),
                "Speed = distance ÷ time = $d ÷ $t = $s km/h.",
                suf = " km/h",
            )
        }
        4 -> {
            val l = r.nextInt(6, 21)
            val b = r.nextInt(4, l)
            mcq(
                r, "A rectangle is $l cm long and $b cm wide. What is its area?", l * b,
                listOf(2 * (l + b), l + b, l * b + l, l * b - b),
                "Area = length × width = $l × $b = ${l * b} sq cm. (Perimeter would be ${2 * (l + b)} cm, a common mix-up.)",
                suf = " sq cm",
            )
        }
        5 -> {
            val a = r.nextInt(2, 11) * 20
            val pct = listOf(10, 20, 25, 30, 40, 50).random(r)
            val b = a * (100 + pct) / 100
            mcq(
                r, "A price goes from ₹$a to ₹$b. What is the percentage increase?", pct,
                listOf(pct + 5, pct + 10, if (pct > 5) pct - 5 else pct + 15, b - a),
                "Increase = $b − $a = ${b - a}. Percentage = ${b - a} ÷ $a × 100 = $pct%.",
                suf = "%",
            )
        }
        6 -> {
            val cp = r.nextInt(1, 6) * 100
            val p = listOf(10, 20, 25, 30, 40).random(r)
            val sp = cp * (100 + p) / 100
            mcq(
                r, "A shopkeeper buys a bag for ₹$cp and sells it at a $p% profit. What is the selling price?", sp,
                listOf(cp + p, cp * p / 100, sp + cp / 10, cp + 2 * p),
                "Profit = $p% of $cp = ${cp * p / 100}. Selling price = $cp + ${cp * p / 100} = $sp.",
                pre = "₹",
            )
        }
        else -> {
            val pairs = listOf(
                6 to 3, 12 to 6, 10 to 15, 12 to 4, 20 to 5, 30 to 20, 12 to 12, 20 to 30, 9 to 18,
            )
            val (x, y) = pairs.random(r)
            val together = x * y / (x + y)
            mcq(
                r, "A can finish a job in $x days and B can finish it in $y days. Working together, how many days do they take?", together,
                listOf((x + y) / 2, together + 1, together + 2, minOf(x, y)),
                "In one day A does 1/$x and B does 1/$y. Together that is ${x + y}/${x * y} of the job, so they need $x × $y ÷ ${x + y} = $together days.",
                suf = " days",
            )
        }
    }
}

object Puzzles {

    fun day(day: Long): List<Q> {
        val r = Random(day * 17 + 3)
        return listOf(oddOne(r), sequence(r), rotation(r))
    }

    private fun oddOne(r: Random): Q {
        val color = r.nextInt(0, 6)
        val sidesMode = r.nextBoolean()
        val base = r.nextInt(4, 8)
        val odd = if (r.nextBoolean()) base + 1 else base - 1
        val oddIdx = r.nextInt(4)
        val specs = List(4) { i ->
            val rot = r.nextInt(0, 60).toFloat()
            if (i == oddIdx) {
                if (sidesMode) ShapeSpec(Kind.POLY, odd, rot, color, true)
                else ShapeSpec(Kind.POLY, base, rot, color, false)
            } else {
                ShapeSpec(Kind.POLY, base, rot, color, true)
            }
        }
        val explain = if (sidesMode) {
            "Three shapes have $base sides. One has $odd."
        } else {
            "Three shapes are solid. One is only an outline."
        }
        return Q("PUZZLE · ODD ONE OUT", "Which one doesn't belong?", specs.map { Opt.Shp(it) }, oddIdx, explain)
    }

    private fun sequence(r: Random): Q {
        val s0 = r.nextInt(3, 6)
        val c1 = r.nextInt(0, 6)
        val c2 = (c1 + 1 + r.nextInt(0, 5)) % 6
        val cols = listOf(c1, c2)
        fun at(i: Int) = ShapeSpec(Kind.POLY, s0 + (i % 3), 0f, cols[i % 2], true)
        val stem: List<ShapeSpec?> = List(5) { at(it) } + listOf(null)
        val correct = at(5)
        val w1 = ShapeSpec(Kind.POLY, s0 + 1, 0f, c2, true)
        val w2 = ShapeSpec(Kind.POLY, s0 + 2, 0f, c1, true)
        val w3 = ShapeSpec(Kind.POLY, s0, 0f, c1, true)
        val opts = listOf(correct, w1, w2, w3).distinct().shuffled(r)
        return Q(
            "PUZZLE · NEXT IN LINE",
            "Which shape comes next?",
            opts.map { Opt.Shp(it) },
            opts.indexOf(correct),
            "The number of sides goes ${s0}, ${s0 + 1}, ${s0 + 2} and repeats, while the colour alternates. The sixth shape has ${s0 + 2} sides in the second colour.",
            stem = stem,
        )
    }

    private fun norm(a: Float): Float = ((a % 360f) + 360f) % 360f

    private fun rotation(r: Random): Q {
        val color = r.nextInt(0, 6)
        val step = listOf(45f, 90f).random(r)
        val start = r.nextInt(0, 4) * 45f
        val stem: List<ShapeSpec?> = List(4) { ShapeSpec(Kind.ARROW, 0, norm(start + step * it), color, true) } + listOf(null)
        val ansRot = norm(start + step * 4)
        val wrongs = LinkedHashSet<Float>()
        for (c in listOf(step, -step, 180f, 45f, 90f, 135f, -45f, -90f)) {
            val w = norm(ansRot + c)
            if (w != ansRot) wrongs.add(w)
            if (wrongs.size == 3) break
        }
        val all = (listOf(ansRot) + wrongs.toList()).shuffled(r)
        return Q(
            "PUZZLE · TURNING POINT",
            "Where does the arrow point next?",
            all.map { Opt.Shp(ShapeSpec(Kind.ARROW, 0, it, color, true)) },
            all.indexOf(ansRot),
            "The arrow turns ${step.toInt()}° clockwise each time, so the next one has turned another ${step.toInt()}°.",
            stem = stem,
        )
    }
}
