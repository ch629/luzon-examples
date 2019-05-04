package com.luzon.examples.calculator

import com.luzon.Luzon
import com.luzon.runtime.Environment
import com.luzon.runtime.primitiveObject

fun main() {
    Luzon.runFile("src\\main\\resources\\Calculator.lz")
    val fibClass = Environment.global.invokeFunction("Calculator", emptyList())

    println(fibClass.invokeFunction("fibonacci", listOf(primitiveObject(12))).value)
    println(fibClass.invokeFunction("factorial", listOf(primitiveObject(12))).value)
    println(fibClass.invokeFunction("sum", listOf(primitiveObject(12))).value)
}
