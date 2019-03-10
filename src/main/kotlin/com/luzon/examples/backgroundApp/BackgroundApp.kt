package com.luzon.examples.backgroundApp

import com.luzon.lexer.Tokenizer
import com.luzon.rd.RecursiveDescent
import com.luzon.rd.TokenRDStream
import com.luzon.rd.expression.accept
import com.luzon.reflectionEngine.ReflectionEngine
import com.luzon.reflectionEngine.annotations.LzMethod
import com.luzon.runtime.*
import com.luzon.runtime.visitors.ClassVisitor
import javafx.application.Application
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.javafx.JavaFx as Main

class BackgroundApp : Application() {
    var mouseHandler = Environment.global.invokeFunction("MouseHandler", emptyList())

    override fun start(primaryStage: Stage) {
        reloadScript()

        primaryStage.scene = Scene(createContent())
        primaryStage.show()
    }

    fun createContent(): Parent = Pane().apply {
        children += MenuBar().apply {
            menus += Menu("Scripts").apply {
                items += MenuItem("Reload Scripts").apply {
                    setOnAction { reloadScript() }
                }
            }
        }

        setPrefSize(500.0, 500.0)

        setOnMouseClicked(::mouseClick)
    }

    fun mouseClick(event: MouseEvent) {
        mouseHandler?.invokeFunction(
            "mouseClick",
            listOf(primitiveObject(event.x), primitiveObject(event.y))
        )
    }

    fun reloadScript() {
        GlobalScope.launch(Dispatchers.Main) {
            val time = System.currentTimeMillis()
            mouseHandler = withContext(Dispatchers.Default) {
                Environment.global.reset()
                ClassReferenceTable.reset()

                ReflectionEngine.registerClassMethods(Methods::class)

                val tokenStream = Tokenizer.fromFile("src\\main\\resources\\Test.lz").findTokens()
                val tree = RecursiveDescent(TokenRDStream(tokenStream)).parse()
                tree?.accept(ClassVisitor)

                Environment.global.invokeFunction("MouseHandler", emptyList())
            }
            println("RELOADED in ${System.currentTimeMillis() - time}ms")
        }
    }
}

object Methods {
    @LzMethod(args = ["Double"])
    fun println(env: Environment, args: List<LzObject>): LzObject {
        println(args[0].value)

        return nullObject
    }
}

fun main() {
    Application.launch(BackgroundApp::class.java)
}