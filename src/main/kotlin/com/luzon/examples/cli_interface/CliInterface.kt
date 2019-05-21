package com.luzon.examples.cli_interface

import com.luzon.Luzon
import com.luzon.examples.background_app.scriptsFolder
import com.luzon.reflection_engine.annotations.LzMethod
import com.luzon.runtime.Environment
import com.luzon.runtime.LzObject
import com.luzon.runtime.nullObject
import com.luzon.runtime.primitiveObject
import java.util.*

data class MenuItem(val name: String, val methodName: String)

val menuItems = mutableListOf<MenuItem>()
val scanner = Scanner(System.`in`)

object Methods {
    // Item Name, Method Name
    @LzMethod(args = ["String", "String"])
    fun addItem(args: List<Any>): LzObject {
        menuItems.add(MenuItem(args[0] as String, args[1] as String))

        return nullObject
    }

    @LzMethod(args = ["Any"])
    fun println(args: List<Any>): LzObject {
        println(args[0])

        return nullObject
    }

    @LzMethod(name = "println")
    fun printlnEmpty(args: List<Any>): LzObject {
        println()

        return nullObject
    }

    @LzMethod(args = ["Any"])
    fun print(args: List<Any>): LzObject {
        print(args[0])

        return nullObject
    }

    @LzMethod
    fun readLine(args: List<Any>) = primitiveObject(scanner.nextInt())
}

fun startCli() {
    Luzon.runFile("${scriptsFolder}CLI.lz")
//    Luzon.runFile("src\\main\\resources\\CLI.lz")
    Luzon.registerMethods(Methods::class)
    val cliClass = Environment.global.invokeFunction("CLI", emptyList())
    cliClass.invokeFunction("init", emptyList())

    do {
        println("Menu")
        println("0 - Exit Program")

        menuItems.forEachIndexed { i, item -> println("${i + 1} - ${item.name}") }

        println()
        print("Enter Option: ")
        val line = scanner.nextInt() - 1

        if (line >= 0 && line < menuItems.size) {
            cliClass.invokeFunction(menuItems[line].methodName, emptyList())
        }
    } while (line >= 0)
}

fun main() {
    startCli()
}