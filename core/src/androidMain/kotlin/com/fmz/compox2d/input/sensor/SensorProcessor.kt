package com.fmz.compox2d.input.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import com.fmz.compox2d.engine.input.sensor.SensorManager
import com.fmz.compox2d.engine.math.Vec3
import com.fmz.compox2d.engine.math.round
import com.fmz.compox2d.engine.time.SmoothValue

actual class SensorProcessor actual constructor(
    actual val sensorManager: SensorManager
) {

    private val smoothAccelerometer = Array(3) { SmoothValue() }

    private val smoothGyroscope = Array(3) { SmoothValue() }

    fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {

            Sensor.TYPE_ACCELEROMETER -> {

                smoothAccelerometer.forEachIndexed { i, item ->
                    item.update(event.values[i])
                }

                val axis = Vec3(
                    x = smoothAccelerometer[0].value.round(1),
                    y = smoothAccelerometer[1].value.round(1),
                    z = smoothAccelerometer[2].value.round(1)
                )

                sensorManager.dispatch(com.fmz.compox2d.engine.input.sensor.SensorEvent.AccelerometerEvent(axis))
            }

            Sensor.TYPE_GYROSCOPE -> {
                smoothGyroscope.forEachIndexed { i, item ->
                    item.update(event.values[i])
                }

                val axis = Vec3(
                    x = smoothGyroscope[0].value.round(1),
                    y = smoothGyroscope[1].value.round(1),
                    z = smoothGyroscope[2].value.round(1)
                )

                sensorManager.dispatch(com.fmz.compox2d.engine.input.sensor.SensorEvent.GyroscopeEvent(axis))
            }
        }
    }
}