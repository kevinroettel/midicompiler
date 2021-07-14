import java.lang.Exception

sealed class Expr {
    data class Var(val name: String) : Expr()
    data class Lambda(val binder: String, val body: Expr) : Expr()
    data class Application(val func: Expr, val arg: Expr) : Expr()
    data class Number(val n: Int) : Expr()
    data class Boolean(val b: kotlin.Boolean) : Expr()
    data class Binary(val operator: Operator, val x: Expr, val y: Expr) : Expr()
    data class If(val condition: Expr, val thenBranch: Expr, val elseBranch: Expr) : Expr()
}

enum class Operator {
    Equals, Plus, Minus, Multiply
}

sealed class Token {
    override fun toString() : String {
        return this.javaClass.simpleName
    }

    // Keywords
    object IF : Token()
    object THEN : Token()
    object ELSE : Token()

    // Symbols
    object LPAREN : Token()
    object RPAREN : Token()
    object BACKSLASH : Token()
    object ARROW : Token()
    object EQUALS : Token()

    // Operatoren
    object PLUS : Token()
    object MINUS : Token()
    object MUL : Token()
    object DOUBLE_EQUALS : Token()

    data class IDENT(val ident: String) : Token()

    // Literals
    data class BOOLEAN_LIT(val b: Boolean) : Token()
    data class NUMBER_LIT(val n: Int) : Token()
    object NUMBER_CONTENT : Token()
    object IDENT_CONTENT : Token()

    // Control Token
    object EOF : Token()
}

//arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

class Lexer(input : MutableList<String>) {

    private val instructions = input;
    private var position : Int = 0

    public fun next(): Token {
        if (instructions.size == position) return Token.EOF

        return when (val c = instructions[position++]) {
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

            "C4" -> Token.NUMBER_LIT(number())
            "C5" -> Token.IDENT(ident())
            else -> throw Exception("Unexpected Char")
        }
    }

    public fun peek() : Token {
        if (instructions.size == position) return Token.EOF

        return when (val c = instructions[position]) {
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

            "C4", "D4", "E4", "F4", "G4", "A4", "B4" -> Token.NUMBER_CONTENT
            "C5", "D5", "E5", "F5", "G5", "A5", "B5" -> Token.IDENT_CONTENT
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

class Parser(val tokens: Lexer) {
    fun parseExpr() : Expr {
        return parseBinary(0)
    }

    fun parseBinary(minBP : Int) : Expr {
        var lhs : Expr = parseApplication()
        while (true) {
            val op = parseOperator() ?: break
            val (leftBP, rightBP) = bindingPower(op)
            if (leftBP < minBP) break
            tokens.next()
            val rhs = parseBinary(rightBP)
            lhs = Expr.Binary(op, lhs, rhs)
        }

        return lhs
    }

    private fun parseOperator(): Operator? {
        return when(tokens.peek()) {
            Token.PLUS -> Operator.Plus
            Token.MINUS -> Operator.Minus
            Token.MUL -> Operator.Multiply
            Token.DOUBLE_EQUALS -> Operator.Equals
            else -> null
        }
    }

    fun bindingPower(op: Operator): Pair<Int, Int> {
        return when(op) {
            Operator.Equals -> 1 to 2
            Operator.Plus, Operator.Minus -> 3 to 4
            Operator.Multiply -> 5 to 6
        }
    }

    fun parseApplication(): Expr {
        val func = parseAtom()
        val args: MutableList<Expr> = mutableListOf()
        while (true) {
            args += tryParseAtom() ?: break
        }
        return args.fold(func) { acc, arg -> Expr.Application(acc, arg) }
    }

    fun parseAtom(): Expr {
        return tryParseAtom() ?: throw Exception("Expected expression, but saw unexpected token: ${tokens.peek()}")
    }

    fun tryParseAtom(): Expr? {
        return when (val t = tokens.peek()) {
            is Token.BOOLEAN_LIT -> parseBoolean()
            is Token.NUMBER_LIT, Token.NUMBER_CONTENT -> parseNumber()
            is Token.IDENT, Token.IDENT_CONTENT -> parseVar()
            is Token.IF -> parseIf()
            is Token.BACKSLASH -> parseLambda()
            is Token.LPAREN -> parseParenthesized()
            is Token.EOF -> null
            else -> null
        }
    }

    private fun parseBoolean(): Expr {
        val t = expectNext<Token.BOOLEAN_LIT>("boolean literal")
        return Expr.Boolean(t.b)
    }

    private fun parseNumber(): Expr {
        val t = expectNext<Token.NUMBER_LIT>("number literal")
        return Expr.Number(t.n)
    }

    private fun parseVar(): Expr {
        val t = expectNext<Token.IDENT>("identifier")
        return Expr.Var(t.ident)
    }

    private fun parseParenthesized(): Expr {
        expectNext<Token.LPAREN>("(")
        val inner = parseExpr()
        expectNext<Token.RPAREN>(")")
        return inner
    }

    private fun parseLambda(): Expr {
        // \binder => body
        expectNext<Token.BACKSLASH>("\\")
        val binder = expectNext<Token.IDENT>("ident").ident
        expectNext<Token.ARROW>("=>")
        val body = parseExpr()
        return Expr.Lambda(binder, body)
    }

    // if true then 3 else 4
    private fun parseIf(): Expr.If {
        expectNext<Token.IF>("if")
        val condition = parseExpr()
        expectNext<Token.THEN>("then")
        val thenBranch = parseExpr()
        expectNext<Token.ELSE>("else")
        val elseBranch = parseExpr()
        return Expr.If(condition, thenBranch, elseBranch)
    }

    private inline fun <reified A>expectNext(msg: String): A {
        val next = tokens.next()
        if (next !is A) {
            throw Exception("Unexpected token: expected $msg, but saw $next")
        }
        return next
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

fun testParser(input: MutableList<String>) {
    println("Parsing: $input")
    val lexer = Lexer(input)
    val parser = Parser(lexer)
    println(parser.parseExpr())
}
