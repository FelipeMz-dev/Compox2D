package com.fmz.compox2d.engine.input.sensor

import com.fmz.compox2d.engine.input.ListenerRegistry

class SensorManager {

    private val listeners = ListenerRegistry<SensorListener>()

    fun register(listener: SensorListener) {
        listeners.add(listener)
    }

    fun unregister(listener: SensorListener) {
        listeners.remove(listener)
    }

    fun dispatch(sensorEvent: SensorEvent) {
        listeners.forEach {
            it.onSensorEvent(sensorEvent)
        }
    }
}
