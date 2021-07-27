package midiCompiler

import kotlin.Exception

class Parser(private val tokens: Lexer) {
    fun parseExpr() : Expr {
        return parseBinary(0)
    }

    private fun parseBinary(minBP : Int) : Expr {
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

    private fun parseOperator() : Operator? {
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

    private fun bindingPower(op: Operator) : Pair<Int, Int> {
        return when(op) {
            Operator.Equals, Operator.Greater, Operator.Less -> 1 to 2
            Operator.Plus, Operator.Minus -> 3 to 4
            Operator.Multiply -> 5 to 6
        }
    }

    private fun parseApplication() : Expr {
        val func = parseAtom()
        val args: MutableList<Expr> = mutableListOf()
        while (true) {
            args += tryParseAtom() ?: break
        }
        return args.fold(func) { acc, arg -> Expr.Application(acc, arg) }
    }

    private fun parseAtom() : Expr {
        return tryParseAtom() ?: throw Exception("Expected expression, but saw unexpected token: ${tokens.peek()}")
    }

    private fun tryParseAtom() : Expr? {
        return when (tokens.peek()) {
            is Token.BOOLEAN_LIT -> parseBoolean()
            is Token.NUMBER_LIT, Token.NUMBER_CONTENT -> parseNumber()
            is Token.IDENT, Token.IDENT_CONTENT -> parseVar()
            is Token.IF -> parseIf()
            is Token.BACKSLASH -> parseLambda()
            is Token.LPAREN -> parseParenthesized()
            is Token.LET -> parseLet()
            is Token.LOOP -> parseLoop()
            is Token.NOT -> parseNot()
            is Token.COLON -> parseAssignment()
            is Token.EOF -> null
            else -> null
        }
    }

    private fun parseBoolean() : Expr {
        val t = expectNext<Token.BOOLEAN_LIT>("boolean literal")
        return Expr.Boolean(t.b)
    }

    private fun parseNumber() : Expr {
        val t = expectNext<Token.NUMBER_LIT>("number literal")
        return Expr.Number(t.n)
    }

    private fun parseVar() : Expr {
        val t = expectNext<Token.IDENT>("identifier")
        return Expr.Var(t.ident)
    }

    private fun parseParenthesized() : Expr {
        expectNext<Token.LPAREN>("(")
        val inner = parseExpr()
        expectNext<Token.RPAREN>(")")
        return inner
    }

    private fun parseLambda() : Expr {
        // \binder => body
        expectNext<Token.BACKSLASH>("\\")
        val binder = expectNext<Token.IDENT>("ident").ident
        expectNext<Token.ARROW>("=>")
        val body = parseExpr()
        return Expr.Lambda(binder, body)
    }

    private fun parseLet() : Expr {
        expectNext<Token.LET>("let")
        val binder = expectNext<Token.IDENT>("binder").ident
        expectNext<Token.EQUALS>("equals")
        val expr = parseExpr()
        expectNext<Token.IN>("in")
        val body = parseExpr()
        return Expr.Let(binder, expr, body)
    }

    // if true then 3 else 4
    private fun parseIf() : Expr.If {
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

    private fun parseNot() : Expr.Unary{
        expectNext<Token.NOT>( "!")
        return Expr.Unary(UnaryOperator.Not, parseExpr())
    }

    private fun parseAssignment() : Expr.Assignment{
        expectNext<Token.COLON>(":")
        val binder = expectNext<Token.IDENT>("varName").ident
        expectNext<Token.EQUALS>("=")
        val value = parseExpr()
        expectNext<Token.SEMICOLON>(";")
        return Expr.Assignment(binder,value,parseExpr())
    }

    private inline fun <reified A>expectNext(msg: String) : A {
        val next = tokens.next()
        if (next !is A) {
            throw Exception("Unexpected token: expected $msg, but saw $next")
        }
        return next
    }
}

fun testParser(input: MutableList<String>) {
    println("Parsing: $input")
    val lexer = Lexer(input)
    val parser = Parser(lexer)
    println(parser.parseExpr())
}
