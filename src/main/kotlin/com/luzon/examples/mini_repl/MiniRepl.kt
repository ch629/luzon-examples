package com.luzon.examples.mini_repl

import com.luzon.lexer.Tokenizer
import com.luzon.recursive_descent.expression.accept
import com.luzon.recursive_descent.parseExpression
import com.luzon.runtime.visitors.ExpressionVisitor
import java.util.*

fun evaluate(code: String): Double {
    val tokens = Tokenizer(code).findTokens()
    val expr = parseExpression(tokens)

    val obj = expr?.accept(ExpressionVisitor)
    return obj?.value?.asDouble() ?: -1.0
}

private fun Any.asDouble(): Double = when (this) {
    is Int -> toDouble()
    is Float -> toDouble()
    is Double -> this
    else -> -1.0
}

fun evaluateRepl() {
    val scanner = Scanner(System.`in`)

    do {
        print("Enter Expression (x to exit): ")
        val inp = scanner.nextLine() + " "

        println("Result: ${evaluate(inp)}")
    } while (inp != "x")
}

fun main() {
    evaluateRepl()
}