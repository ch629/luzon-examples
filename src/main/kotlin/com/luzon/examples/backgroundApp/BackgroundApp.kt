package com.luzon.examples.backgroundApp

import com.luzon.lexer.Tokenizer
import com.luzon.rd.RecursiveDescent
import com.luzon.rd.TokenRDStream
import com.luzon.rd.expression.accept
import com.luzon.reflectionEngine.ReflectionEngine
import com.luzon.reflectionEngine.annotations.LzMethod
import com.luzon.runtime.Environment
import com.luzon.runtime.LzObject
import com.luzon.runtime.nullObject
import com.luzon.runtime.primitiveObject
import com.luzon.runtime.visitors.ClassVisitor
import javafx.application.Application
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.stage.Stage

class BackgroundApp : Application() {
    val pane = Pane()
    val mouseHandler = Environment.global.invokeFunction("MouseHandler", emptyList())

    override fun start(primaryStage: Stage) {
        primaryStage.scene = Scene(createContent())
        primaryStage.show()
    }

    fun createContent(): Parent = pane.apply {
        setOnMouseClicked(::mouseClick)
    }

    fun mouseClick(event: MouseEvent) {
        mouseHandler?.invokeFunction(
            "mouseClick",
            listOf(primitiveObject(event.x), primitiveObject(event.y))
        )
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
    ReflectionEngine.registerClassMethods(Methods::class)

    val tokenStream = Tokenizer.fromFile("src\\main\\resources\\Test.lz").findTokens()
    val tree = RecursiveDescent(TokenRDStream(tokenStream)).parse()
    tree?.accept(ClassVisitor)

    Application.launch(BackgroundApp::class.java)
}