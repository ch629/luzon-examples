package com.luzon.examples.backgroundApp

import com.luzon.Luzon
import com.luzon.reflection_engine.annotations.LzMethod
import com.luzon.runtime.Environment
import com.luzon.runtime.LzObject
import com.luzon.runtime.nullObject
import com.luzon.runtime.primitiveObject
import javafx.application.Application
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornadofx.*

class NewBackgroundApp : App(NewBackgroundView::class) {
    override fun start(stage: Stage) {
        stage.isResizable = false
        super.start(stage)
    }
}

class NewBackgroundView : View() {
    private var mouseHandler = nullObject

    init {
        reloadScript()
    }

    override val root = vbox {
        setPrefSize(500.0, 500.0)

        menubar {
            menu("Scripts") {
                item("Reload Scripts") {
                    setOnAction { reloadScript() }
                }
            }
        }

        imageview("file:///C:\\Users\\Zurro\\Documents\\ShareX\\Screenshots\\2018-09\\1.png") {
            fitToParentSize()

            fitWidth = prefWidth
            isPreserveRatio = true

            setOnMouseClicked(::mouseClick)
        }
    }

    private fun reloadScript() {
        GlobalScope.launch(Dispatchers.Main) {
            val timer = Timer()
            mouseHandler = withContext(Dispatchers.Default) {
                Luzon.resetLanguage()
                Luzon.registerMethods(Methods::class)

                Luzon.runFile("src\\main\\resources\\Test.lz")

                Environment.global.invokeFunction("MouseHandler", emptyList())
            }
            println(timer.timeString("RELOADED"))
        }
    }

    private fun mouseClick(event: MouseEvent) {
        mouseHandler.invokeFunction(
            "mouseClick",
            listOf(primitiveObject(event.x), primitiveObject(event.y))
        )
    }
}

object Methods {
    @LzMethod(args = ["Any"])
    fun println(args: List<Any>): LzObject {
        println(args[0])

        return nullObject
    }
}

class Timer {
    private val startTime = System.currentTimeMillis()

    fun time() = System.currentTimeMillis() - startTime
    fun timeString(text: String) = "$text in ${time()}ms"
}

fun main(args: Array<String>) {
    Application.launch(NewBackgroundApp::class.java, *args)
}