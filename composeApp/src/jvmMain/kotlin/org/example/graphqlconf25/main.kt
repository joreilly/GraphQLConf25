package org.example.graphqlconf25

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "graphqlconf25",
    ) {
        App()
    }
}