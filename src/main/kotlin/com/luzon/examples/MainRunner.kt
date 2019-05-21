package com.luzon.examples

import com.luzon.examples.background_app.BackgroundApp
import com.luzon.examples.cli_interface.startCli
import com.luzon.examples.mini_repl.evaluateRepl
import com.luzon.examples.tree_viewer.TreeViewerApp
import javafx.application.Application

fun main(args: Array<String>) {
    if (args.size == 1) {
        when (args[0]) {
            "B" -> {
                Application.launch(BackgroundApp::class.java, *args)
            }
            "C" -> {
                startCli()
            }
            "T" -> {
                Application.launch(TreeViewerApp::class.java, *args)
            }
            "R" -> {
                evaluateRepl()
            }
        }
    }
}