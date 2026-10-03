package com.fmz.compox2d.engine.input.sensor

import com.fmz.compox2d.engine.math.Vec3

sealed interface SensorEvent {

    data class AccelerometerEvent(
        val value: Vec3
    ) : SensorEvent

    data class GyroscopeEvent(
        val value: Vec3
    ) : SensorEvent
}
