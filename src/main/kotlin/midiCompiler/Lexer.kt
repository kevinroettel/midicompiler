package midiCompiler

class Lexer(input : MutableList<String>) {

    private val instructions = input
    private var position : Int = 0

    fun next(): Token {
        if (instructions.size == position) return Token.EOF

        return when (instructions[position++]) {
            "C1" -> Token.LPAREN
            "D1" -> Token.RPAREN
            "E1" -> Token.PLUS
            "F1" -> Token.MINUS
            "G1" -> Token.MUL
            "A1" -> when (peek()) {
                Token.EQUALS -> {
                    position++
                    Token.DOUBLE_EQUALS
                }
                Token.ARROW -> {
                    position++
                    Token.ARROW
                }
                else -> Token.EQUALS
            }
            "C2" -> Token.IF
            "D2" -> Token.THEN
            "E2" -> Token.ELSE
            "F2" -> Token.BACKSLASH
            "G2" -> Token.LET
            "A2" -> Token.IN
            "B2" -> Token.SEMICOLON
            "C3" -> Token.LOOP
            "D3" -> Token.LCURLPAREN
            "E3" -> Token.RCURLPAREN
            "F3" -> Token.LESS
            "G3" -> Token.GREATER
            "A3" -> Token.NOT
            "B3" -> Token.COLON

            "C4" -> Token.NUMBER_LIT(number())
            "C5" -> Token.IDENT(ident())
            "C6" -> Token.BOOLEAN_LIT(true)
            "D6" -> Token.BOOLEAN_LIT(false)
            else -> throw Exception("Unexpected Char")
        }
    }

    fun peek() : Token {
        if (instructions.size == position) return Token.EOF

        return when (instructions[position]) {
            "C1" -> Token.LPAREN
            "D1" -> Token.RPAREN
            "E1" -> Token.PLUS
            "F1" -> Token.MINUS
            "G1" -> Token.MUL
            "A1" -> Token.EQUALS
            "B1" -> Token.ARROW
            "C2" -> Token.IF
            "D2" -> Token.THEN
            "E2" -> Token.ELSE
            "F2" -> Token.BACKSLASH
            "G2" -> Token.LET
            "A2" -> Token.IN
            "B2" -> Token.SEMICOLON
            "C3" -> Token.LOOP
            "D3" -> Token.LCURLPAREN
            "E3" -> Token.RCURLPAREN
            "F3" -> Token.LESS
            "G3" -> Token.GREATER
            "A3" -> Token.NOT
            "B3" -> Token.COLON

            "C4", "D4", "E4", "F4", "G4", "A4", "B4" -> Token.NUMBER_CONTENT
            "C5", "D5", "E5", "F5", "G5", "A5", "B5" -> Token.IDENT_CONTENT
            "C6" -> Token.BOOLEAN_LIT(true)
            "D6" -> Token.BOOLEAN_LIT(false)
            else -> throw Exception("Unexpected Char")
        }
    }

    private fun number() : Int {
        var result = 0

        while (instructions[position] != "C4") {
            when (instructions[position++]) {
                "D4" -> result += 100000
                "E4" -> result +=  10000
                "F4" -> result +=   1000
                "G4" -> result +=    100
                "A4" -> result +=     10
                "B4" -> result +=      1
            }
        }

        position++
        return result
    }

    private fun ident() : String {
        var result = ""

        while (instructions[position] != "C5") {
            when (instructions[position++]) {
                "D5" -> result += "D"
                "E5" -> result += "E"
                "F5" -> result += "F"
                "G5" -> result += "G"
                "A5" -> result += "A"
                "B5" -> result += "B"
            }
        }

        position++
        return result
    }

}

fun test(input: MutableList<String>) {
    println("Lexing: $input")

    val lexer = Lexer(input)
    while (lexer.peek() != Token.EOF) {
        println(lexer.next())
    }

    println(lexer.next())
}
