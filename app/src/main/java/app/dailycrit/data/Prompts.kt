package app.dailycrit.data

import kotlin.random.Random

data class Idea(val tag: String, val text: String)

object Prompts {
    private val things = listOf(
        "a chair", "a bicycle", "a teapot", "a pair of shoes", "a lamp", "a backpack", "a door", "a staircase", "a bottle",
        "a pair of hands", "a bus stop", "a market stall", "a phone charger", "a bird", "a plant on a windowsill", "a clock",
        "a toothbrush", "a cup and saucer", "a kettle", "a street corner", "a train window", "a umbrella", "a camera", "a sofa",
    )
    private val twists = listOf(
        "using only circles", "without lifting your pen", "from three different angles", "in one-point perspective",
        "as if it were made of paper", "with strong side light and dark shadows", "as a tiny poster", "using only straight lines",
        "in 5 minutes, then again in 1", "as a friendly mascot", "from memory, then check and fix", "as it would look in 50 years",
        "made out of food", "upside down", "using only dots and lines for texture", "as if for a children's book",
    )
    private val design = listOf(
        "Design a bottle that tells you what's inside without any label.",
        "Redesign a spoon for someone who is left-handed and always in a hurry.",
        "Invent a bag that opens in a surprising way. Show it closed, half-open and open.",
        "Design a bookmark that does one extra useful thing.",
        "Design a lamp that works without a plug. How does it get its light?",
        "Sketch five ways to carry two eggs safely without a box.",
        "Design a water bottle kids can't lose. Give it three features.",
        "Design a chair for a giant. Show front, side and top.",
        "Design a safer, friendlier handle for a hot pan.",
        "Design a poster for a film called The Last Bus using only 3 colours.",
    )
    private val exam = listOf(
        "CEED-style: draw the same cup from the top, the side and below, then invent a handle.",
        "CEED-style: draw a crowd of 10 people using as few lines per person as possible.",
        "NID-style: make a pattern from one shape. Show three different arrangements.",
        "NID-style: draw a staircase that is hard to climb and one that invites you up.",
        "NIFT-style: design a fabric print using only two motifs from nature.",
        "NIFT-style situation: you have a paper bag, a string and a button. Sketch three things you could make.",
        "NID-style: draw a letter 9 times in 9 different moods.",
        "CEED-style: three objects on a table, one strong light from the side. Draw the shadows first.",
    )

    fun spin(r: Random): List<Idea> = listOf(
        Idea("OBSERVE", "Draw ${things.random(r)}, ${twists.random(r)}."),
        Idea("DESIGN", design.random(r)),
        exam.random(r).let { Idea(it.substringBefore(":").uppercase(), it.substringAfter(": ")) },
    )
}
