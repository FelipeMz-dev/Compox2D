package com.fmz.compox2d.input.sensor

import com.fmz.compox2d.engine.input.sensor.SensorManager

expect class SensorProcessor(sensorManager: SensorManager) {
    val sensorManager: SensorManager
}