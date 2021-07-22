package midiCompiler

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import kotlin.Exception

sealed class Expr {
    data class Var(val name: String) : Expr()
    data class Lambda(val binder: String, val body: Expr) : Expr()
    data class Application(val func: Expr, val arg: Expr) : Expr()
    data class Number(val n: Int) : Expr()
    data class Boolean(val b: kotlin.Boolean) : Expr()
    data class Binary(val operator: Operator, val x: Expr, val y: Expr) : Expr()
    data class If(val condition: Expr, val thenBranch: Expr, val elseBranch: Expr) : Expr()
    data class Let(val binder: String, val expr: Expr, val body: Expr) : Expr()
    data class Loop(val iterator : Int, val returnValue : String, val body: Expr) : Expr()
}

enum class Operator {
    Equals, Plus, Minus, Multiply, Greater, Less
}

sealed class Token {
    override fun toString() : String {
        return this.javaClass.simpleName
    }

    // Keywords
    object IF : Token()
    object THEN : Token()
    object ELSE : Token()
    object LET : Token()
    object IN : Token()
    object LOOP : Token()


    // Symbols
    object LPAREN : Token()
    object RPAREN : Token()
    object LCURLPAREN : Token()
    object RCURLPAREN : Token()
    object BACKSLASH : Token()
    object ARROW : Token()
    object EQUALS : Token()

    // Operatoren
    object PLUS : Token()
    object MINUS : Token()
    object MUL : Token()
    object DOUBLE_EQUALS : Token()
    object GREATER : Token()
    object LESS : Token()

    object NOT : Token()

    data class IDENT(val ident: String) : Token()

    // Literals
    data class BOOLEAN_LIT(val b: Boolean) : Token()
    data class NUMBER_LIT(val n: Int) : Token()
    object NUMBER_CONTENT : Token()
    object IDENT_CONTENT : Token()

    // Control midiCompiler.Token
    object EOF : Token()
}

//arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

class Lexer(input : MutableList<String>) {

    private val instructions = input
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
            "G2" -> Token.LET
            "A2" -> Token.IN
            "C3" -> Token.LOOP
            "D3" -> Token.LCURLPAREN
            "E3" -> Token.RCURLPAREN
            "F3" -> Token.LESS
            "G3" -> Token.GREATER
            "A3" -> Token.NOT

            "C4" -> Token.NUMBER_LIT(number())
            "C5" -> Token.IDENT(ident())
            "C6" -> Token.BOOLEAN_LIT(true)
            "D6" -> Token.BOOLEAN_LIT(false)
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
            "G2" -> Token.LET
            "A2" -> Token.IN
            "C3" -> Token.LOOP
            "D3" -> Token.LCURLPAREN
            "E3" -> Token.RCURLPAREN
            "F3" -> Token.LESS
            "G3" -> Token.GREATER
            "A3" -> Token.NOT

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
            if(op != Operator.Equals) tokens.next()
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
            Token.EQUALS -> if(tokens.next() == Token.DOUBLE_EQUALS) return Operator.Equals else null
            Token.GREATER -> Operator.Greater
            Token.LESS -> Operator.Less
            //midiCompiler.Token.DOUBLE_EQUALS -> midiCompiler.Operator.Equals
            else -> null
        }
    }

    fun bindingPower(op: Operator): Pair<Int, Int> {
        return when(op) {
            Operator.Equals, Operator.Greater, Operator.Less -> 1 to 2
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
            is Token.LET -> parseLet()
            is Token.LOOP -> parseLoop()
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

    private fun parseLet(): Expr {
        expectNext<Token.LET>("let")
        val binder = expectNext<Token.IDENT>("binder").ident
        expectNext<Token.EQUALS>("equals")
        val expr = parseExpr()
        expectNext<Token.IN>("in")
        val body = parseExpr()
        return Expr.Let(binder, expr, body)
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

    private fun parseLoop() : Expr.Loop{
        expectNext<Token.LOOP>( "loop")
        val iterator = expectNext<Token.NUMBER_LIT>("iterator").n
        val returnValue = expectNext<Token.IDENT>("returnValue").ident
        expectNext<Token.LCURLPAREN>("{")
        val body = parseExpr()
        expectNext<Token.RCURLPAREN>("}")
        return Expr.Loop(iterator, returnValue, body)
    }

    private inline fun <reified A>expectNext(msg: String): A {
        val next = tokens.next()
        if (next !is A) {
            throw Exception("Unexpected token: expected $msg, but saw $next")
        }
        return next
    }
}

typealias Env = PersistentMap<String, Value>

sealed class Value {
    data class Number(val n: Int) : Value()
    data class Closure(val env: Env, val binder: String, val body: Expr) : Value()
    data class Boolean(val b: kotlin.Boolean) : Value()
}

fun eval(env: Env, expr: Expr): Value {
    return when (expr) {
        is Expr.Number -> Value.Number(expr.n)
        is Expr.Boolean -> Value.Boolean(expr.b)
        is Expr.Var -> env[expr.name] ?: throw Exception("${expr.name} is not defined.")
        is Expr.Lambda -> Value.Closure(env, expr.binder, expr.body)
        is Expr.Let -> {
            val evaledExpr = eval(env, expr.expr)
            val nestedEnv = env.put(expr.binder, evaledExpr)
            eval(nestedEnv, expr.body)
        }
        is Expr.Application -> {
            val evaledFunc = eval(env, expr.func)
            val evaledArg = eval(env, expr.arg)
            when (evaledFunc) {
                is Value.Closure -> {
                    val newEnv = evaledFunc.env.put(evaledFunc.binder, evaledArg)
                    eval(newEnv, evaledFunc.body)
                }
                else -> throw Exception("$evaledFunc is not a function")
            }
        }
        is Expr.If -> {
            val cond = eval(env, expr.condition) as? Value.Boolean ?: throw Exception("Not a boolean")
            if (cond.b) {
                eval(env, expr.thenBranch)
            } else {
                eval(env, expr.elseBranch)
            }
        }
        is Expr.Binary -> {
            when (expr.operator) {
                Operator.Equals -> equalsValue(eval(env, expr.x), eval(env, expr.y))
                Operator.Multiply ->
                    evalBinaryNumber(eval(env, expr.x), eval(env, expr.y)) { x, y -> x * y }
                Operator.Plus ->
                    evalBinaryNumber(eval(env, expr.x), eval(env, expr.y)) { x, y -> x + y }
                Operator.Minus ->
                    evalBinaryNumber(eval(env, expr.x), eval(env, expr.y)) { x, y -> x - y }
                Operator.Greater -> greaterThanValue(eval(env,expr.x), eval(env,expr.y))
                Operator.Less -> lessThanValue(eval(env,expr.x), eval(env,expr.y))
            }
        }
        is Expr.Loop -> {
            var evalExpr = eval(env,expr.body)
            var newEnv = env.put(expr.returnValue,evalExpr)
            for (i in 3 .. expr.iterator)
            {
                evalExpr = eval(newEnv,expr.body)
                newEnv = env.put(expr.returnValue,evalExpr)
            }
            eval(newEnv,expr.body)
        }
    }


}

fun equalsValue(x: Value, y: Value): Value {
    val v1n = x
    val v2n = y
    if (v1n is Value.Number && v2n is Value.Number){
        return Value.Boolean(v1n.n == v2n.n)
    } else if(v1n is Value.Boolean && v2n is Value.Boolean){
        return Value.Boolean(v1n.b == v2n.b)
    } else throw Exception("Can't compare $x and $y, they're  either not comparable or they're neither a number nor a boolean")
}

fun greaterThanValue(x : Value, y: Value) : Value{
    val v1n = x as? Value.Number ?: throw Exception("Can't compare $x, it's not a number")
    val v2n = y as? Value.Number ?: throw Exception("Can't compare $y, it's not a number")
    return Value.Boolean(v1n.n > v2n.n)
}
fun lessThanValue(x : Value, y: Value) : Value{
    val v1n = x as? Value.Number ?: throw Exception("Can't compare $x, it's not a number")
    val v2n = y as? Value.Number ?: throw Exception("Can't compare $y, it's not a number")
    return Value.Boolean(v1n.n < v2n.n)
}

fun evalBinaryNumber(v1: Value, v2: Value, f: (Int, Int) -> Int): Value {
    val v1n = v1 as? Value.Number ?: throw Exception("Can't use a binary operation on $v1, it's not a number")
    val v2n = v2 as? Value.Number ?: throw Exception("Can't use a binary operation on $v2, it's not a number")
    return Value.Number(f(v1n.n, v2n.n))
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

fun testEval(expr: MutableList<String>) {
    try {
        println(eval(persistentHashMapOf("G" to z), Parser(Lexer(expr)).parseExpr()))
    } catch (ex: Exception) {
        println("Failed to  with: ${ex.message}")
    }
}
fun testEval2(expr: MutableList<String>){
    println(eval(persistentHashMapOf("E" to Value.Boolean(false),"G" to z),Parser(Lexer(expr)).parseExpr()))
}


