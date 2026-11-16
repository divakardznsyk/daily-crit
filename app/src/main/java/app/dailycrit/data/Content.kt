package app.dailycrit.data

import kotlin.random.Random

class Raw(
    val prompt: String,
    val correct: String,
    val wrongs: List<String>,
    val explain: String,
    val tag: String = "",
)

fun textQ(
    r: Random,
    kicker: String,
    prompt: String,
    correct: String,
    wrongs: List<String>,
    explain: String,
    imageUrl: String? = null,
): Q {
    val all = (listOf(correct) + wrongs.distinct().filter { it != correct }.take(3)).shuffled(r)
    return Q(kicker, prompt, all.map { Opt.Txt(it) }, all.indexOf(correct), explain, imageUrl)
}

object Content {

    val gk = listOf(
        Raw(
            "Who designed the 1972 New York City subway map?",
            "Massimo Vignelli",
            listOf("Milton Glaser", "Paul Rand", "Saul Bass"),
            "Vignelli's 1972 map turned the tangled system into clean, colour-coded lines and 45° bends. Bold geometry, same spirit as the signage system he worked on.",
        ),
        Raw(
            "Who designed the \"I ♥ NY\" logo?",
            "Milton Glaser",
            listOf("Paul Rand", "Massimo Vignelli", "Saul Bass"),
            "Glaser designed it in the 1970s as a simple tourism mark. It became one of the most copied logos ever.",
        ),
        Raw(
            "Who designed the typeface Helvetica (1957)?",
            "Max Miedinger",
            listOf("Paul Renner", "Adrian Frutiger", "Eric Gill"),
            "Miedinger worked with Eduard Hoffmann at the Haas foundry in Switzerland. It was first called Neue Haas Grotesk.",
        ),
        Raw(
            "Futura (1927) is built from circles, triangles and squares. Who drew it?",
            "Paul Renner",
            listOf("Max Miedinger", "Herbert Bayer", "Jan Tschichold"),
            "Renner's Futura shares the geometric thinking of the Bauhaus era, though he was not a Bauhaus teacher.",
        ),
        Raw(
            "Who founded the Bauhaus school in 1919?",
            "Walter Gropius",
            listOf("Le Corbusier", "Mies van der Rohe", "Wassily Kandinsky"),
            "Gropius started it in Weimar, Germany. Mies van der Rohe became its last director.",
        ),
        Raw(
            "Which company is Dieter Rams most associated with?",
            "Braun",
            listOf("Olivetti", "Bang & Olufsen", "Philips"),
            "Rams led design at Braun and wrote the ten principles of good design. One of them: good design is as little design as possible.",
        ),
        Raw(
            "\"Less is more\" is a phrase linked to which architect?",
            "Mies van der Rohe",
            listOf("Frank Lloyd Wright", "Le Corbusier", "Louis Sullivan"),
            "Mies used it to describe reduction: remove everything that doesn't earn its place.",
        ),
        Raw(
            "\"Form follows function\" comes from which architect?",
            "Louis Sullivan",
            listOf("Mies van der Rohe", "Walter Gropius", "Antoni Gaudí"),
            "Sullivan wrote it in 1896 about tall office buildings. Modernist designers adopted it as a creed.",
        ),
        Raw(
            "Which primary colours does printing use (before black is added)?",
            "Cyan, magenta, yellow",
            listOf("Red, green, blue", "Red, yellow, blue", "Black, white, grey"),
            "Screens add light (RGB). Printing subtracts it with CMY, and K (black) is added for deep darks and text.",
        ),
        Raw(
            "On the traditional red-yellow-blue colour wheel, what is blue's complement?",
            "Orange",
            listOf("Green", "Purple", "Yellow"),
            "Complements sit opposite each other. Blue and orange side by side make each other look more intense.",
        ),
        Raw(
            "The golden ratio is roughly equal to…",
            "1.618",
            listOf("1.414", "2.718", "3.142"),
            "Often written as phi. Designers use it as a proportion guide, not a law.",
        ),
        Raw(
            "Which of these is a serif typeface?",
            "Garamond",
            listOf("Helvetica", "Futura", "Gill Sans"),
            "Serifs are the small strokes at the ends of letters. Garamond has them; the other three don't.",
        ),
        Raw(
            "What is kerning?",
            "Adjusting the space between specific pairs of letters",
            listOf("Adjusting the space between lines", "Changing a font's weight", "Aligning text to a grid"),
            "Pairs like AV or To need tightening so the spacing looks even to the eye.",
        ),
        Raw(
            "In typography, leading means…",
            "The space between lines of text",
            listOf("The space between letters", "The thickness of strokes", "The height of capital letters"),
            "The word comes from the strips of lead that printers once put between lines of metal type.",
        ),
        Raw(
            "Which Gestalt principle makes nearby objects look like a group?",
            "Proximity",
            listOf("Closure", "Similarity", "Continuity"),
            "Things placed close together read as related. It's why spacing is such a strong layout tool.",
        ),
        Raw(
            "Which Gestalt idea lets you see a full circle even when part of it is missing?",
            "Closure",
            listOf("Proximity", "Similarity", "Symmetry"),
            "The brain fills in gaps to complete a familiar shape.",
        ),
        Raw(
            "Who designed the Sydney Opera House?",
            "Jørn Utzon",
            listOf("Frank Gehry", "Renzo Piano", "Le Corbusier"),
            "The Danish architect won the competition in 1957 with his sail-like shell design.",
        ),
        Raw(
            "The Eames Lounge Chair (1956) was designed by…",
            "Charles and Ray Eames",
            listOf("Arne Jacobsen", "Verner Panton", "Eero Saarinen"),
            "Moulded plywood and leather, made to feel like a well-worn baseball glove.",
        ),
        Raw(
            "Who designed the Egg chair?",
            "Arne Jacobsen",
            listOf("Hans Wegner", "Eero Saarinen", "Alvar Aalto"),
            "Jacobsen created it in 1958 for the lobby of the Radisson SAS Royal Hotel in Copenhagen.",
        ),
        Raw(
            "Which group, founded in Milan in 1981, is famous for loud, playful furniture?",
            "Memphis Group",
            listOf("De Stijl", "Bauhaus", "Arts and Crafts"),
            "Led by Ettore Sottsass, Memphis broke the rules of good taste on purpose.",
        ),
        Raw(
            "Who designed the striped IBM logo?",
            "Paul Rand",
            listOf("Saul Bass", "Milton Glaser", "Massimo Vignelli"),
            "Rand refined IBM's identity over decades. The striped version came in 1972.",
        ),
        Raw(
            "The Nike Swoosh was designed by a student named…",
            "Carolyn Davidson",
            listOf("Paula Scher", "Susan Kare", "Rob Janoff"),
            "She was paid $35 in 1971. Nike later gave her shares and a gold ring.",
        ),
        Raw(
            "Who designed many of the original Macintosh icons, including the smiling Mac?",
            "Susan Kare",
            listOf("Paula Scher", "Jony Ive", "Rob Janoff"),
            "Kare drew them pixel by pixel on a tiny grid, which is why they still feel so clear.",
        ),
        Raw(
            "\"The Great Wave off Kanagawa\" was made by…",
            "Hokusai",
            listOf("Hiroshige", "Utamaro", "Kuniyoshi"),
            "A woodblock print from around 1831, part of his series Thirty-six Views of Mount Fuji.",
        ),
        Raw(
            "Which artist made the famous Art Nouveau posters for Sarah Bernhardt's plays?",
            "Alphonse Mucha",
            listOf("Toulouse-Lautrec", "Gustav Klimt", "Egon Schiele"),
            "His first Bernhardt poster, Gismonda (1894), made him famous overnight.",
        ),
        Raw(
            "In one-point perspective, parallel lines running away from you meet at…",
            "A single vanishing point",
            listOf("Two vanishing points", "The corner of the picture", "Nowhere, they stay parallel"),
            "Put the vanishing point on the horizon line and everything that recedes aims at it.",
        ),
        Raw(
            "The rule of thirds divides a frame into…",
            "Nine equal parts by two horizontal and two vertical lines",
            listOf("Three horizontal strips only", "Four quarters", "A spiral"),
            "Placing key elements on the lines or their crossings usually feels more dynamic than dead centre.",
        ),
        Raw(
            "Which institute runs the CEED exam?",
            "IIT Bombay (IDC School of Design)",
            listOf("NID Ahmedabad", "IIT Delhi", "NIFT Delhi"),
            "CEED, the Common Entrance Examination for Design, is conducted by IIT Bombay and leads to M.Des and PhD programmes.",
        ),
        Raw(
            "Where is the main campus of the National Institute of Design (NID)?",
            "Ahmedabad",
            listOf("Mumbai", "Pune", "Bengaluru"),
            "NID was founded in Ahmedabad in 1961.",
        ),
        Raw(
            "The 1958 India Report that led to NID's founding was written by…",
            "Charles and Ray Eames",
            listOf("Eero Saarinen and Florence Knoll", "Walter Gropius and Marcel Breuer", "Dieter Rams and Hans Gugelot"),
            "The Eameses were invited to India by the government and recommended a design institute.",
        ),
        Raw(
            "Chandigarh's design is most associated with which architect?",
            "Le Corbusier",
            listOf("Louis Kahn", "Charles Correa", "B. V. Doshi"),
            "Le Corbusier shaped the city and its Capitol Complex from the early 1950s.",
        ),
        Raw(
            "Which of these file types is a vector image format?",
            "SVG",
            listOf("JPEG", "PNG", "GIF"),
            "Vectors store shapes and paths, so they stay sharp at any size.",
        ),
        Raw(
            "WCAG's minimum contrast ratio (level AA) for normal body text is…",
            "4.5 : 1",
            listOf("2 : 1", "3 : 1", "7 : 1"),
            "3:1 is the AA minimum for large text, and 7:1 is the stricter AAA level for normal text.",
        ),
        Raw(
            "\"Above the fold\" originally refers to…",
            "The upper half of a folded newspaper's front page",
            listOf("A web page's footer", "The back cover of a magazine", "A printing error"),
            "Papers sat folded on stands, so the top half had to sell the issue. Websites borrowed the term.",
        ),
        Raw(
            "Mid-century modern design is usually placed roughly in…",
            "The 1940s to the 1960s",
            listOf("The 1900s to the 1920s", "The 1970s to the 1990s", "The 2000s onwards"),
            "Think Eames, Jacobsen, Saarinen: organic curves, new materials, clean lines.",
        ),
        Raw(
            "Who designed the Panton chair, a single-piece moulded plastic cantilever chair?",
            "Verner Panton",
            listOf("Arne Jacobsen", "Charles Eames", "Eero Aarnio"),
            "The Danish designer worked on it for years before it went into production in 1967.",
        ),
        Raw(
            "Who designed the Barcelona chair (1929)?",
            "Ludwig Mies van der Rohe",
            listOf("Marcel Breuer", "Le Corbusier", "Alvar Aalto"),
            "Designed with Lilly Reich for the German pavilion at the Barcelona International Exposition.",
        ),
        Raw(
            "Which colour model do screens use?",
            "RGB",
            listOf("CMYK", "Pantone", "RYB"),
            "Red, green and blue light mix to white on a screen.",
        ),
        Raw(
            "The International Typographic (Swiss) Style is known for…",
            "Grids, sans-serif type and objective photography",
            listOf("Ornate decoration", "Hand-lettered script", "Random collage"),
            "Clarity and order first: strong grids, flush-left text, and few decorations.",
        ),
    )

    val artText = listOf(
        Raw(
            "Who painted \"The Starry Night\" (1889)?",
            "Vincent van Gogh",
            listOf("Claude Monet", "Paul Cézanne", "Edvard Munch"),
            "Painted from memory of the view from his asylum window in Saint-Rémy.",
        ),
        Raw(
            "Who painted \"Girl with a Pearl Earring\"?",
            "Johannes Vermeer",
            listOf("Rembrandt", "Frans Hals", "Jan van Eyck"),
            "A tronie, a character study, not a portrait of a named sitter.",
        ),
        Raw(
            "Which painting gave Impressionism its name?",
            "Impression, Sunrise by Claude Monet",
            listOf("The Starry Night", "Olympia", "A Bar at the Folies-Bergère"),
            "A critic mocked it in 1874 and the label stuck.",
        ),
        Raw(
            "Which two artists pioneered Cubism?",
            "Picasso and Braque",
            listOf("Matisse and Derain", "Dalí and Miró", "Monet and Renoir"),
            "They broke objects into shapes and showed several viewpoints at once.",
        ),
        Raw(
            "Who painted \"The Persistence of Memory\" with its melting clocks?",
            "Salvador Dalí",
            listOf("René Magritte", "Joan Miró", "Max Ernst"),
            "A Surrealist classic from 1931.",
        ),
        Raw(
            "Who painted \"The Kiss\" (1907-08), famous for its gold-leaf patterns?",
            "Gustav Klimt",
            listOf("Egon Schiele", "Edvard Munch", "Henri Matisse"),
            "Klimt's golden period mixes flat decoration with delicate faces and hands.",
        ),
        Raw(
            "\"Nighthawks\" (1942), a late-night diner scene, is by…",
            "Edward Hopper",
            listOf("Norman Rockwell", "Andrew Wyeth", "Grant Wood"),
            "Hopper's lonely light and framing are studied by designers and filmmakers alike.",
        ),
        Raw(
            "\"Guernica\" responds to the bombing of a town in which country?",
            "Spain",
            listOf("Italy", "France", "Portugal"),
            "Picasso painted it in 1937 as a protest. Black, white and grey carry all the emotion.",
        ),
    )

    val prompts = listOf(
        "Draw your left (or non-drawing) hand holding a pen, from where you're sitting. Don't look at the paper for the first minute.",
        "Design a chair for a giant. Draw it three ways: front, side, and from above.",
        "Pick any object near you. Draw it 12 times in 12 minutes, changing the angle every time.",
        "Draw a door that opens to somewhere you'd never expect. What's behind it?",
        "Redesign a spoon for someone who's left-handed and in a hurry. Show two ideas.",
        "Three objects on a table, one strong light from the side. Draw only the shadows first.",
        "Draw a glass of water so it looks wet and cold using only line and tone.",
        "Make a monster using only circles, then turn it into a product mascot.",
        "Draw the same cup from the top, the side, and below. Then draw a handle you've never seen.",
        "Sketch five different ways to carry two eggs safely without a box.",
        "Draw your room corner in one-point perspective. Mark the vanishing point first.",
        "Draw a pair of shoes without lifting your pen once.",
        "Thumbnail 8 poster layouts for a film called \"The Last Bus\". Boxes the size of a matchbox.",
        "Draw a plant near you. Spend 5 minutes on its shape, 5 on its shadow, 5 on its texture.",
        "Invent a tool for a job that doesn't exist yet. Name it and show how it's held.",
        "Draw a crowd of 10 people using as few lines per person as possible.",
        "Pick a letter. Draw it 9 times in 9 different moods.",
        "Draw a bicycle from memory. Then look at one and correct what you got wrong.",
        "Fold a paper, draw the half of a face or object, and mirror it in your mind. Finish the other half freehand.",
        "Make a pattern out of one shape you can repeat. Try three different arrangements.",
        "Draw a bottle that tells you what's inside without any label.",
        "Draw what the sound of rain looks like. No umbrellas or clouds allowed.",
        "Pick any app on your phone. Sketch its home screen as if it were a poster.",
        "Draw your breakfast from above, as if for a magazine spread.",
        "Draw a staircase that's hard to climb and one that invites you up.",
        "Choose a household appliance and give it a friendlier face using shapes only.",
        "Sketch the same stool in three materials: wood, metal, soft foam. Show how the form changes.",
        "Draw a street corner from memory. Add what you remember, then check later what you missed.",
        "Draw a lamp that works without a plug. How does it get its light?",
        "Draw a hand holding five different objects, one after another. Notice how the fingers change.",
        "Draw a bag that opens in a surprising way. Show it closed, half-open and open.",
        "Take a leaf or stone. Draw it large, filling the page, with all its texture.",
        "Design a bookmark that does one extra useful thing.",
        "Draw 6 different faces made from only three marks each.",
        "Pick a word like 'heavy' or 'quiet' and draw an object that is exactly that.",
        "Make a simple map of your day so far using only icons you invent.",
        "Draw a chair from five different sides without moving it, just by imagining it rotating.",
        "Sketch a water bottle for kids that is impossible to lose. Give it three features.",
        "Draw a plate of food, then redraw it as if the plate were the planet.",
        "Draw a market stall and show the three most important things a customer sees first.",
    )

    fun gkQuestions(day: Long, level: Int, exams: Set<String>): List<Q> {
        val r = Random(day * 3 + 1)
        return Banks.pick(level, exams, r, gk).map {
            textQ(r, if (it.tag.isEmpty()) "DESIGN GK" else "DESIGN GK · ${it.tag}", it.prompt, it.correct, it.wrongs, it.explain)
        }
    }

    fun artOffline(day: Long): List<Q> {
        val r = Random(day * 5 + 2)
        return artText.shuffled(r).take(3).map { textQ(r, "ART DESK · OFFLINE", it.prompt, it.correct, it.wrongs, it.explain) }
    }

    fun promptFor(day: Long): String = prompts[(day % prompts.size).toInt()]
}
