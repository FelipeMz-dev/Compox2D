package com.fmz.compox2d.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.fmz.compox2d.engine.assets.SpriteManager
import com.fmz.compox2d.engine.audio.AudioSystem
import com.fmz.compox2d.engine.input.GameInput
import com.fmz.compox2d.engine.input.keyboard.KeyboardManager
import com.fmz.compox2d.engine.input.keyboard.KeyboardProcessor
import com.fmz.compox2d.engine.input.mouse.MouseManager
import com.fmz.compox2d.engine.input.mouse.MouseProcessor
import com.fmz.compox2d.engine.input.sensor.SensorManager
import com.fmz.compox2d.input.sensor.SensorProcessor
import com.fmz.compox2d.engine.input.sensor.SensorSystem
import com.fmz.compox2d.engine.input.touch.TouchManager
import com.fmz.compox2d.engine.input.touch.TouchProcessor

@Composable
expect fun rememberAudioSystem(): AudioSystem

@Composable
expect fun rememberSpriteManager(): SpriteManager

@Composable
expect fun rememberSensorSystem(sensorProcessor: SensorProcessor): SensorSystem

@Composable
fun rememberGameInput(
    sensorProcessor: SensorManager? = null,
    touchManager: TouchManager? = null,
    keyboardManager: KeyboardManager? = null,
    mouseManager: MouseManager? = null
): GameInput {
    val gameInput = remember {
        val builder = GameInput.Builder()
        sensorProcessor?.apply { builder.withSensor(SensorProcessor(this)) }
        touchManager?.apply { builder.withTouch(TouchProcessor(this)) }
        keyboardManager?.apply { builder.withKeyboard(KeyboardProcessor(this)) }
        mouseManager?.apply { builder.withMouse(MouseProcessor(this)) }
        builder.build()
    }
    return gameInput
}

@Composable
fun rememberGameInput(): GameInput {
    val gameInput = remember {
        GameInput.Builder()
            .withSensor(SensorProcessor(SensorManager()))
            .withTouch(TouchProcessor(TouchManager()))
            .withKeyboard(KeyboardProcessor(KeyboardManager()))
            .withMouse(MouseProcessor(MouseManager()))
            .build()
    }
    return gameInput
}