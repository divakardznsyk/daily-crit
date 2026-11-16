package app.dailycrit.data

enum class Block(
    val id: String,
    val title: String,
    val kicker: String,
    val blurb: String,
    val mins: Int,
    val xp: Int,
) {
    READ("read", "Front page", "SECTION A", "One story. One thing noticed.", 5, 20),
    GK("gk", "Crit cards", "SECTION B", "Four design GK cards", 4, 20),
    ART("art", "Art desk", "SECTION C", "Who made this?", 5, 25),
    PUZZLE("puzzle", "Puzzle page", "SECTION D", "Eyes and logic", 5, 25),
    MATHS("maths", "Maths corner", "SECTION E", "Five quick ones", 8, 30),
    STUDIO("studio", "Studio", "SECTION F", "Draw, then judge yourself", 15, 40),
    PYQ("pyq", "PYQ desk", "SECTION G", "One real past question", 8, 30),
}

sealed interface Opt {
    data class Txt(val s: String) : Opt
    data class Shp(val spec: ShapeSpec) : Opt
}

enum class Kind { POLY, STAR, ARROW, CIRCLE }

data class ShapeSpec(
    val kind: Kind,
    val sides: Int = 0,
    val rot: Float = 0f,
    val color: Int = 0,
    val filled: Boolean = true,
)

data class Q(
    val kicker: String,
    val prompt: String,
    val options: List<Opt>,
    val answer: Int,
    val explain: String,
    val imageUrl: String? = null,
    val stem: List<ShapeSpec?> = emptyList(),
)

data class Story(val title: String, val url: String, val source: String)

data class Art(
    val title: String,
    val artist: String,
    val date: String,
    val imageId: String,
    val blurb: String,
)
