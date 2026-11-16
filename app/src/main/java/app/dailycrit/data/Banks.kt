package app.dailycrit.data

private fun r(p: String, c: String, a: String, b: String, d: String, e: String, tag: String = "") = Raw(p, c, listOf(a, b, d), e, tag)

object Banks {
    val mid = listOf(
        r("Frutiger (the typeface) was first made for signage at which place?", "Charles de Gaulle Airport", "Heathrow Airport", "Schiphol Airport", "Zurich Airport", "Adrian Frutiger designed it in the 1970s for Paris's new airport so signs stay legible from far away and at an angle."),
        r("Who designed the typeface Gill Sans?", "Eric Gill", "Edward Johnston", "Paul Renner", "Max Miedinger", "Gill was a student of Edward Johnston, who made the London Underground typeface."),
        r("Whose 1931 diagram redesigned the London Underground map?", "Harry Beck", "Edward Johnston", "Massimo Vignelli", "Frank Pick", "Beck dropped geography and kept only lines, stations and 45° angles."),
        r("Which art movement is linked to Mondrian and Theo van Doesburg?", "De Stijl", "Dada", "Art Deco", "Constructivism", "De Stijl (The Style) reduced art to primary colours, black lines and straight geometry."),
        r("Constructivism, with its bold diagonals and red-black posters, began in…", "Russia", "France", "Italy", "Netherlands", "Artists like El Lissitzky and Rodchenko made it after the 1917 revolution."),
        r("Who designed the Glasgow School of Art building?", "Charles Rennie Mackintosh", "Antoni Gaudi", "Victor Horta", "Louis Sullivan", "Mackintosh mixed Art Nouveau curves with strong straight lines."),
        r("Casa Batllo in Barcelona was designed by…", "Antoni Gaudi", "Le Corbusier", "Jorn Utzon", "Gustave Eiffel", "Gaudi avoided straight lines and borrowed forms from nature."),
        r("The Guggenheim Museum Bilbao (1997) was designed by…", "Frank Gehry", "Zaha Hadid", "Renzo Piano", "Norman Foster", "Its titanium curves made it a symbol of 'Bilbao effect' architecture."),
        r("Fallingwater, the house built over a waterfall, is by…", "Frank Lloyd Wright", "Mies van der Rohe", "Le Corbusier", "Louis Kahn", "Built 1935-37 in Pennsylvania as a weekend home."),
        r("In which year did the Bauhaus move from Weimar to Dessau?", "1925", "1919", "1930", "1933", "In Dessau it got Gropius's iconic glass workshop building."),
        r("A colour scheme using three colours evenly spaced around the wheel is…", "Triadic", "Analogous", "Complementary", "Monochromatic", "Like red, yellow and blue."),
        r("Analogous colours are…", "Neighbours on the colour wheel", "Opposite on the wheel", "Only black, white and grey", "Always warm colours", "Blue, blue-green and green are analogous. They feel calm and harmonious."),
        r("In typography, the x-height is…", "The height of a lowercase x", "The height of capital letters", "The gap between lines", "The width of the stem", "A large x-height makes text look bigger and more readable at small sizes."),
        r("Which of these is a monospaced typeface?", "Courier", "Garamond", "Futura", "Baskerville", "Every letter in a monospaced font takes the same width, like on a typewriter."),
        r("Baskerville, a refined transitional serif, was designed in the 1750s by…", "John Baskerville", "Claude Garamond", "Giambattista Bodoni", "William Caslon", "Baskerville was also a printer in Birmingham."),
        r("Didot and Bodoni typefaces are known for…", "Extreme contrast between thick and thin strokes", "Rounded monoline shapes", "Handwritten slant", "Very wide letter spacing", "That high contrast gives them the look of fashion magazines."),
        r("Who introduced movable type printing to Europe around 1450?", "Johannes Gutenberg", "William Caxton", "Aldus Manutius", "Nicolas Jenson", "His Gutenberg Bible is the most famous early printed book."),
        r("In print design, 'bleed' means…", "Artwork that extends past the trim edge", "Ink running on wet paper", "Colours fading to white", "A fold line", "It stops thin white edges appearing when pages are cut."),
        r("The Japanese idea of finding beauty in imperfection is…", "Wabi-sabi", "Kintsugi", "Ikebana", "Origami", "Cracks, age and asymmetry are valued rather than hidden."),
        r("The Anglepoise lamp (1932) was designed by…", "George Carwardine", "Dieter Rams", "Richard Sapper", "Achille Castiglioni", "Its spring-balanced arm stays where you leave it."),
        r("The red 'Valentine' typewriter (1969) was designed by…", "Ettore Sottsass", "Marcello Nizzoli", "Mario Bellini", "Dieter Rams", "Made for Olivetti as a playful, portable, pop-colour object."),
        r("Anni Albers is best known for her work in…", "Textiles and weaving", "Furniture", "Poster design", "Architecture", "She studied and later taught at the Bauhaus."),
        r("A 'tint' of a colour is made by adding…", "White", "Black", "Grey", "Its complement", "Adding black gives a shade; adding grey gives a tone."),
        r("In perspective drawing, the horizon line is at…", "The viewer's eye level", "The top of the page", "The vanishing point of the sun", "The lowest object", "Look up or down from it and objects rise above or fall below it."),
        r("Chiaroscuro describes…", "Strong contrast between light and dark", "A style using only blue tones", "A drawing made with a single line", "Painting on wet plaster", "Caravaggio used it to make figures glow out of darkness."),
        r("Visual hierarchy is created mainly by…", "Size, colour, contrast and spacing", "Using only one font", "Adding more decoration", "Centering everything", "It tells the eye what to read first, second and third."),
        r("CEED stands for…", "Common Entrance Examination for Design", "Central Entrance Exam for Designers", "Creative Entrance and Evaluation of Design", "Combined Exam for Engineering Design", "It is held by IIT Bombay for M.Des and PhD admissions.", "CEED"),
        r("NID DAT stands for…", "Design Aptitude Test", "Drawing Ability Test", "Design Admission Task", "Digital Aptitude Test", "DAT has a Prelims and then a Mains with studio tests and an interview.", "NID"),
        r("NIFT was set up in which year?", "1986", "1961", "1975", "1999", "The National Institute of Fashion Technology is headquartered in New Delhi.", "NIFT"),
        r("In NIFT's entrance, GAT stands for…", "General Ability Test", "Graphic Aptitude Test", "Group Assessment Task", "Global Awareness Test", "The CAT is the Creative Ability Test.", "NIFT"),
    )

    val hard = listOf(
        r("Who wrote 'Ways of Seeing' (1972)?", "John Berger", "Susan Sontag", "Roland Barthes", "E. H. Gombrich", "Based on a BBC series. It changed how people think about images."),
        r("'The Elements of Typographic Style' was written by…", "Robert Bringhurst", "Jan Tschichold", "Ellen Lupton", "Erik Spiekermann", "It is a classic reference on setting type well."),
        r("'The Design of Everyday Things' was written by…", "Don Norman", "Jakob Nielsen", "Dieter Rams", "Bruno Munari", "It popularised ideas like affordance, signifiers and feedback."),
        r("The term 'affordance' was originally coined by…", "James J. Gibson", "Don Norman", "Herbert Simon", "Rudolf Arnheim", "Gibson used it in ecological psychology; Norman brought it to design."),
        r("Fitts's law says that the time to hit a target depends on…", "Its distance and its size", "Its colour and shape", "How many choices there are", "How familiar it looks", "That's why big, close buttons are easier to tap."),
        r("Hick's law says decision time grows with…", "The number of choices", "The size of the target", "The brightness of the screen", "The age of the user", "Fewer, clearer options make faster decisions."),
        r("Who was the first rector of the Ulm School of Design (HfG Ulm)?", "Max Bill", "Otl Aicher", "Dieter Rams", "Josef Albers", "Founded in 1953, it trained many designers behind Braun's look."),
        r("Otl Aicher is famous for the visual identity of which Olympics?", "Munich 1972", "Tokyo 1964", "Mexico City 1968", "Montreal 1976", "His pictograms and colour system set the standard for event design."),
        r("Helvetica was based on which earlier typeface?", "Akzidenz-Grotesk", "Univers", "Futura", "Gill Sans", "Miedinger and Hoffmann refined it at the Haas foundry."),
        r("The IIM Ahmedabad campus was designed by…", "Louis Kahn", "Le Corbusier", "B. V. Doshi", "Charles Correa", "Kahn's brick arches and courtyards are a landmark of modern Indian architecture.", "NID"),
        r("Balkrishna Doshi became India's first winner of which prize in 2018?", "Pritzker Prize", "Turner Prize", "Aga Khan Award", "Praemium Imperiale", "It is often called architecture's Nobel."),
        r("Kanchanjunga Apartments in Mumbai were designed by…", "Charles Correa", "B. V. Doshi", "Laurie Baker", "Raj Rewal", "Its sections interlock so each flat gets a view and a garden terrace."),
        r("Warli painting comes from…", "Maharashtra", "Bihar", "Odisha", "Rajasthan", "It uses white geometric figures on earthy walls: circles, triangles and lines.", "NID"),
        r("Madhubani painting originated in…", "Mithila, Bihar", "Odisha", "Andhra Pradesh", "Rajasthan", "Known for bold outlines, patterns and filled-in borders.", "NID"),
        r("Pattachitra is a traditional cloth scroll art of…", "Odisha", "Gujarat", "Kerala", "Punjab", "Painted with natural colours around Puri.", "NID"),
        r("Kalamkari, drawn or block-printed with natural dyes, is associated with…", "Andhra Pradesh", "Assam", "Punjab", "Kerala", "Centres include Srikalahasti and Machilipatnam.", "NID"),
        r("Ajrakh block printing is strongly associated with…", "Kutch, Gujarat", "Kerala", "Assam", "Punjab", "Deep indigo and madder red, with dense geometric patterns.", "NIFT"),
        r("Phulkari embroidery belongs to…", "Punjab", "Bengal", "Karnataka", "Assam", "Floral stitching in bright silk thread on cotton.", "NIFT"),
        r("Chikankari, a delicate white-thread embroidery, is from…", "Lucknow", "Jaipur", "Varanasi", "Bhopal", "It is traditionally done on light muslin or cotton.", "NIFT"),
        r("Bandhani is a tie-dye craft famous in…", "Gujarat and Rajasthan", "Kerala", "Mizoram", "Odisha", "Tiny knots are tied before dyeing to make dotted patterns.", "NIFT"),
        r("Gond art, with its dotted and lined fills, comes from…", "Madhya Pradesh", "Bihar", "Tamil Nadu", "Goa", "Animals and trees are filled with patterns made of dots and dashes.", "NID"),
        r("Kantha, running-stitch embroidery on layered cloth, is from…", "Bengal", "Kashmir", "Gujarat", "Tamil Nadu", "Old saris were stitched together into quilts and wraps.", "NIFT"),
        r("The Amul Girl advertising campaign (1966) was created by…", "Sylvester daCunha", "Alyque Padamsee", "Piyush Pandey", "R. K. Laxman", "Topical cartoon hoardings with the line 'Utterly Butterly Delicious'."),
        r("The Indian rupee symbol was designed by…", "D. Udaya Kumar", "Pradeep Mahajan", "Satish Gujral", "M. F. Husain", "It was selected in 2010 from thousands of entries."),
        r("The Ashoka Chakra on the Indian flag has how many spokes?", "24", "12", "16", "32", "Each spoke stands for an hour of the day in one popular reading."),
        r("In the NIFT B.Des entrance, the hands-on round using given materials is the…", "Situation Test", "Group Discussion", "Portfolio Review", "Drawing Marathon", "You make a model or composition from supplied materials under time pressure.", "NIFT"),
        r("The NID DAT is held in two stages: Prelims and…", "Mains", "Finals", "Portfolio Round", "Viva", "Mains has studio tests and an interview.", "NID"),
        r("In CEED, 'NAT' questions require…", "A typed numerical answer", "Choosing one option", "Choosing several options", "A short drawing", "Part A mixes NAT, MSQ and MCQ questions.", "CEED"),
        r("CEED Part B mainly tests…", "Drawing, design and creative ability", "Only GK", "Only maths", "Typing speed", "It checks sketching, visualisation, observation and design thinking.", "CEED"),
        r("Which painter is most associated with the use of strong chiaroscuro and tenebrism?", "Caravaggio", "Monet", "Matisse", "Seurat", "Dark backgrounds and spotlit figures are his signature."),
    )

    /** Mixes tiers by level. exams filters tagged questions. */
    fun pick(level: Int, exams: Set<String>, rnd: kotlin.random.Random, easy: List<Raw>): List<Raw> {
        fun ok(q: Raw) = q.tag.isEmpty() || q.tag in exams
        val e = easy.filter(::ok).shuffled(rnd)
        val m = mid.filter(::ok).shuffled(rnd)
        val h = hard.filter(::ok).shuffled(rnd)
        val (ne, nm, nh) = when {
            level <= 2 -> Triple(3, 1, 0)
            level <= 4 -> Triple(1, 2, 1)
            level <= 6 -> Triple(0, 2, 2)
            else -> Triple(0, 1, 3)
        }
        return (e.take(ne) + m.take(nm) + h.take(nh)).let { l -> if (l.size < 4) (l + (m + h + e).filter { it !in l }).take(4) else l }.shuffled(rnd)
    }
}
