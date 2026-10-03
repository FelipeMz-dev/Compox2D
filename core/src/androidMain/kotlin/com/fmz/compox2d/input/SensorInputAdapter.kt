package com.fmz.compox2d.input

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.fmz.compox2d.engine.input.sensor.SensorSystem
import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.input.sensor.SensorProcessor

/**
 * Implementación específica de Android de SensorSystem.
 */
class SensorInputAdapter(
    context: Context,
    private val sensorProcessor: SensorProcessor
): SensorSystem, SensorEventListener {
    private val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val gyroscope = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private var acceleration = Vec2.Zero
    private var rotation = Vec2.Zero

    override fun start() {
        accelerometer?.let {
            manager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
        gyroscope?.let {
            manager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    override fun stop() {
        manager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                acceleration = Vec2(event.values[0], event.values[1])
            }
            Sensor.TYPE_GYROSCOPE -> {
                rotation = Vec2(event.values[0], event.values[1])
            }
        }
        sensorProcessor.onSensorChanged(event)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}