package com.fmz.compox2d.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import com.fmz.compox2d.audio.AudioManagerImpl
import com.fmz.compox2d.engine.assets.SpriteManager
import com.fmz.compox2d.engine.audio.AudioSystem
import com.fmz.compox2d.engine.input.sensor.SensorManager
import com.fmz.compox2d.engine.input.sensor.SensorSystem
import com.fmz.compox2d.input.SensorInputAdapter
import com.fmz.compox2d.input.sensor.SensorProcessor
import com.fmz.compox2d.render.ImageLoaderImpl

@Composable
actual fun rememberAudioSystem(): AudioSystem {
    val context = LocalContext.current
    return AudioManagerImpl(context)
}

@Composable
actual fun rememberSpriteManager(): SpriteManager {
    val resources = LocalResources.current
    val spriteManager = remember { SpriteManager(ImageLoaderImpl(resources)) }
    return spriteManager
}

@Composable
actual fun rememberSensorSystem(sensorProcessor: SensorProcessor): SensorSystem {
    val context = LocalContext.current
    return SensorInputAdapter(context, sensorProcessor)
}