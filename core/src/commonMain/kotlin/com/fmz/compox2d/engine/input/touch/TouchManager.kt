package com.fmz.compox2d.engine.input.touch

import com.fmz.compox2d.engine.input.ListenerRegistry

class TouchManager {

    private val listeners = ListenerRegistry<TouchListener>()

    fun register(listener: TouchListener) {
        listeners.add(listener)
    }

    fun unregister(listener: TouchListener) {
        listeners.remove(listener)
    }

    fun dispatch(event: TouchEvent) {
        listeners.forEach { it.onTouchEvent(event) }
    }
}

