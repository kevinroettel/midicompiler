package midiCompiler

import kotlinx.collections.immutable.PersistentMap

sealed class Expr {
    data class Var(val name: String) : Expr()
    data class Lambda(val binder: String, val body: Expr) : Expr()
    data class Application(val func: Expr, val arg: Expr) : Expr()
    data class Number(val n: Int) : Expr()
    data class Boolean(val b: kotlin.Boolean) : Expr()
    data class Binary(val operator: Operator, val x: Expr, val y: Expr) : Expr()
    data class If(val condition: Expr, val thenBranch: Expr, val elseBranch: Expr) : Expr()
    data class Let(val binder: String, val expr: Expr, val body: Expr) : Expr()
    data class Loop(val iterator: Int, val returnValue: String, val body: Expr) : Expr()
    data class Unary(val op: UnaryOperator, val x: Expr) : Expr()
    data class Assignment( val binder: String, val expr: Expr, val restCode: Expr) : Expr()
}

enum class Operator {
    Equals, Plus, Minus, Multiply, Greater, Less
}
enum class UnaryOperator{
    Not
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
    object COLON : Token()
    object SEMICOLON : Token()

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

typealias Env = PersistentMap<String, Value>

sealed class Value {
    data class Number(val n: Int) : Value()
    data class Closure(val env: Env, val binder: String, val body: Expr) : Value()
    data class Boolean(val b: kotlin.Boolean) : Value()
}