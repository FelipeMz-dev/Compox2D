package com.fmz.compox2d.input.sensor

import com.fmz.compox2d.engine.input.sensor.SensorManager

actual class SensorProcessor actual constructor(sensorManager: SensorManager) {
    actual val sensorManager: SensorManager = sensorManager
}