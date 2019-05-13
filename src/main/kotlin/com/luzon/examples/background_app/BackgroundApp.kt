package com.luzon.examples.background_app

import com.luzon.Luzon
import com.luzon.reflection_engine.annotations.LzMethod
import com.luzon.runtime.Environment
import com.luzon.runtime.LzObject
import com.luzon.runtime.nullObject
import com.luzon.runtime.primitiveObject
import javafx.application.Application
import javafx.geometry.Rectangle2D
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornadofx.*
import java.io.File
import kotlin.random.Random

val scriptsFolder =
    "${File(BackgroundApp::class.java.protectionDomain.codeSource.location.toURI()).toPath().parent}\\scripts\\"

class BackgroundApp : App(BackgroundView::class) {
    override fun start(stage: Stage) {
        stage.isResizable = false
        super.start(stage)
    }
}

class BackgroundView : View() {
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

        canvas(this.prefWidth, this.prefHeight) {
            ClickRegionManager.graphicsContext = graphicsContext2D

            setOnMouseClicked(::mouseClick)
        }
    }

    private fun reloadScript() {
        if (ClickRegionManager.graphicsContext != null) {
            with(ClickRegionManager.graphicsContext!!) {
                clearRect(0.0, 0.0, 500.0, 500.0)
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            val timer = Timer()
            mouseHandler = withContext(Dispatchers.Default) {
                ClickRegionManager.reset()
                Luzon.resetLanguage()
                Luzon.registerMethods(Methods::class)

                Luzon.runFile("$scriptsFolder\\MouseHandler.lz")

                Environment.global.invokeFunction("MouseHandler", emptyList())
            }

            ClickRegionManager.mouseHandler = mouseHandler

            mouseHandler.invokeFunction("init", emptyList())

            println(timer.timeString("RELOADED"))
        }
    }

    private fun mouseClick(event: MouseEvent) {
        mouseHandler.invokeFunction(
            "mouseClick",
            listOf(primitiveObject(event.x), primitiveObject(event.y))
        )

        mouseHandler.invokeFunction("regionClick", listOf(primitiveObject(ClickRegionManager.click(event.x, event.y))))
    }
}

object ClickRegionManager {
    private val regions = mutableListOf<Rectangle2D>()
    var mouseHandler = nullObject
    var graphicsContext: GraphicsContext? = null
    val rand = Random(System.currentTimeMillis())

    fun reset() = regions.clear()

    fun addRegion(x: Double, y: Double, width: Double, height: Double) {
        val rect = Rectangle2D(x, y, width, height)
        regions += rect

        graphicsContext?.apply {
            fill = Color.rgb(rand.nextInt(0, 255), rand.nextInt(0, 255), rand.nextInt(0, 255))

            fillRect(x, y, width, height)
        }
    }

    fun click(x: Double, y: Double) = regions.indexOfFirst { it.contains(x, y) }
}

object Methods {
    @LzMethod(args = ["Any"])
    fun println(args: List<Any>): LzObject {
        println(args[0])

        return nullObject
    }

    @LzMethod(args = ["Double", "Double", "Double", "Double"])
    fun addRegion(args: List<Any>): LzObject {
        ClickRegionManager.addRegion(args[0] as Double, args[1] as Double, args[2] as Double, args[3] as Double)

        return nullObject
    }

    @LzMethod(args = ["Double", "Double", "Double", "Double"])
    fun fillRect(args: List<Any>): LzObject {
        ClickRegionManager.graphicsContext?.fillRect(
            args[0] as Double,
            args[1] as Double,
            args[2] as Double,
            args[3] as Double
        )

        return nullObject
    }
}

class Timer {
    private val startTime = System.currentTimeMillis()

    fun time() = System.currentTimeMillis() - startTime
    fun timeString(text: String) = "$text in ${time()}ms"
}

fun main(args: Array<String>) {
    Application.launch(BackgroundApp::class.java, *args)
}