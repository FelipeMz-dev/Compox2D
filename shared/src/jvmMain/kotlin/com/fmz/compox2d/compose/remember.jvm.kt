package com.fmz.compox2d.compose

import androidx.compose.runtime.Composable
import com.fmz.compox2d.engine.assets.SpriteManager
import com.fmz.compox2d.engine.audio.AudioSystem
import com.fmz.compox2d.input.sensor.SensorProcessor

@Composable
actual fun rememberAudioSystem(): AudioSystem {
    TODO("Not yet implemented")
}

@Composable
actual fun rememberSpriteManager(): SpriteManager {
    TODO("Not yet implemented")
}

@Composable
actual fun rememberSensorSystem(sensorProcessor: SensorProcessor): com.fmz.compox2d.engine.input.sensor.SensorSystem {
    TODO("Not yet implemented")
}
