package com.fmz.compox2d

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.fmz.compox2d.compose.GameSceneView
import com.fmz.compox2d.samples.breackout_sensor.MainScene

@Composable
fun App() {
    val scene = remember { MainScene() }
    GameSceneView(
        modifier = Modifier.fillMaxSize(),
        scene = scene,
        contentScale = ContentScale.FillBounds

    )
}
