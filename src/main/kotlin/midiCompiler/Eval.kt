package midiCompiler

fun eval(env: Env, expr: Expr) : Value {
    return when (expr) {
        is Expr.Number  -> Value.Number(expr.n)
        is Expr.Boolean -> Value.Boolean(expr.b)
        is Expr.Var     -> env[expr.name] ?: throw Exception("${expr.name} is not defined.")
        is Expr.Lambda  -> Value.Closure(env, expr.binder, expr.body)
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
        is Expr.Unary -> {
            when(expr.op) {
                UnaryOperator.Not -> {
                    val evalExpr = eval(env, expr.x) as? Value.Boolean ?: throw Exception("Not a boolean")
                    Value.Boolean(!evalExpr.b)
                }
            }
        }
        is Expr.Assignment -> {
            val evaledExpr = eval(env, expr.expr)
            val nestedEnv = env.put(expr.binder, evaledExpr)
            eval(nestedEnv, expr.restCode)
        }
    }
}

fun equalsValue(x: Value, y: Value): Value {
    return if (x is Value.Number && y is Value.Number){
        Value.Boolean(x.n == y.n)
    } else if(x is Value.Boolean && y is Value.Boolean){
        Value.Boolean(x.b == y.b)
    } else throw Exception("Can't compare $x and $y, they're  either not comparable or they're neither a number nor a boolean")
}

fun greaterThanValue(x : Value, y : Value) : Value {
    val v1n = x as? Value.Number ?: throw Exception("Can't compare $x, it's not a number")
    val v2n = y as? Value.Number ?: throw Exception("Can't compare $y, it's not a number")
    return Value.Boolean(v1n.n > v2n.n)
}
fun lessThanValue(x : Value, y : Value) : Value {
    val v1n = x as? Value.Number ?: throw Exception("Can't compare $x, it's not a number")
    val v2n = y as? Value.Number ?: throw Exception("Can't compare $y, it's not a number")
    return Value.Boolean(v1n.n < v2n.n)
}

fun evalBinaryNumber(v1 : Value, v2 : Value, f : (Int, Int) -> Int) : Value {
    val v1n = v1 as? Value.Number ?: throw Exception("Can't use a binary operation on $v1, it's not a number")
    val v2n = v2 as? Value.Number ?: throw Exception("Can't use a binary operation on $v2, it's not a number")
    return Value.Number(f(v1n.n, v2n.n))
}

fun evalMidi(expr : MutableList<String>, environments : Env) {
    try {
        println(eval(environments, Parser(Lexer(expr)).parseExpr()))
    } catch (ex: Exception) {
        println("Failed to  with: ${ex.message}")
    }
}