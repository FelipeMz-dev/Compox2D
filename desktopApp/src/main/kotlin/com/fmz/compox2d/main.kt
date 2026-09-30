package com.fmz.compox2d

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.fmz.compox2d.compose.GameSceneView
import com.fmz.compox2d.samples.breackout_sensor.MainScene

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Compox2D Breakout"
    ) {
        val scene = remember { MainScene() }
        GameSceneView(
            modifier = Modifier.fillMaxSize(),
            scene = scene,
            contentScale = ContentScale.FillBounds

        )
    }
}