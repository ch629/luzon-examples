package com.luzon.examples.tree_viewer

import com.luzon.lexer.Tokenizer
import com.luzon.recursive_descent.RecursiveDescent
import com.luzon.recursive_descent.TokenRDStream
import com.luzon.recursive_descent.expression.accept
import javafx.application.Application
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import tornadofx.*

fun main(args: Array<String>) {
    Application.launch(TreeViewerApp::class.java, *args)
}

class TreeViewerApp : App(TreeViewView::class) {
    override fun start(stage: Stage) {
        stage.isResizable = false
        super.start(stage)
    }
}

class TreeViewView : View() {
    private val treeView: TreeView<Group>
    override val root = VBox()

    init {
        with(root) {
            menubar {
                menu("Scripts") {
                    item("Load Script") {
                        setOnAction {
                            val file = chooseFile(
                                "Choose a Script",
                                arrayOf(FileChooser.ExtensionFilter("Luzon Files (*.lz)", "*.lz"))
                            ).firstOrNull()

                            if (file != null)
                                loadScript(file.absolutePath)
                        }
                    }
                }
            }

            treeView = treeview {
                root = getNodes("src\\main\\resources\\Test.lz")
                root.isExpanded = true

                cellFormat { text = it.name }
                onUserSelect { println(it) }
                populate {
                    it.value.children
                }
            }
        }
    }

    fun getNodes(path: String): TreeItem<Group> {
        return TreeItem(
            RecursiveDescent(TokenRDStream(Tokenizer.fromFile(path).findTokens())).parse()!!.accept(
                TreeVisitor
            )
        )
    }

    fun loadScript(path: String) {
        with(treeView) {
            root = getNodes(path)
            root.isExpanded = true

            populate {
                it.value.children
            }
        }
    }
}