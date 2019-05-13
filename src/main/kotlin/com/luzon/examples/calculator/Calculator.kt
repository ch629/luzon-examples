package com.luzon.examples.calculator

import com.luzon.Luzon
import com.luzon.examples.background_app.scriptsFolder
import com.luzon.runtime.Environment
import com.luzon.runtime.primitiveObject
import java.util.*

fun calculator() {
    print("Enter Number: ")
    val num = Scanner(System.`in`).nextInt()

    Luzon.runFile("${scriptsFolder}Calculator.lz")
    val fibClass = Environment.global.invokeFunction("Calculator", emptyList())

    println("Fibonacci of $num: " + fibClass.invokeFunction("fibonacci", listOf(primitiveObject(num))).value)
    println("Factorial of $num: " + fibClass.invokeFunction("factorial", listOf(primitiveObject(num))).value)
    println("Sum of 1 to $num: " + fibClass.invokeFunction("sum", listOf(primitiveObject(num))).value)
}
