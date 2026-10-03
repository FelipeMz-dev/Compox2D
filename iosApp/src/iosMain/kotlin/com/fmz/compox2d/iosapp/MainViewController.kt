package com.fmz.compox2d.iosapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.fmz.compox2d.compose.GameSceneView
import com.fmz.compox2d.samples.breackout_sensor.MainScene
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    val scene = remember { MainScene() }
    GameSceneView(
        modifier = Modifier.fillMaxSize(),
        scene = scene
    )
}
