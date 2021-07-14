import java.lang.NumberFormatException

fun getNotes(string : String) : MutableList<String> {
    val notes : MutableList<String> = ArrayList()
    val chars = string.split(" ").toMutableList()

    for (character in chars) {
        var temp = when (character) {
            "(" -> mutableListOf("C1")
            ")" -> mutableListOf("D1")
            "+" -> mutableListOf("E1")
            "-" -> mutableListOf("F1")
            "*" -> mutableListOf("G1")
            "=" -> mutableListOf("A1")
            "==" -> mutableListOf("A1", "A1")
            "=>" -> mutableListOf("A1", "B1")
            "IF" -> mutableListOf("C2")
            "THEN" -> mutableListOf("D2")
            "ELSE" -> mutableListOf("E2")
            "LET" -> mutableListOf("G2")
            "IN" -> mutableListOf("A2")
            "\\" -> mutableListOf("F2")
            else -> parse(character)
        }

        notes.addAll(temp)
    }

    return notes
}

fun parse(character : String) : List<String> {
    val notes : MutableList<String> = ArrayList()

    try {
        val number = character.toInt()
        notes.addAll(parseNumber(number))
    } catch (e : NumberFormatException) {
        notes.addAll(parseIdent(character))
    }

    return notes
}

fun parseNumber(number : Int) : List<String> {
    val notes : MutableList<String> = ArrayList()

    val numberStrings = number.toString().split("").toMutableList()
    numberStrings.removeAt(0)
    numberStrings.removeAt(numberStrings.size - 1)

    notes.add("C4")
    var digit = numberStrings.size

    for (numberString in numberStrings) {
        val num = numberString.toInt()

        for (i in 1..num) {
            notes.add(when (digit) {
                1 -> "B4"
                2 -> "A4"
                3 -> "G4"
                4 -> "F4"
                5 -> "E4"
                6 -> "D4"
                else -> ""
            })
        }

        digit--
    }

    notes.add("C4")
    return notes
}

fun parseIdent(identifier : String) : List<String> {
    val notes : MutableList<String> = ArrayList()

    val identifierContent = identifier.split("").toMutableList()
    identifierContent.removeAt(0)
    identifierContent.removeAt(identifierContent.size - 1)

    notes.add("C5")
    for (char in identifierContent) {
        notes.add(when (char) {
            "D" -> "D5"
            "E" -> "E5"
            "F" -> "F5"
            "G" -> "G5"
            "A" -> "A5"
            "B" -> "B5"
            else -> ""
        })
    }
    notes.add("C5")

    return notes
}

fun main() {
    val string = "3 + 43"
    val notes : List<String> = getNotes(string)
    println(notes)
}

