package com.luzon.examples.tree_viewer

import com.luzon.lexer.Tokenizer
import com.luzon.recursive_descent.RecursiveDescent
import com.luzon.recursive_descent.TokenRDStream
import com.luzon.recursive_descent.expression.accept
import javafx.application.Application
import javafx.scene.control.TreeItem
import javafx.scene.layout.HBox
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
    override val root = HBox()

    init {
        with(root) {
            val ast =
                RecursiveDescent(TokenRDStream(Tokenizer.fromFile("src\\main\\resources\\Test.lz").findTokens())).parse()!!

            treeview<Group> {
                root = TreeItem(ast.accept(TreeVisitor))
                root.isExpanded = true

                cellFormat { text = it.name }
                onUserSelect { println(it) }
                populate {
                    it.value.children
                }
            }
        }
    }
}