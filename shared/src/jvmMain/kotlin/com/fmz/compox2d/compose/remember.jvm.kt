package com.fmz.compox2d.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.fmz.compox2d.audio.AudioManagerImpl
import com.fmz.compox2d.engine.assets.SpriteManager
import com.fmz.compox2d.engine.audio.AudioSystem
import com.fmz.compox2d.engine.input.sensor.SensorSystem
import com.fmz.compox2d.input.sensor.SensorProcessor
import com.fmz.compox2d.render.ImageLoaderImpl

private class JvmSensorSystem : SensorSystem {
    override fun start() {}
    override fun stop() {}
}

@Composable
actual fun rememberAudioSystem(): AudioSystem {
    return remember { AudioManagerImpl() }
}

@Composable
actual fun rememberSpriteManager(): SpriteManager {
    return remember { SpriteManager(ImageLoaderImpl()) }
}

@Composable
actual fun rememberSensorSystem(sensorProcessor: SensorProcessor): SensorSystem {
    return remember { JvmSensorSystem() }
}
